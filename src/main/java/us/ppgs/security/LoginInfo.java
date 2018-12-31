package us.ppgs.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class LoginInfo {
	
	public static final int SESS_LEVEL_NO_LOGIN = 0;
	public static final int SESS_LEVEL_REMEMBER_ME = 1;
	public static final int SESS_LEVEL_CREDENTIAL_LOGIN = 2;
	public static final int SESS_LEVEL_TWO_FACTOR_LOGIN = 3;

	// used outside security
	public static LoginInfo getLoginInfo() {
		return getLoginInfo(SESS_LEVEL_NO_LOGIN);
	}
	
	public static LoginInfo getLoginInfo(int levelRequired) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return null;
		}
		
		// RememberMeAuthenticationToken.class.isAssignableFrom(authentication.getClass());
		
		return new LoginInfo("admin", authentication.getName(), levelRequired);
	}

	public static LoginInfo getLoginInfoAjax(int levelRequired) {
		return getLoginInfo(levelRequired);
	}

	LoginInfo(String realmId, String userId, int sessLevel) {
		this.realmId = realmId;
		this.userId = userId;
		this.sessLevel = sessLevel;
	}
	
	private String realmId;
	private String userId;
	private int sessLevel;
	
	public void setRealmId(String realmId) {
		this.realmId = realmId;
	}
	public String getRealmId() {
		return realmId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserId() {
		return userId;
	}
	
	public int getSessionLevel() {
		return sessLevel;
	}
}
