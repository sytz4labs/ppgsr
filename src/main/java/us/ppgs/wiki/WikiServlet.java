package us.ppgs.wiki;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;
import us.ppgs.config.ConfigFacility;
import us.ppgs.io.StreamCache;
import us.ppgs.security.LoginInfo;

@Controller
@Slf4j
public class WikiServlet {
	
	private static Random rand = new Random();
    private static String[][] affirm = new String[][] {
	    {"IT WILL BE DIFFICULT:", "I have the ability to accomplish any task I set my mind to with ease and comfort."},
	    {"IT'S GOING TO BE RISKY:", "Being myself involves no risks.  It is my ultimate truth, and I live it fearlessly."},
	    {"IT WILL TAKE A LONG TIME:", "I have infinite patience when it comes to fulfilling my destiny."},
	    {"THERE WILL BE FAMILY DRAMA:", "I would rather be loathed for who I am than loved for who I am not."},
	    {"I DON'T DESERVE IT:", "I am a Divine creation, a piece of God.  Therefore, I cannot be undeserving."},
	    {"IT'S NOT MY NATURE:", "My essential nature is perfect and faultless.  It is to this nature that I return."},
	    {"I CAN'T AFFORD IT:", "I am connected to an unlimited source of abundance."},
	    {"NO ONE WILL HELP ME:", "The right circumstances and the right people are already here and will show up on time."},
	    {"IT HAS NEVER HAPPENED BEFORE:", "I am willing to attract all that I desire, beginning here and now."},
	    {"I'M NOT STRONG ENOUGH:", "I have access to unlimited assistance.  My strength comes from my connection to my Source of being."},
	    {"I'M NOT SMART ENOUGH:", "I am a creation of the Divine mind; all is perfect, and I am a genius in my own right."},
	    {"I'M TOO OLD (OR NOT OLD ENOUGH}:", "I am an infinite being. The age of my body has no bearing on what I do or who I am."},
	    {"THE RULES WON'T LET ME:", "I live my life according to Divine rules."},
	    {"IT'S TOO BIG:", "I think only about what I can do now.  By thinking small, I accomplish great things."},
	    {"I DON'T HAVE THE ENERGY:", "I feel passionately about my life, and this passion fills me with excitement and energy."},
	    {"IT'S MY PERSONAL FAMILY HISTORY:", "I live in the present moment by being grateful for all of my life experiences as a child."},
	    {"I'M TOO BUSY:", "As I unclutter my life, I free myself to answer the callings of my soul."},
	    {"I'M TOO SCARED:", "I can accomplish anything I put my mind to, because I know that I am never alone."}};

    //Tue, 11 May 2010 22:04:44 GMT
    private static SimpleDateFormat fmt = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
    static {
    	fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value="/wiki/edit", method=RequestMethod.GET)
	public String edit(HttpServletRequest req, Model m) {

    	String qs = req.getQueryString();
    	if (qs == null || qs.length() == 0) {
    		qs = "index";
    	}
    	
		LoginInfo li = LoginInfo.getLoginInfo(LoginInfo.SESS_LEVEL_REMEMBER_ME);
		
		m.addAttribute("li", li);
		
		FileInfo fi = getFile(qs);
		m.addAttribute("f", fi == null ? null : new String(fi.contents));
		
		m.addAttribute("qs", qs);
		m.addAttribute("li", li);
		
		return "wiki/edit";
	}

    @RequestMapping(value="/wiki", method=RequestMethod.GET)
	public String index(HttpServletRequest req, Model m) {

    	return pageService("index", m);
    }

    @RequestMapping(value="/wiki/{page}", method=RequestMethod.GET)
	public String page(@PathVariable String page, Model m) {

    	return pageService(page, m);
    }

	private String pageService(String qs, Model m) {

		FileInfo fi = getFile(qs);
		m.addAttribute("f", fi == null ? null : new String(fi.contents));

		m.addAttribute("qs", qs);
		LoginInfo li = LoginInfo.getLoginInfo(qs.startsWith("tnt") ? LoginInfo.SESS_LEVEL_REMEMBER_ME : LoginInfo.SESS_LEVEL_NO_LOGIN);
		m.addAttribute("li", li);

		if ("index".equals(qs)) {
		    int x = rand.nextInt(affirm.length);
	
			m.addAttribute("affirm0", affirm[x][0]);
			m.addAttribute("affirm1", affirm[x][1]);
		}

		return "wiki/home";
	}
    
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value="/wiki/save", method=RequestMethod.POST)
	public String save(String text, String qs, Model m) throws Exception {

		// save text
		saveFile(qs, text);

		return "redirect:/wiki/" + ("index".equals(qs) ? "" : "?" + qs);
	}
	
	private static FileInfo getFile(String qs) {

		File d = new File(ConfigFacility.get("wikiDir"));
		File f = new File(d, qs);
		StreamCache sc = new StreamCache();
		if (f.exists()) {
			try {
				FileInfo fi = new FileInfo();
				fi.modified = f.lastModified();
				sc.fillFromInputStream(new FileInputStream(f));
				fi.contents = sc.getBytes();
				return fi;
			}
			catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		return null;
	}

	public static class FileInfo {
		public long modified;
		public byte[] contents;
	}

	public static void saveFile(String qs, String text) throws Exception {
		
		File d = new File(ConfigFacility.get("wikiDir"));
		File f = new File(d, qs);
		FileOutputStream fos = new FileOutputStream(f);
		try {
			fos.write(text.getBytes());
			fos.close();
		}
		catch (IOException e) {
			log.error("saveFile " + qs, e);
		}
		finally {
			if (fos != null) {
				fos.close();
			}
		}
	}
}
