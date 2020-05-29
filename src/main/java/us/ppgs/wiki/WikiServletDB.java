package us.ppgs.wiki;

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

	@GetMapping("/ewiki/")
	public String indexSlash() {
		return "ewiki/index";
	}

    @GetMapping("/ewiki/{page}")
	public String page(@PathVariable String page, Model m) {
		return "ewiki/index";
    }

    @PostMapping("/ewiki/get")
    @ResponseBody
    public WikiReq pageService(@RequestBody WikiReq req) {

    	WikiReq res = new WikiReq();

		FileInfo fi = wikiDao.getFile(req.getFileName());
		res.setFileName(req.getFileName());
		if (fi == null) {
			res.setFileText("Newish");
		}
		else {
			res.setFileText(new String(fi.contents));
		}

		return res;
	}
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/ewiki/save")
    @ResponseBody
	public WikiReq save(@RequestBody WikiReq req) throws Exception {

		wikiDao.saveFile(req.getFileName(), req.getFileText());

		return pageService(req);
	}
}
