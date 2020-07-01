package us.ppgs.tasks;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public @Data class TaskInfo {

	private int id;
	private String area;
	private int priority;
	private String task;
	private String benefit;
	private String notes;
}
