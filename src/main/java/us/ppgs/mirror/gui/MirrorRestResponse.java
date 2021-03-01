package us.ppgs.mirror.gui;

import java.util.List;

import us.ppgs.util.info.RestResponse;

public class MirrorRestResponse extends RestResponse {

	private List<Log> logs;
	
	public MirrorRestResponse(Object info, String success, String error, List<Log> logs) {
		super(info, success, error);
		this.logs = logs;
	}

	public List<Log> getLogs() {
		return logs;
	}
	public void setLogs(List<Log> logs) {
		this.logs = logs;
	}
	
	public static class Log {
		private String name;
		private long size;
		private List<String> text;

		public Log(String name, long size, List<String> text) {
			super();
			this.name = name;
			this.size = size;
			this.text = text;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public long getSize() {
			return size;
		}
		public void setSize(long size) {
			this.size = size;
		}
		public List<String> getText() {
			return text;
		}
		public void setText(List<String> text) {
			this.text = text;
		}
	}
}
