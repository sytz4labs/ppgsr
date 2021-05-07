package us.ppgs.blog;

import java.util.HashMap;
import java.util.Map;

public class HtmlParser {

	public static void parse(String chars, HtmlHandler handler) {
		
		parse(chars.toCharArray(), handler);
	}

	public static void parse(char[] chars, HtmlHandler handler) {
		
		HtmlParser parser = new HtmlParser();
		parser.chars = chars;
		parser.handler = handler;
		parser.pos = 0;
		parser.parse();
	}
	
	private char[] chars;
	private int pos;
	private HtmlHandler handler;
	
	private void parse() {
		
		while(pos < chars.length && parseToken()) {
		}
		if (pos < chars.length) {
			int len = chars.length - pos;
			if (len > 40) {
				len = 40;
			}
			
			handler.error("Unexpected Characters [" + pos + "]" + new String(chars, pos, len));
		}
	}

	private boolean parseToken() {
		return parseChars() || parseElement() || parseComment() || parseDocType();
	}

	private boolean parseChars() {
		if (pos < chars.length && chars[pos] != '<') {
			StringBuilder sb = new StringBuilder();
			while (chars.length > pos && chars[pos] != '<') {
				sb.append(chars[pos++]);
			}
			handler.characters(sb.toString());
			return true;
		}
		
		return false;
	}
	
	private boolean parseElement() {
		int savePos = pos;
		
		if (parseConst('<')) {
			if (parseConst('/')) { // end tag
				String tageName = parseIdentifier();
				if (tageName != null) {
					
					Map<String, String> attributes = parseAttributes();
					
					if (parseConst('>')) {
						HtmlModel.EndElement e = new HtmlModel.EndElement();
						e.setName(tageName);
						e.setAttributes(attributes);
						handler.endElement(e);
						return true;
					}
				}
			}
			else { // start tag
				String tageName = parseIdentifier();
				if (tageName != null) {
					
					Map<String, String> attributes = parseAttributes();
					
					if (parseConst('>')) {
						HtmlModel.StartElement e = new HtmlModel.StartElement();
						e.setName(tageName);
						e.setAttributes(attributes);
						if (tageName.equalsIgnoreCase("script")) {
							// remove script
							int save = pos;
							do {
								if (parseConst("</script>")) {
									HtmlModel.FullElement e1 = new HtmlModel.FullElement();
									e1.setName(tageName);
									e1.setAttributes(attributes);
									handler.startElement(e1);
									return true;
								}
								else {
									if (pos < chars.length) {
										pos++;
									}
									else {
										pos = save;
										return false;
									}
								}
							} while (true);
						}
						handler.startElement(e);
						return true;
					}
					else if (parseConst("/>")) {
						HtmlModel.FullElement e = new HtmlModel.FullElement();
						e.setName(tageName);
						e.setAttributes(attributes);
						handler.startElement(e);
						return true;
					}
 
				}
			}
		}
		
		pos = savePos;
		
		return false;
	}
	
	private Map<String, String> parseAttributes() {
		Map<String, String> attributes = null;
		
		// attribute stuff goes here
		while (parseWhiteSpace()) { // have to have some white space before
			String key = parseIdentifier();
			if (key != null) {
				parseWhiteSpace();
				if (parseConst('=')) {
					parseWhiteSpace();
					String value = parseAttributeValue();
					if (value != null) {
						if (attributes == null) {
							attributes = new HashMap<>();
						}
						attributes.put(key, value);
					}
				}
			}						
		}
		
		return attributes;
	}

	private boolean parseComment() {
		if (parseConst("<!--")) {
			
			int save = pos;
			
			StringBuilder sb = new StringBuilder();
			
			do {
				if (parseConst("-->")) {
					handler.comment(sb.toString());
					return true;
				}
				else {
					if (pos < chars.length) {
						sb.append(chars[pos++]);
					}
					else {
						pos = save;
						return false;
					}
				}
			} while (true);
		}
		
		return false;
	}

	private boolean parseDocType() {
		if (parseConst("<!")) {
			int save = pos;
			StringBuilder sb = new StringBuilder();
			
			while (pos < chars.length && chars[pos] != '>') {
				sb.append(chars[pos++]);
			}
			if (pos < chars.length) {
				if (chars[pos] == '>') {
					handler.docType(sb.toString());
					pos++;
					return true;
				}
				else {
					pos = save;
				}
			}
		}

		return false;
	}

	private String parseAttributeValue() {

		String value = parseQuotedString('"');
		
		if (value == null) {
			value = parseQuotedString('\'');
		}
		
		if (value == null) {
			value = parseIdentifier();
		}
		
		return value;
	}

	private String parseQuotedString(char quoteChar) {

		if (chars.length > pos && chars[pos] == quoteChar) {
			// we have a quoted string, go to end
			StringBuilder sb = new StringBuilder();
			pos++;
			while (chars.length > pos && chars[pos] != quoteChar) {
				sb.append(chars[pos++]);
			}
			if (chars.length > pos && chars[pos] == quoteChar) {
				pos++;
				return sb.toString();
			}
		}
		
		return null;
	}

	private final String CHARS_WHITE_SPACE = " \t\n\r";
	private final String CHARS_ALPHA_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private final String CHARS_ALPHA_LOWER = "abcdefghijklmnopqrstuvwxyz";
	private final String CHARS_NUMBERS = "0123456789";
	private final String CHARS_IDENTIFIER = CHARS_ALPHA_UPPER + CHARS_ALPHA_LOWER + CHARS_NUMBERS + "_-.:";
			
	private String parseIdentifier() {
		char c = parseSet(CHARS_IDENTIFIER); // must find atleast one
		if (c > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(c);
			while ((c = parseSet(CHARS_IDENTIFIER)) > 0) {
				sb.append(c);
			}
			return sb.toString();
		}
		
		return null;
	}

	private boolean parseWhiteSpace() {
		
		int num = 0;
		while (parseSet(CHARS_WHITE_SPACE) > 0) {
			num++;
		}
	
		return num != 0;
	}
	
	private char parseSet(String charSet) {
		
		if (pos < chars.length && charSet.indexOf(chars[pos]) >= 0) {
			return chars[pos++];
		}

		return 0;
	}

	private boolean parseConst(String string) {

		int save = pos;
		
		for (char c : string.toCharArray()) {
			if (!parseConst(c)) {
				pos = save;
				return false;
			}
		}

		return true;
	}

	private boolean parseConst(char c) {
		if (pos < chars.length && chars[pos] == c) {
			pos++;
			return true;
		}
		return false;
	}

}
