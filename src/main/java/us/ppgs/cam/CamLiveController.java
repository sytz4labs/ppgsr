package us.ppgs.cam;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/camlive")
public class CamLiveController {
	
	@GetMapping("")
	public String indexb() {
		return "redirect:/camlive/";
	}

	@GetMapping("/")
	public String indexSlash() {
		return "/html/camlive.html";
	}
}

