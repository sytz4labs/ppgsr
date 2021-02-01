package us.ppgs.wiki;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public @Data class FileInfo {
	public int id;
	public String file;
	public String page;
	public long modified;
	public String contents;
}

