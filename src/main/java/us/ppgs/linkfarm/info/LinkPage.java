package us.ppgs.linkfarm.info;

import lombok.Data;

public @Data class LinkPage {

	private LFList<Column> cols = new LFList<Column>();
	
	public static @Data class Column {

		private LFList<Group> groups = new LFList<Group>();
	}
	
	public static @Data class Group {
		
		private String name;
		private LFList<Link> links = new LFList<Link>();
	}
	
	public static @Data class Link {
		
		private String name;
		private String href;
	}
}
