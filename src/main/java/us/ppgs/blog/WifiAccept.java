package us.ppgs.blog;

import java.io.CharArrayWriter;
import java.io.FileReader;

import us.ppgs.blog.HtmlModel.EndElement;
import us.ppgs.blog.HtmlModel.StartElement;

public class WifiAccept {

	public static void main(String[] args) {
		
		try {
			FileReader f = new FileReader("C:/my/_pss/form.sav");

			CharArrayWriter baos = new CharArrayWriter();
			char c[] = new char[1024];
			int len = 0;
			while ((len = f.read(c)) > 0) {
				baos.write(c, 0, len);
			}
			
			HtmlParser.parse(baos.toCharArray(), new HtmlHandler() {

				@Override
				public void startElement(StartElement element) {
					if (element.getName().equalsIgnoreCase("form")
						|| element.getName().equalsIgnoreCase("input")) {
						System.out.println("startElement " + element.getName());
						if (element.getName().equalsIgnoreCase("form")) {
							System.out.println("  " + element.getAttributes().get("action"));
						}
						if (element.getName().equalsIgnoreCase("input")) {
							System.out.println("  " + element.getAttributes().get("name") + " = " + element.getAttributes().get("value"));
						}
					}
				}

				@Override
				public void fullElement(StartElement element) {
					startElement(element);
				}

				@Override
				public void endElement(EndElement element) {
					if (element.getName().equalsIgnoreCase("form")
							|| element.getName().equalsIgnoreCase("input")) {
						System.out.println("endElement " + element.getName());
					}
				}

				@Override
				public void characters(String chars) {
				}

				@Override
				public void error(String error) {
					System.out.println("error " + error);
				}

				@Override
				public void docType(String docType) {
				}

				@Override
				public void comment(String comment) {
				}
				
			});

			f.close();
		}
		catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
