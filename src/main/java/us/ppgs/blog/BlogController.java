package us.ppgs.blog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.InternalResourceView;

import us.ppgs.blog.HtmlModel.EndElement;
import us.ppgs.blog.HtmlModel.StartElement;
import us.ppgs.util.Crypt;

@Controller
@RequestMapping("/blog")
public class BlogController {

	@RequestMapping("")
	public String index() {
		return "redirect:/blog/";
	}

	@RequestMapping("/")
	public View indexSlash() {
		return new InternalResourceView("/blog/index.html");
	}

	@RequestMapping("/url")
	@ResponseBody
	public List<String> url(@RequestBody AUrl url) throws ClientProtocolException, IOException {
		return doUrl(url.getName(), url.getUrl());
	}
	
	@RequestMapping("/all")
	@ResponseBody
	public String bunch() throws ClientProtocolException, IOException {
		String[] ss = {
//		"http://ethsjapan2014.blogspot.com/p/alex.html",
//		"http://ethsjapan2014.blogspot.com/p/alex-g.html",
//		"http://ethsjapan2014.blogspot.com/p/atul-kumar.html",
//		"http://ethsjapan2014.blogspot.com/p/carly.html",
//		"http://ethsjapan2014.blogspot.com/p/daniel.html",
//		"http://ethsjapan2014.blogspot.com/p/danielle.html",
//		"http://ethsjapan2014.blogspot.com/p/grant_6.html",
//		"http://ethsjapan2014.blogspot.com/p/logan.html",
//		"http://ethsjapan2014.blogspot.com/p/melody.html",
//		"http://ethsjapan2014.blogspot.com/p/max.html",
//		"http://ethsjapan2014.blogspot.com/p/mitchell-e.html",
//		"http://ethsjapan2014.blogspot.com/p/mitchell-w.html",
//		"http://ethsjapan2014.blogspot.com/p/nathan.html",
//		"http://ethsjapan2014.blogspot.com/p/rodrigo_9.html",
//		"http://ethsjapan2014.blogspot.com/p/sofia.html",
//		"http://ethsjapan2014.blogspot.com/p/saturday-november-8-well-being-totally.html",
//		"http://ethsjapan2014.blogspot.com/p/susannah.html",
//		"http://ethsjapan2014.blogspot.com/p/van.html"
				"http://ethsjapan2014.blogspot.com/2014/11/sayonara-kj-were-going-home.html",
				"http://ethsjapan2014.blogspot.com/2014/11/school-day-at-kj.html",
				"http://ethsjapan2014.blogspot.com/2014/11/home-stays.html",
				"http://ethsjapan2014.blogspot.com/2014/11/excursion-to-odaiba-in-tokyo.html",
				"http://ethsjapan2014.blogspot.com/2014/11/welcome-to-kj.html",
				"http://ethsjapan2014.blogspot.com/2014/11/from-kumamoto-to-urasa.html",
				"http://ethsjapan2014.blogspot.com/2014/11/a-day-in-hot-spring-village.html",
				"http://ethsjapan2014.blogspot.com/2014/11/nagasaki.html",
				"http://ethsjapan2014.blogspot.com/2014/11/day-trip-to-dazaifu.html",
				"http://ethsjapan2014.blogspot.com/2014/11/on-to-kumamoto.html",
				"http://ethsjapan2014.blogspot.com/2014/11/22-miles-were-not-kidding-aka-50420.html",
				"http://ethsjapan2014.blogspot.com/2014/11/arashiyama.html",
				"http://ethsjapan2014.blogspot.com/2014/11/were-here.html"
				};
	
		StringBuilder sb = new StringBuilder();
		for (String s : ss) {
			sb.append(s).append(" ");
			doUrl("x", s);
		}
		return sb.toString();
	}
	
	private List<String> doUrl(String name, String url) throws ClientProtocolException, IOException {
		HttpGet httppost = new HttpGet(url);
		CloseableHttpClient httpclient = HttpClientBuilder.create().build();
		CloseableHttpResponse saveResponse = httpclient.execute(httppost);
		InputStream is = saveResponse.getEntity().getContent();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		IOUtils.copyLarge(is, baos);
		
		BlogHandler bh = new BlogHandler();
		HtmlParser.parse(baos.toString(), bh);

		bh.saveHtml(bh.get(), url);
		return bh.get();
	}
	
