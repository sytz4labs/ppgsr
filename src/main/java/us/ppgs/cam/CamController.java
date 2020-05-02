package us.ppgs.cam;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.Data;
import us.ppgs.config.ConfigFacility;

@Controller
@RequestMapping("/cam")
public class CamController {
	
	@RequestMapping("")
	public String indexb() {
		return "redirect:/cam/";
	}

	@RequestMapping("/")
	public String indexSlash() {
		return "cam/index";
	}

    @PreAuthorize("hasRole('ADMIN')")
	@RequestMapping("goSubDir")
	@ResponseBody
	public CamRet goSubDir(String subDir) {
		File baseDir = new File(ConfigFacility.get("camDir", "C:\\temp"));

		CamRet cr = new CamRet();
		cr.setSubDir(subDir);
		
		String crumbPath = "";
		LinkedList<String> crumbs = new LinkedList<String>(Arrays.asList(subDir.split("/")));
		if (crumbs.get(0).length() != 0) {
			// add the root crumb
			crumbs.addFirst("");
		}
		
		for (String crumb : crumbs) {
			crumbPath = (crumbPath.length() == 0 ? crumb : crumbPath + "/" + crumb);

			Dir d = new Dir();
			d.setPath(crumbPath);
			d.setName(crumb.length() == 0 ? "root" : crumb);

			File baseSubDir = new File(baseDir, crumbPath);

			for (String file : baseSubDir.list()) {
				File f = new File(baseSubDir, file);
				if (f.isDirectory()) {
					d.getDirs().add(file);
				}
				else if (file.toLowerCase().endsWith(".jpg")) {
					d.setHasImages(true);
				}
			}
			
			cr.getBreadCrumbs().add(d);
		}
		
		return cr;
	}
	
    public @Data class Dir {
    	private String path;
    	private String name;
    	private List<String> dirs = new ArrayList<String>();
		private boolean hasImages = false;
    }
	
	public @Data class CamRet {
		private List<Dir> breadCrumbs = new ArrayList<Dir>();
		private String subDir = "";
//		private List<String> dirs = new ArrayList<String>();
//		private boolean hasImages = false;
	}
	
	StringCompare sc = new StringCompare();
	public static class StringCompare implements Comparator<String> {

		@Override
		public int compare(String s1, String s2) {
			return s1.compareToIgnoreCase(s2);
		}
	}
}

