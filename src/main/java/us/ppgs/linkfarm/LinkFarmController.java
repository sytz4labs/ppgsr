package us.ppgs.linkfarm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import us.ppgs.config.ConfigFacility;
import us.ppgs.config.dao.ConfigException;
import us.ppgs.linkfarm.info.LFRequest;
import us.ppgs.linkfarm.info.LinkPage;
import us.ppgs.util.info.RestResponse;

@Controller
@RequestMapping("/lf")
public class LinkFarmController {
	
	@Autowired
	private LinkModel lm;

	@GetMapping("")
	public String indexb() {
		return "redirect:/lf/";
	}

	@GetMapping("/")
	public String indexSlash() {
		return "/html/linkfarm.html";
	}

	@GetMapping("/{page}")
	public String indexSlashPage(@PathVariable("page") String pPage) {
		return "/html/linkfarm.html";
	}

    @GetMapping("/links")
    @ResponseBody
	public RestResponse links(@RequestParam("page") String pPage) throws ConfigException {
		return new RestResponse(lm.getObjLinks(pPage), "", "");
	}

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update")
    @ResponseBody
	public RestResponse update(@RequestBody LFRequest lr) throws ConfigException {

		LinkPage lp = ConfigFacility.get("lf." + lr.getPage(), LinkPage.class);
		
		switch(lr.getCmd()) {
		case "newGroup":
			lm.grpNew(lp, lr.getColOrd(), lr.getName());
			break;
		case "newGroupLink":
			lm.grpNewLink(lp, lr.getColOrd(), lr.getGrpOrd());
			break;
		case "renameGroup":
			lm.grpRename(lp, lr.getColOrd(), lr.getGrpOrd(), lr.getName());
			break;
		case "removeGroup":
			lm.grpRemove(lp, lr.getColOrd(), lr.getGrpOrd());
			break;
		case "moveGroupUp":
			lm.grpUp(lp, lr.getColOrd(), lr.getGrpOrd());
			break;
		case "moveGroupDown":
			lm.grpDown(lp, lr.getColOrd(), lr.getGrpOrd());
			break;
		case "moveGroupLeft":
			lm.grpLeft(lp, lr.getColOrd(), lr.getGrpOrd());
			break;
		case "moveGroupRight":
			lm.grpRight(lp, lr.getColOrd(), lr.getGrpOrd());
			break;
		case "moveLinkUp":
			lm.linkUp(lp, lr.getColOrd(), lr.getGrpOrd(), lr.getLinkOrd());
			break;
		case "moveLinkDown":
			lm.linkDown(lp, lr.getColOrd(), lr.getGrpOrd(), lr.getLinkOrd());
			break;
		case "saveLink":
			lm.saveLink(lp, lr.getColOrd(), lr.getGrpOrd(), lr.getLinkOrd(), lr.getName(), lr.getUrl(), lr.getNewColGrp());
			break;
		case "removeLink":
			lm.linkRemove(lp, lr.getColOrd(), lr.getGrpOrd(), lr.getLinkOrd());
			break;
		}

		lm.validatePage(lp);
		
		ConfigFacility.save("lf." + lr.getPage(), lp);
    	
		return links(lr.getPage());
	}
}