	public static class AUrl {
		private String url;
		private String name;
		
		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
	
	public static class BlogHandler implements HtmlHandler {
		
		private File saveDir = new File("c:/my/japan2");
		
		private List<String> caw = new ArrayList<>();
		private boolean important = false;
		private Stack<String> stk = new Stack<>();
		
		public List<String> get() {
			return caw;
		}

		@Override
		public void startElement(StartElement element) {
			Map<String, String> a = element.getAttributes();

			if (element.getName().equals("br")
					|| element.getName().equals("meta")
					|| element.getName().equals("g:plusone")
					|| element.getName().equals("embed")
					|| element.getName().equals("param")
					|| element.getName().equals("input")
					|| element.getName().equals("script")) {
				return;
			}
				
			if (element.getName().equals("div")) {
				if (a != null) {
					if ("date-outer".equals(a.get("class"))) {
						important = true;
					}
				}
			}

			if (element.getName().equals("img")) {
				String src = element.getAttributes().get("src");
				if (src.endsWith(".jpg") || src.endsWith(".JPG")) {
					src = src.replace("s1600", "s5000");
					if (src.startsWith("//")) {
						src = "http:" + src;
					}
					String newSrc = Crypt.bytesToHex(Crypt.encryptMD5(src)) + ".jpg";
					loadFile(src, newSrc);
					element.getAttributes().put("src", newSrc);
				}
			}
			
			if (element.getName().equals("a")) {
				String src = element.getAttributes().get("href");
				if (src != null) {
					if (src.endsWith(".jpg") || src.endsWith(".JPG")) {
						src = src.replace("s1600", "s5000");
						String newSrc = Crypt.bytesToHex(Crypt.encryptMD5(src)) + ".jpg";
						loadFile(src, newSrc);
						element.getAttributes().put("href", newSrc);
					}
				}
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append("<").append(element.getName());
			if (a != null) {
				for (Map.Entry<String, String> e : a.entrySet()) {
					sb.append(" ").append(e.getKey()).append("=\"").append(e.getValue()).append("\"");
				}
			}
			sb.append(">");
			writeln(sb.toString());
			
			if (important && !element.getName().equals("img")) {
				stk.push(element.getName());
			}
		}

		private void loadFile(String src, String newSrc) {
			
			if (!saveDir.exists()) {
				saveDir.mkdirs();
			}
			
			System.out.println(src + " to " + newSrc);
			File f = new File(saveDir, newSrc);
			if (!f.exists()) {
				HttpGet httppost = new HttpGet(src);
				CloseableHttpClient httpclient = HttpClientBuilder.create().build();
				CloseableHttpResponse saveResponse;
				try {
					saveResponse = httpclient.execute(httppost);
					InputStream is = saveResponse.getEntity().getContent();
					FileOutputStream fos = new FileOutputStream(f);
					IOUtils.copyLarge(is, fos);
					fos.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		private void saveHtml(List<String> html, String newSrc) {
			int pos = newSrc.lastIndexOf('/');
			String fileName = newSrc.substring(pos + 1);
			System.out.println(" to " + fileName);
			File f = new File(saveDir, fileName);
			if (!f.exists()) {
				try {
					FileOutputStream fos = new FileOutputStream(f);
					for (String s : html) {
						fos.write(s.getBytes());
					}
					fos.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		@Override
		public void fullElement(StartElement element) {
			startElement(element);
		}

		@Override
		public void endElement(EndElement element) {
			
			writeln("</" + element.getName() + ">");
			
			if (important) {
				if (element.getName().equals(stk.peek())) {
					stk.pop();
					if (stk.isEmpty()) {
						important = false;
					}
				}
				else {
					System.out.println("found " + element.getName() + " peek= " + stk.peek());
				}
			}
		}

		@Override
		public void characters(String chars) {
			writeln(chars);
		}

		@Override
		public void error(String error) {
			writeln("error " + error);
		}

		@Override
		public void docType(String docType) {
		}

		@Override
		public void comment(String comment) {
		}

		private void writeln(String s) {
			if (important) {
				caw.add(s);
			}
		}
	}
}
