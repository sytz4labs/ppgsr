package us.ppgs.wiki;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.util.TimeZone;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import us.ppgs.config.ConfigFacility;
import us.ppgs.io.StreamCache;

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

	@GetMapping("/wiki")
	public String indexb() {
		return "redirect:/wiki/";
	}

	@GetMapping("/wiki/")
	public String indexSlash() {
		return "wiki/index";
	}

    @GetMapping("/wiki/{page}")
	public String page(@PathVariable String page, Model m) {
		return "wiki/index";
    }

    public static @Data class WikiReq {
    	private String fileName;
    	private String fileText;
    }

    public static @Data class WikiRes {
    	private String fileName;
    	private String fileText;
    	private String affirm0;
    	private String affirm1;
    }
    
    @PostMapping("/wiki/get")
    @ResponseBody
	private WikiRes pageService(@RequestBody WikiReq req) {

    	WikiRes res = new WikiRes();

		FileInfo fi = getFile(req.fileName);
		res.setFileName(req.fileName);
		if (fi != null) {
			res.setFileText(new String(fi.contents));
		}

		if ("index".equals(req.getFileName())) {
		    int x = rand.nextInt(affirm.length);
			res.setAffirm0(affirm[x][0]);
			res.setAffirm1(affirm[x][1]);
		}

		return res;
	}
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/wiki/save")
    @ResponseBody
	public WikiRes save(@RequestBody WikiReq req) throws Exception {

		saveFile(req.fileName, req.fileText);

		return pageService(req);
	}
	
	private static FileInfo getFile(String fileName) {

		File d = new File(ConfigFacility.get("wikiDir"));
		File f = new File(d, fileName);
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

	public static void saveFile(String fileName, String fileText) throws Exception {
		
		File d = new File(ConfigFacility.get("wikiDir"));
		File f = new File(d, fileName);
		FileOutputStream fos = new FileOutputStream(f);
		try {
			fos.write(fileText.getBytes());
			fos.close();
		}
		catch (IOException e) {
			log.error("saveFile " + fileName, e);
		}
		finally {
			if (fos != null) {
				fos.close();
			}
		}
	}
}
