package us.ppgs.cam;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.Data;
import us.ppgs.config.ConfigFacility;
import us.ppgs.util.info.KeyValue;

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
	@RequestMapping("goSubDir")
	@ResponseBody
	public CamRet goSubDir(String subDir, Model m) {
		CamRet cr = new CamRet();
		cr.setSubDir(subDir);

		File baseDir = new File(ConfigFacility.get("camDir", "C:\\temp"));
		File baseSubDir = new File(baseDir, subDir);
		String[] files = baseSubDir.list();
		
		// are there any images
		for (String file : files) {
			File f = new File(baseSubDir, file);
			if (f.isDirectory()) {
				cr.getDirs().add(file);
			}
			else if (file.toLowerCase().endsWith(".jpg")) {
				cr.setHasImages(true);
			}
		}

		Collections.sort(cr.getDirs(), sc);
		cr.getBreadCrumbs().add(new KeyValue("", "root"));
		String crumbPath = "";
		for (String crumb : subDir.split("/")) {
			crumbPath = (crumbPath.length() == 0 ? crumb : crumbPath + "/" + crumb);
			cr.getBreadCrumbs().add(new KeyValue(crumbPath, crumb));
		}
		
		return cr;
	}
	
	
	public @Data class CamRet {
		private List<KeyValue> breadCrumbs = new ArrayList<KeyValue>();
		private String subDir = "";
		private List<String> dirs = new ArrayList<String>();
		private boolean hasImages = false;
	}
	
	StringCompare sc = new StringCompare();
	public static class StringCompare implements Comparator<String> {

		@Override
		public int compare(String s1, String s2) {
			return s1.compareToIgnoreCase(s2);
		}
	}
}

