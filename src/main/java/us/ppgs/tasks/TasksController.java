package us.ppgs.tasks;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.AllArgsConstructor;
import lombok.Data;

@Controller
@RequestMapping("/tasks")
public class TasksController {
	
	@Autowired
	private TasksDAO tDao;
	
	@GetMapping("")
	public String indexb() {
		return "redirect:/tasks/";
	}

	@GetMapping("/")
	public String indexSlash() {
		return "/html/tasks.html";
	}
	
	public static @Data class TaskReq {
		private String cmd;
		private int id;
		private String val;
		private String area;
		private String task;
	}
	
	@Data
	@AllArgsConstructor
	public static class Res {
		private Set<String> areas;
		private List<TaskInfo> tasks;
	}
	
	@PostMapping("/get")
	@ResponseBody
	public Res get(@RequestBody TaskReq req) {

		switch(req.getCmd()) {
		case "new":
			tDao.newTask(req.getArea(), req.getTask());
			break;
		case "area":
		case "priority":
		case "task":
		case "benefit":
			tDao.updateTask(req.getId(), req.getCmd(), req.getVal());
			break;
		}
		
		return new Res(tDao.getAreas(), tDao.getTasks());
	}
}

