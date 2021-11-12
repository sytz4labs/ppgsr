package us.ppgs.cam;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/camlive")
public class CamLiveController {
	
	@RequestMapping("")
	public String indexb() {
		return "redirect:/camlive/";
	}

	@RequestMapping("/")
	public String indexSlash() {
		return "camlive/index";
	}
}

