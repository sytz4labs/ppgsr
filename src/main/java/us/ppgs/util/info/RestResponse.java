package us.ppgs.util.info;

import lombok.Data;
import us.ppgs.security.LoginInfo;

public @Data class RestResponse {

	private String user;
	private Object info;
	private String success;
	private String error;

	public RestResponse(Object info, String success, String error) {
    	LoginInfo li = LoginInfo.getLoginInfo(LoginInfo.SESS_LEVEL_NO_LOGIN);
    	if (li != null) {
    		this.user = li.getUserId();
    	}
		this.info = info;
		this.success = success;
		this.error = error;
	}
}
