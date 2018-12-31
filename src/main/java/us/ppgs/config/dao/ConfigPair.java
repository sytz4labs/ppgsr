package us.ppgs.config.dao;

public class ConfigPair {

	private int id;
	private String name;
	private boolean multiLine;
	private int len;
	private String value;
	
	public ConfigPair() {}
	
	public ConfigPair(int id, String name, boolean multiLine, int len, String value) {
		this.id = id;
		this.name = name;
		this.multiLine = multiLine;
		this.len = len;
		this.value = value;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean getMultiLine() {
		return multiLine;
	}
	public void setMultiLine(boolean multiLine) {
		this.multiLine = multiLine;
	}

	public int getLen() {
		return len;
	}
	public void setLen(int len) {
		this.len = len;
	}

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
