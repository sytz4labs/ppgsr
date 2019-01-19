package us.ppgs.budget.info;

import java.util.Date;
import java.util.List;

import lombok.Data;
import us.ppgs.security.LoginInfo;

public @Data class TransData {

	private String user = null;
	private Date today = null;
	private List<BdgtTranInfoVisitor> trans = null;
	private List<BdgtTranSpecInfo> transSpecs = null;
	private List<BdgtTranInfoVisitor> transHist = null;
	
	public TransData(List<BdgtTranSpecInfo> specs, List<BdgtTranInfoVisitor> trans, Date date) {
    	LoginInfo li = LoginInfo.getLoginInfo(LoginInfo.SESS_LEVEL_NO_LOGIN);
    	if (li != null) {
    		this.user = li.getUserId();
    	}
		this.transSpecs = specs;
		this.trans = trans;
		this.today = date;
	}
	
	public TransData(List<BdgtTranInfoVisitor> list) {
		this.transHist = list;
	}
}
