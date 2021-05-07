package us.ppgs.blog;

import java.util.Map;

public class HtmlModel {

	public static class Token {}
	
	public static abstract class Element extends Token {
		private String name;

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}

	public static class EndElement extends StartElement {
	}

	public static class StartElement extends Element {
		
		private Map<String, String> attributes = null;

		public Map<String, String> getAttributes() {
			return attributes;
		}
		public void setAttributes(Map<String, String> attributes) {
			this.attributes = attributes;
		}
	}

	public static class FullElement extends StartElement {
	}

	public static class Chars extends Token {
		
		public Chars(String chars) {
			this.chars = chars;
		}
		
		private String chars;
		public String getChars() {
			return chars;
		}
		public void setChars(String chars) {
			this.chars = chars;
		}
	}
}
