package us.ppgs.security;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginSecurityController {

	//Spring Security sees this :
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(@RequestParam(value = "error", required = false) String error, Model m) {

		m.addAttribute("title", "A title in login security controller");
		if (error != null) {
			m.addAttribute("error", "Invalid username and password!");
		}
		
		return "security/login";
	}
}
