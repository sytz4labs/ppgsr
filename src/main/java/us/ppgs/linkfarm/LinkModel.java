package us.ppgs.linkfarm;

import org.springframework.stereotype.Component;

import us.ppgs.config.ConfigFacility;
import us.ppgs.config.dao.ConfigException;
import us.ppgs.linkfarm.info.LFList;
import us.ppgs.linkfarm.info.LinkPage;
import us.ppgs.linkfarm.info.LinkPage.Column;
import us.ppgs.linkfarm.info.LinkPage.Group;
import us.ppgs.linkfarm.info.LinkPage.Link;

@Component
public class LinkModel {

	public LinkPage getObjLinks(String page) throws ConfigException {
		LinkPage lp = ConfigFacility.get("lf." + page, LinkPage.class);
		
		if (lp == null) {
			lp = new LinkPage();
			lp.getCols().add(new Column());
			ConfigFacility.add("lf." + page, lp);
		}
		
		return lp;
	}
	// page
	public void validatePage(LinkPage page) {

		LFList<Column> cols = page.getCols();

		// remove empty columns
		int col = 0;
		while (col < cols.size()) {
			// remove empty groups
			LFList<Group> grps = cols.get(col).getGroups();
			
			if (grps.size() == 0) {
				cols.remove(col);
			}
			else {
				col++;
			}
		}
		
		// make sure at lease one column exists
		if (cols.size() == 0) {
			cols.add(new Column());
		}
	}
	
	// columns
	public void colNew(LinkPage page) {
		page.getCols().add(new LinkPage.Column());
	}

	public void colRemove(LinkPage page) {
		throw new RuntimeException("may just be cleanup");
	}

	public void colLeft(LinkPage page, int colOrd) {
		page.getCols().rotateMinus(colOrd);
	}
	
	public void colRight(LinkPage page, int colOrd) {
		page.getCols().rotatePlus(colOrd);
	}
	
	// groups
	public void grpNew(LinkPage page, int colOrd, String name) {
		Group grp = new Group();
		grp.setName(name);
		page.getCols().get(colOrd).getGroups().add(grp); 
	}

	public void grpNewLink(LinkPage page, int colOrd, int grpOrd) {
		Link newLink = new Link();
		newLink.setHref("http:///");
		newLink.setName("New Link");
		page.getCols().get(colOrd).getGroups().get(grpOrd).getLinks().add(newLink);
	}

	public void grpRename(LinkPage page, int colOrd, int grpOrd, String name) {
		
		page.getCols().get(colOrd).getGroups().get(grpOrd).setName(name);
	}

	public void grpRemove(LinkPage page, int colOrd, int grpOrd) {
		page.getCols().get(colOrd).getGroups().remove(grpOrd); 
	}

	public void grpLeft(LinkPage page, int colOrd, int grpOrd) {
		
		LFList<Column> cols = page.getCols();
		
		Group grp = cols.get(colOrd).getGroups().remove(grpOrd);
		if (colOrd == 0) {
			Column col = new Column();
			col.getGroups().add(grp);
			cols.add(0, col);
		}
		else {
			cols.get(colOrd - 1).getGroups().add(0, grp);
		}
	}
	
	public void grpRight(LinkPage page, int colOrd, int grpOrd) {

		LFList<Column> cols = page.getCols();
		
		Group grp = cols.get(colOrd).getGroups().remove(grpOrd);
		if (colOrd == (cols.size() - 1)) {
			Column col = new Column();
			col.getGroups().add(grp);
			cols.add(col);
		}
		else {
			cols.get(colOrd + 1).getGroups().add(0, grp);
		}
	}
	
	public void grpDown(LinkPage page, int colOrd, int grpOrd) {
		page.getCols().get(colOrd).getGroups().rotatePlus(grpOrd);
	}
	
	public void grpUp(LinkPage page, int colOrd, int grpOrd) {
		page.getCols().get(colOrd).getGroups().rotateMinus(grpOrd);
	}
	
	// links
	public void linkNew(LinkPage page, int colOrd, int grpOrd, String name) {
		Link link = new Link();
		link.setName(name);
		link.setHref("");
		page.getCols().get(colOrd).getGroups().get(grpOrd).getLinks().add(link); 
	}
	
	public Link linkRemove(LinkPage page, int colOrd, int grpOrd, int linkOrd) {
		return page.getCols().get(colOrd).getGroups().get(grpOrd).getLinks().remove(linkOrd); 
	}

	public void linkDown(LinkPage page, int colOrd, int grpOrd, int linkOrd) {
		page.getCols().get(colOrd).getGroups().get(grpOrd).getLinks().rotatePlus(linkOrd);
	}
	
	public void linkUp(LinkPage page, int colOrd, int grpOrd, int linkOrd) {
		page.getCols().get(colOrd).getGroups().get(grpOrd).getLinks().rotateMinus(linkOrd);
	}
	
	public void linkChangeGroup(LinkPage page, int colOrd, int grpOrd, int linkOrd, int toColOrd, int toGrpOrd) {
		Link link = linkRemove(page, colOrd, grpOrd, linkOrd);
		page.getCols().get(toColOrd).getGroups().get(toGrpOrd).getLinks().add(link);
	}
	
	public void saveLink(LinkPage lp, int colOrd, int grpOrd, int linkOrd, String name, String url, String newColGrp) {
		
		Link link = lp.getCols().get(colOrd).getGroups().get(grpOrd).getLinks().get(linkOrd);
		link.setName(name);
		link.setHref(url);
		
		String[] newColGrpVals = newColGrp.split(",");
		int newColOrd = Integer.parseInt(newColGrpVals[0]);
		int newGrpOrd = Integer.parseInt(newColGrpVals[1]);
		if ((newColOrd != colOrd) || (newGrpOrd != grpOrd)) {
			linkChangeGroup(lp, colOrd, grpOrd, linkOrd, newColOrd, newGrpOrd);
		}
	}
}
