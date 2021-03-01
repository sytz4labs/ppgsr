package us.ppgs.mirror.info;

import java.util.ArrayList;
import java.util.List;

import us.ppgs.config.ConfigFacility;
import us.ppgs.config.dao.ConfigException;

public class MirrorClientInfo {

	public static String MIRROR_CLIENT_CONFIG_NAME = "mirror.client";
	public static MirrorClientInfo getConfig() throws ConfigException {
		
		MirrorClientInfo ci = ConfigFacility.get(MIRROR_CLIENT_CONFIG_NAME, MirrorClientInfo.class);
		
		if (ci == null) {
			MirrorDirInfo c = new MirrorDirInfo();
			c.setName("default");
			c.setDirectory("/");
			
			ci = new MirrorClientInfo();
			ci.setServerUrl("localhost:80");
			ci.getDirs().add(c);
			ConfigFacility.add(MIRROR_CLIENT_CONFIG_NAME, ci);
		}
		
		return ci;
	}
	
	private String serverUrl;
	private String serverDir;
	private String logDir;
	private List<MirrorDirInfo> dirs = new ArrayList<MirrorDirInfo>();
	
	public String getServerUrl() {
		return serverUrl;
	}
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getServerDir() {
		return serverDir;
	}
	public void setServerDir(String serverDir) {
		this.serverDir = serverDir;
	}
	public String getLogDir() {
		return logDir;
	}
	public void setLogDir(String logDir) {
		this.logDir = logDir;
	}
	public List<MirrorDirInfo> getDirs() {
		return dirs;
	}
	public void setDirs(List<MirrorDirInfo> dir) {
		this.dirs = dir;
	}

	public static class MirrorDirInfo {
		private String name = "";
		private String directory = "";
		private List<String> include = new ArrayList<String>();
		private List<String> exclude = new ArrayList<String>();
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getDirectory() {
			return directory;
		}
		public void setDirectory(String directory) {
			this.directory = directory;
		}
		public List<String> getInclude() {
			return include;
		}
		public void setInclude(List<String> include) {
			this.include = include;
		}
		public List<String> getExclude() {
			return exclude;
		}
		public void setExclude(List<String> exclude) {
			this.exclude = exclude;
		}
	}

	public void clean() {
		for (MirrorDirInfo dir : dirs) {
			List<String> t = dir.getInclude();
			for (int i = t.size() - 1; i>=0; i--) {
				if (t.get(i).trim().length() == 0) {
					t.remove(i);
				}
			}
			t = dir.getExclude();
			for (int i = t.size() - 1; i>=0; i--) {
				if (t.get(i).trim().length() == 0) {
					t.remove(i);
				}
			}
		}
	}
}
