package us.ppgs;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PSSController {

	@GetMapping("")
	public String index() {
		return "redirect:/";
	}

	@GetMapping("/")
	public String indexSlash(Model m) {
		return "/html/index.html";
	}
}
