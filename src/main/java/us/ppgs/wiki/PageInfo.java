package us.ppgs.wiki;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public @Data class PageInfo {
	public Integer id;
	public Integer sort;
	public String page;
	public String tab;
	public Long modified;
	public String contents;
}

