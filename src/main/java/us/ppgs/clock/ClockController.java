package us.ppgs.clock;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import us.ppgs.config.dao.ConfigException;

@Controller
@RequestMapping("/clock")
public class ClockController {

	@Autowired
	ClockModel clkMdl;
	
	@Data
	@AllArgsConstructor
	public static class ZoneNode {
		private String subName;
		private ZoneId zone;
		private String offset;
		private Map<String, ZoneNode> subZones;
	}
	
	private ZoneNode rootZone = new ZoneNode(null, null, null, null);
	
	public ClockController() {
		for (var zoneId : ZoneId.getAvailableZoneIds()) {
			addZone(rootZone, ZoneId.of(zoneId), zoneId);
		}
	}
	
	private void addZone(ZoneNode zn, ZoneId zoneId2, String name) {
		var zoneIdSplit = name.split("/");
		var zoneId = zoneIdSplit[0];
		
		var subZones = zn.getSubZones();
		if (subZones == null) {
			subZones = new TreeMap<String, ZoneNode>();
			zn.setSubZones(subZones);
		}

		var subZone = subZones.get(zoneId);
		if (subZone == null) {
			subZone = new ZoneNode(zoneId, null, null, null);
			subZones.put(zoneId, subZone);
		}
		
		if (zoneIdSplit.length == 1) {
			subZone.setZone(zoneId2);
			subZone.setOffset(zoneId2.getRules().getOffset(Instant.now()).toString());
		}
		else {
			addZone(subZone, zoneId2, name.substring(name.indexOf('/') + 1));
		}
	}
	
	@GetMapping(value={"", "/"})
	public String indexb() {
		return "redirect:/clock/default";
	}

	@GetMapping("/{id}")
	public String indexSlash(@PathVariable(required = false) String id) {
		System.out.printf("'%s'%n", id);
		return "/html/clock.html";
	}

	@Data
	public static class ZoneReq {
		private String cmd;
		private String[] path;
	}

	@Data
	@AllArgsConstructor
	public static class ZoneRes {
		private List<ZoneCrumb> crumbs;
	}

	@Data
	@AllArgsConstructor
	public static class ZoneCrumb {
		private List<String> path;
		private List<SubZone> subZones;
	}

	@Data
	@AllArgsConstructor
	public static class SubZone {
		private boolean parent;
		private String name;
		private String path;
	}

	@PostMapping("/api/zoneReq")
	@ResponseBody
	public ZoneRes apiZoneReq(@RequestBody ZoneReq req) {

		var zoneCrumbs = new ArrayList<ZoneCrumb>();
		var startZone = rootZone;
		var curPath = new ArrayList<String>();
		
		for (var nextPath : req.getPath()) {
			curPath.add(nextPath);
			startZone = nextPath.length() == 0 ? startZone : startZone.subZones.get(nextPath);
			var subZones = new ArrayList<SubZone>();
			
			var zoneCrumb = new ZoneCrumb(new ArrayList<>(curPath), subZones);
			zoneCrumbs.add(zoneCrumb);
			
			for (var zone : startZone.subZones.entrySet()) {
				var zoneId = zone.getValue().getZone();
				var zonePath = zoneId == null ? null : zoneId.getId();
				subZones.add(new SubZone(zone.getValue().getSubZones() != null, zone.getKey(), zonePath));
			}
		}
		
		return new ZoneRes(zoneCrumbs);
	}

	@Data
	public static class ClockReq {
		private String cmd;
		private String clockSetId;
		private int index;
		private String zone;
	}

	@Data
	@AllArgsConstructor
	public static class ClockRes {
		private List<Clock> clocks;
	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Clock {
		private String zone;
		private String timeShort;
		private String timeLong;
	}

	@PostMapping("/api/clockReq")
	@ResponseBody
	public ClockRes apiClockReq(@RequestBody ClockReq req) throws ConfigException {
		
		var clockSetIdData = clkMdl.getClocks(req.getClockSetId());
		
		switch (req.getCmd()) {
		case "add":
			clockSetIdData.add(new Clock(req.getZone(), null, null));
			clkMdl.saveClocks(req.getClockSetId(), clockSetIdData);
			break;

		case "set":
			var clock = clockSetIdData.get(req.getIndex());
			clock.setZone(req.getZone());
			clkMdl.saveClocks(req.getClockSetId(), clockSetIdData);
			break;

		default:
			break;
		}
		
		// get current times for each tz
		var sdfShort = new SimpleDateFormat("HH:mm");
		var sdfLong = new SimpleDateFormat("MM/dd hh:mm a z");
		var now = new Date();
		for (var clock : clockSetIdData) {
			var tz = TimeZone.getTimeZone(clock.getZone());
			sdfShort.setTimeZone(tz);
			sdfLong.setTimeZone(tz);
			clock.setTimeShort(sdfShort.format(now));
			clock.setTimeLong(sdfLong.format(now));
		}
		
		return new ClockRes(clockSetIdData);
	}
}
