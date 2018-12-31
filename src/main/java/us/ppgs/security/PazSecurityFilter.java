package us.ppgs.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.web.filter.GenericFilterBean;

public class PazSecurityFilter extends GenericFilterBean {

	public static class PazFilterResponseRequestWrapper extends HttpServletResponseWrapper {
	    public PazFilterResponseRequestWrapper(HttpServletResponse response) {
	        super(response);
	    }
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		String xForwardProto = httpRequest.getHeader("x-forwarded-proto");
		if ("https".equals(xForwardProto)) {
			// we are behind a proxy (goRouter) and scheme was https, Add HSTS header
			PazFilterResponseRequestWrapper responseWrapper = new PazFilterResponseRequestWrapper(httpResponse);
			responseWrapper.addHeader("Strict-Transport-Security", "max-age=31536000");
			chain.doFilter(request, responseWrapper);
		}
		else if ("http".equals(xForwardProto)) {
			// we are behind a proxy (goRouter) and scheme was http, redirect to https.  Assuming port 80
			httpResponse.sendRedirect("https://" + request.getServerName());
		}
		else {
			chain.doFilter(request, response);
		}
	}
}
