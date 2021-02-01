package us.ppgs.wiki;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public @Data class PageInfo {
	public int id;
	public String page;
	public String tab;
	public long modified;
	public String contents;
}

