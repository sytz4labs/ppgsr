package us.ppgs.util.info;

import lombok.Data;

public @Data class RestResponse {

	private Object info;
	private String success;
	private String error;

	public RestResponse(Object info, String success, String error) {
		this.info = info;
		this.success = success;
		this.error = error;
	}
}
