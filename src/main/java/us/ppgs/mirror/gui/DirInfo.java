package us.ppgs.mirror.gui;

import java.util.ArrayList;
import java.util.List;

public class DirInfo {
	
	private String path;
	private List<String> subDirs = new ArrayList<>();
	private List<String> files = new ArrayList<>();
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public List<String> getSubDirs() {
		return subDirs;
	}
	public void setSubDirs(List<String> subDirs) {
		this.subDirs = subDirs;
	}
	public List<String> getFiles() {
		return files;
	}
	public void setFiles(List<String> files) {
		this.files = files;
	}
}