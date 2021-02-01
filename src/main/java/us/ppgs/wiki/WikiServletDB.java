package us.ppgs.wiki;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WikiServletDB {
	
	@Autowired
	private WikiDAO wikiDao;
	
	@GetMapping("/ewiki")
	public String indexb() {
		return "redirect:/ewiki/";
	}

    @PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/ewiki/")
	public String indexSlash() {
		return "ewiki/index";
	}

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/ewiki/{page}")
	public String page(@PathVariable String page, Model m) {
		return "ewiki/index";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/ewiki/get")
    @ResponseBody
    public List<FileInfo> pageService(@RequestBody FileInfo req) {
    	
    	var pages = wikiDao.getFile(req.getFile());
    	if (pages.size() == 0) {
    		pages.add(new FileInfo(-1, req.getFile(), "", 0, "Undefined"));
    	}

		return pages; 
	}
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/ewiki/saveFile")
    @ResponseBody
	public List<FileInfo> saveFile(@RequestBody FileInfo req) throws Exception {

		wikiDao.saveFileContents(req);

		return pageService(req);
	}
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/ewiki/saveFileTab")
    @ResponseBody
	public List<FileInfo> saveFileTab(@RequestBody FileInfo req) throws Exception {

		wikiDao.saveFileTab(req);

		return pageService(req);
	}
}
