package us.ppgs.util.info;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public @Data class KeyValue {
	private String key;
	private String value;
}
