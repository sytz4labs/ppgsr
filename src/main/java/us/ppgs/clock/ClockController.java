package us.ppgs.clock;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/clock")
public class ClockController {

	@RequestMapping(value={"", "/"})
	public String indexb() {
		return "redirect:/clock/default";
	}

	@RequestMapping("/{id}")
	public String indexSlash(@PathVariable(required = false) String id) {
		System.out.printf("'%s'%n", id);
		return "clock/index";
	}
}
