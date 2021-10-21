package us.ppgs.clock;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Data;
import us.ppgs.clock.ClockController.Clock;
import us.ppgs.config.ConfigFacility;
import us.ppgs.config.dao.ConfigException;

@Component
public class ClockModel {

	@Data
	public static class ClockStorage {
		private List<Clock> clocks = new ArrayList<Clock>(); 
	}
	
	public List<Clock> getClocks(String clockSetId) throws ConfigException {
		
		if (clockSetId == null) return null;
		
		var clocks = ConfigFacility.get("clocks." + clockSetId, ClockStorage.class);
		
		if (clocks == null) {
			clocks = new ClockStorage();
			ConfigFacility.add("clocks." + clockSetId, clocks);
		}

		return clocks.clocks;
	}

	public void saveClocks(String clockSetId, List<Clock> clockSetIdData) {
		var cs = new ClockStorage();
		cs.setClocks(clockSetIdData);
		ConfigFacility.save("clocks." + clockSetId, cs);
	}
}
