package us.ppgs.host;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HostApp {

	private Map<String, String> hosts = new TreeMap<String, String>();

	@RequestMapping("/hosts")
	public void indexb(String host, String ip, HttpServletResponse res) throws IOException {
    	
    	String now = new Date().toString();
    	
    	if (host != null) {
    		hosts.put(host, ip + " " + now);
    	}

    	StringBuilder sb = new StringBuilder();
    	for (Map.Entry<String, String> hostEntry : hosts.entrySet()) {
    		sb.append(hostEntry.getValue())
    			.append(" - ")
    			.append(hostEntry.getKey())
    			.append("\n");
    	}
    	
    	res.setContentType("text/plain");
    	ServletOutputStream os = res.getOutputStream();
		os.print( now.toString() + "\n" + sb.toString());
	}

}
