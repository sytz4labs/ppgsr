package us.ppgs.config.servlet;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import us.ppgs.config.ConfigFacility;
import us.ppgs.config.dao.ConfigException;
import us.ppgs.config.dao.ConfigPair;
import us.ppgs.security.LoginInfo;

@Controller
@RequestMapping("/config")
public class ConfigServlet {

	@GetMapping("")
	public String indexb() {
		return "redirect:/config/";
	}

	@GetMapping("/")
	public String indexSlash() {
		return "/html/config.html";
	}

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value="/configs")
    @ResponseBody
	public Collection<ConfigPair> configs() {

		return ConfigFacility.getCacheMap().values();
	}
    
    public static class SaveRequest {
    	private int id;
    	private String type;
    	private String value;
    	
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
    	
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/save")
    @ResponseBody
	public Collection<ConfigPair> editHandler(@RequestBody SaveRequest saver) throws IOException {

		LoginInfo.getLoginInfoAjax(LoginInfo.SESS_LEVEL_REMEMBER_ME);

		// saving a value so need to update a div
		try {
			if (saver.getId() == -1) {
				ConfigFacility.add(saver.getValue());
			}
			else {
				if (saver.getType().equals("name")) {
					if (saver.getValue().length() == 0) { // renaming to a 0 length name is a delete
						ConfigFacility.delete(saver.getId());
					}
					else {
						ConfigFacility.rename(saver.getId(), saver.getValue());
					}
				}
				else {
					ConfigFacility.save(saver.getId(), saver.getValue());
				}
			}
		}
		catch (ConfigException e) {
			e.printStackTrace();
		}
		
		return configs();
	}
}
