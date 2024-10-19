package us.ppgs;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PSSController {

	@GetMapping("/")
	public String indexSlash() {
		return "/html/index.html";
	}
}
