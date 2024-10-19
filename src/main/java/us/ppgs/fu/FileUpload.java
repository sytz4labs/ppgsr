package us.ppgs.fu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.Data;
import us.ppgs.config.ConfigFacility;
import us.ppgs.util.info.RestResponse;

@Controller
@RequestMapping("/fu")
public class FileUpload {

	public static @Data class FileInfo implements Comparable<FileInfo> {
		
		private String name;
		private long length;

		public FileInfo(File f) {
			name = f.getName();
			length = f.length();
		}
		
		@Override
		public int compareTo(FileInfo o) {
			return name.compareToIgnoreCase(o.name);
		}
	}
	
    @PreAuthorize("hasRole('ADMIN')")
	@GetMapping("")
	public String indexb() {
		return "redirect:/fu/";
	}

    @PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/")
	public String indexSlash() {
		return "/html/fu.html";
	}

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/files")
    @ResponseBody
    public RestResponse handleFormUpload() {
    	
    	return new RestResponse(getFiles(), "", "");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/go")
    @ResponseBody
    public RestResponse handleFormUpload(@RequestParam("file") MultipartFile pFile, RedirectAttributes ra) {

    	String success = null;
    	String error = null;
    	if (!pFile.isEmpty()) {
            try {
				pFile.transferTo(new File(new File(ConfigFacility.get("fuDir", "/tmp")), pFile.getOriginalFilename()));
	            // store the bytes somewhere
				success = pFile.getOriginalFilename() + " uploaded";
			}
            catch (IOException e) {
				error = "error " + e.getMessage();
			}
        }
    	else {
    		error = "File is empty";
    	}

    	return new RestResponse(getFiles(), success, error);
    }
    
    private List<FileInfo> getFiles() {
    	
    	List<FileInfo> fl = new ArrayList<FileInfo>();
    	for (File f : new File(ConfigFacility.get("fuDir", "/tmp")).listFiles()) {
    		if (f.isFile()) {
    			fl.add(new FileInfo(f));
    		}
    	}

    	Collections.sort(fl);
    	
    	return fl;
    }
}
