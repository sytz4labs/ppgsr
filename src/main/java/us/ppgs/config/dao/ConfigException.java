package us.ppgs.config.dao;

public class ConfigException extends Exception {

	private static final long serialVersionUID = 1L;
	private String name;

	public ConfigException(String name, String s) {
		super(s);
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
