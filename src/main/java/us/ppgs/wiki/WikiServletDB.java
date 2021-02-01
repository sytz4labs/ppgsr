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
    public List<PageInfo> pageService(@RequestBody PageInfo req) {
    	
    	var pages = wikiDao.getPage(req.getPage());
    	if (pages.size() == 0) {
    		pages.add(new PageInfo(-1, req.getPage(), "", 0, "Undefined"));
    	}

		return pages; 
	}
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/ewiki/savePage")
    @ResponseBody
	public List<PageInfo> savePage(@RequestBody PageInfo req) throws Exception {

		wikiDao.savePageContents(req);

		return pageService(req);
	}
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/ewiki/savePageTab")
    @ResponseBody
	public List<PageInfo> savePageTab(@RequestBody PageInfo req) throws Exception {

		wikiDao.savePageTab(req);

		return pageService(req);
	}
}
