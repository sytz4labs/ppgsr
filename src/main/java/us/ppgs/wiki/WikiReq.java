package us.ppgs.wiki;

import lombok.Data;

public @Data class WikiReq {
	private String fileName;
	private String fileText;
}