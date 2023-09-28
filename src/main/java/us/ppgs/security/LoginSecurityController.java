package us.ppgs.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginSecurityController {
	
	@GetMapping("/userInfo")
	@ResponseBody
	public LoginInfo userInfo() {

		return LoginInfo.getLoginInfo(LoginInfo.SESS_LEVEL_REMEMBER_ME);
	}
}
