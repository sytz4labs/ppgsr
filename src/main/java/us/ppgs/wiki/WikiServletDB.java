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
    public FileInfo pageService(@RequestBody FileInfo req) {

		return wikiDao.getFile(req.getFile()).get(0);
	}
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/ewiki/save")
    @ResponseBody
	public FileInfo save(@RequestBody FileInfo req) throws Exception {

		wikiDao.saveFile(req.getFile(), req.getPage(), req.getContents());

		return pageService(req);
	}
}
