package us.ppgs.linkfarm.info;

import lombok.Data;

public @Data class LFRequest {
	
	private String cmd;
	private String page;
	private int colOrd;
	private int grpOrd;
	private int linkOrd;
	private String name;
	private String url;
	private String newColGrp;
}