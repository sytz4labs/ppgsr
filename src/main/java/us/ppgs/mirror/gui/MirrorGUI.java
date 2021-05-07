package us.ppgs.mirror.gui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.InternalResourceView;

import us.ppgs.config.ConfigFacility;
import us.ppgs.config.dao.ConfigException;
import us.ppgs.mirror.client.MirrorClient;
import us.ppgs.mirror.gui.MirrorRestResponse.Log;
import us.ppgs.mirror.info.MirrorClientInfo;
import us.ppgs.mirror.info.MirrorClientInfo.MirrorDirInfo;
import us.ppgs.mirror.info.MirrorCmdRequest;

@Controller
@RequestMapping("/mirror")
public class MirrorGUI {

	@Autowired
	private MirrorClient client;
	
	@RequestMapping("")
	public String index() {
		return "redirect:/mirror/";
	}

    @PreAuthorize("hasRole('ADMIN')")
	@RequestMapping("/")
	public View indexSlash() {
		return new InternalResourceView("/mirror/index.html");
	}

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value="/dirs")
    @ResponseBody
	public MirrorRestResponse dirs(String error) throws ConfigException {

    	MirrorClientInfo ci = MirrorClientInfo.getConfig();
		return new MirrorRestResponse(ci, null, error, null);
	}

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value="/logs")
    @ResponseBody
	public MirrorRestResponse logs(String logName) throws ConfigException, IOException {

    	MirrorClientInfo ci = MirrorClientInfo.getConfig();
    	File dir = new File(ci.getLogDir());
    	List<Log> logs = new ArrayList<Log>();
    	if (logName == null) {
        	for (String logListName : dir.list()) {
        		File f = new File(dir, logListName);
        		logs.add(new Log(logListName, f.length(), null));
        	}
    	}
    	else {
    		File f = new File(dir, logName);
    		logs.add(new Log(logName, f.length(), Files.readAllLines(Paths.get(ci.getLogDir(), logName))));
    	}
		return new MirrorRestResponse(ci, null, null, logs);
	}

//    @PreAuthorize("hasRole('ADMIN')")
//    @RequestMapping(value="/log")
//    @ResponseBody
//	public MirrorRestResponse log(String logName) throws ConfigException, IOException {
//
//    	MirrorClientInfo ci = MirrorClientInfo.getConfig();
//		return new MirrorRestResponse(ci, null, null, new Log(logName, Files.readAllLines(Paths.get(ci.getLogDir(), logName))));
//	}
//

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value="/save", method=RequestMethod.POST)
    @ResponseBody
	public MirrorRestResponse save(@RequestBody MirrorCmdRequest mcr) throws ConfigException  {

    	MirrorClientInfo mci = MirrorClientInfo.getConfig();

    	Map<String, MirrorDirInfo> mdiMap = new HashMap<String, MirrorDirInfo>();
		for (MirrorDirInfo mdi : mci.getDirs()) {
			mdiMap.put(mdi.getName(), mdi);
		}

		MirrorDirInfo mdi = null;
		switch (mcr.getCmd()) {
		case "newDir":
			if (mdiMap.containsKey(mcr.getNewValue())) {
				return dirs("Duplicate name " + mcr.getNewValue() + " rejected.");
			}
			else {
				mdi = new MirrorDirInfo();
				mdi.setName(mcr.getNewValue());
				mci.getDirs().add(mdi);
			}
			break;
		case "renDir":
			if (mdiMap.containsKey(mcr.getNewValue())) {
				return dirs("Duplicate name " + mcr.getNewValue() + " rejected.");
			}
			else {
				mdi = mdiMap.get(mcr.getName());
				if (mcr.getNewValue().trim().length() == 0) {
					mci.getDirs().remove(mdi);
				}
				else {
					mdi.setName(mcr.getNewValue());
				}
			}
			break;
		case "setDir":
			mdi = mdiMap.get(mcr.getName());
			mdi.setDirectory(mcr.getNewValue());
			break;
		case "srvUrl":
			mci.setServerUrl(mcr.getNewValue());
			break;
		case "srvDir":
			mci.setServerDir(mcr.getNewValue());
			break;
		case "logDir":
			mci.setLogDir(mcr.getNewValue());
			break;
		}
    	
    	Collections.sort(mci.getDirs(), new Comparator<MirrorDirInfo>() {
			@Override
			public int compare(MirrorDirInfo o1, MirrorDirInfo o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
    	ConfigFacility.save(MirrorClientInfo.MIRROR_CLIENT_CONFIG_NAME, mci);

    	return dirs(null);
    }

    @PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "/getdir", method = RequestMethod.POST, produces="application/json")
	@ResponseBody
	public DirInfo postDir(@RequestBody DirReqInfo dir) {

		DirInfo di = new DirInfo();
		di.setPath(dir.getDir() == null || dir.getDir().length() == 0 ? "c:/" : dir.getDir());
		
		File root = new File(di.getPath());
		if (!root.exists()) {
			di.setPath("c:/");
			root = new File(di.getPath());
		}
		
		for (File f : root.listFiles()) {
			if (f.isDirectory()) {
				di.getSubDirs().add(f.getName());
			}
			else {
				di.getFiles().add(f.getName());
			}
		}
		
		return di;
	}

	@RequestMapping("/kick")
	public @ResponseBody MirrorRestResponse launch() throws InterruptedException, ConfigException {

		boolean started = client.schedule();
		return new MirrorRestResponse(null, started ? "Kicked" : null, started ? null : "Already Running", null);
	}
	
	private String dots = ".";
	
	@RequestMapping("/isRunning")
	public @ResponseBody MirrorRestResponse isRunning() throws InterruptedException, ConfigException {

		if (client.isRunning()) {
			dots = dots + " .";
			if (dots.length() > 20) {
				dots = ".";
			}
			return new MirrorRestResponse(null, "Running " + dots, null, null);
		}
		else {
			dots = ".";
			return new MirrorRestResponse(null, "Idle", null, null);
		}
	}
}