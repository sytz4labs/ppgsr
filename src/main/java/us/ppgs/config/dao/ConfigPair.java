package us.ppgs.config.dao;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public @Data class ConfigPair {

	private int id;
	private String name;
	private boolean multiLine;
	private int len;
	private String value;
}
