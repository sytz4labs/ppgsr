package us.ppgs;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PSSController {

	@RequestMapping("")
	public String index() {
		return "redirect:/";
	}

	@RequestMapping("/")
	public String indexSlash(Model m) {
		return "index/index";
	}
}
