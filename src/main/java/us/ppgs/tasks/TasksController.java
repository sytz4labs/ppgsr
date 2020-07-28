package us.ppgs.tasks;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.Data;

@Controller
@RequestMapping("/tasks")
public class TasksController {
	
	@Autowired
	private TasksDAO tDao;
	
	@RequestMapping("")
	public String indexb() {
		return "redirect:/tasks/";
	}

	@RequestMapping("/")
	public String indexSlash() {
		return "tasks/index";
	}
	
	public static @Data class TaskReq {
		private String cmd;
		private int id;
		private String val;
	}
	
	@RequestMapping(value="/get", method=RequestMethod.POST)
	@ResponseBody
	public List<TaskInfo> get(@RequestBody TaskReq req) {

		switch(req.getCmd()) {
		case "new":
			tDao.newTask(req.getVal());
			break;
		case "area":
		case "priority":
		case "task":
		case "benefit":
			tDao.updateTask(req.getId(), req.getCmd(), req.getVal());
			break;
		}
		
		return tDao.get();
	}
}

