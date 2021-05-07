package us.ppgs.blog;

import us.ppgs.blog.HtmlModel.EndElement;
import us.ppgs.blog.HtmlModel.StartElement;

public interface HtmlHandler {

	public void docType(String docType);
	
	public void comment(String comment);
	
	public void startElement(StartElement element);
	
	public void fullElement(StartElement element);
	
	public void endElement(EndElement name);
	
	public void characters(String chars);

	public void error(String error);
}
