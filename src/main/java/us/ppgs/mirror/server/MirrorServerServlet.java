package us.ppgs.mirror.server;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import us.ppgs.mirror.client.RemoteFileSystem;
import us.ppgs.mirror.info.MirrorClientInfo;

@Controller
public class MirrorServerServlet {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat stf = new SimpleDateFormat("HHmm_ss");

	private void log_audit(String logDir, String volName, String msg) throws IOException {
		Date now = new Date();
		
		Files.write(Paths.get(logDir, sdf.format(now) + "_" + volName + ".log"), (stf.format(now) + " " + msg + "\n").getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
	}

    @RequestMapping(value="/mirs", method=RequestMethod.POST)
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/plain");
		ServletOutputStream out = response.getOutputStream();
		DataInputStream dis = new DataInputStream(request.getInputStream());
		try {
			if (dis.readLong() != RemoteFileSystem.getSecret()) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, request.getRequestURI());
				return;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_NOT_FOUND, request.getRequestURI());
			return;
		}
		
		try {
			String cmd = dis.readUTF();
			String volumeName = dis.readUTF();
	
			MirrorClientInfo mci = MirrorClientInfo.getConfig();
			String root = mci.getServerDir();

			File rootDir = new File(root, volumeName);
			if (!rootDir.exists()) {
				rootDir.mkdirs();
			}

			if ("dir".equals(cmd)) {
				doDir(out, rootDir);
				log_audit(mci.getLogDir(), volumeName, "dir   " + rootDir.getCanonicalPath());
			}
			else if ("delete".equals(cmd)) {
				String path = dis.readUTF();
				File dFile = new File(rootDir, path);
				recursiveDelete(mci, volumeName, dFile);
				out.println("OK");
			}
			else if ("mkdir".equals(cmd)) {
				String path = dis.readUTF();
				File dFile = new File(rootDir, path);
				dFile.mkdirs();
				log_audit(mci.getLogDir(), volumeName, "MkDir " + dFile.getCanonicalPath());
				out.println("OK");
			}
			else if ("save".equals(cmd)) {
				String path = dis.readUTF();
				File dFile = new File(rootDir, path);
				FileOutputStream fos = new FileOutputStream(dFile);
				long length = dis.readLong();
				long lastModified = dis.readLong();
				int len = 0;
				long totalLen = 0;
				byte[] buf = new byte[2048];
				while ((len = dis.read(buf)) > 0) {
					fos.write(buf, 0, len);
					totalLen += len;
				}
				fos.close();
				if (totalLen == length) {
					out.println("OK");
					dFile.setLastModified(lastModified);
					log_audit(mci.getLogDir(), volumeName, "Sav   " + dFile.getCanonicalPath());
				}
				else {
					out.println("Xlength not equal actual=" + totalLen + " desired=" + length);
				}
			}
			else {
				out.println("XInvalid command " + cmd);
			}
			dis.close();
		}
		catch (Exception e) {
			out.println("X" + e.toString());
			PrintStream ps = new PrintStream(out);
			e.printStackTrace(ps);
			ps.flush();
			ps.close();
		}
	}
	
	private void recursiveDelete(MirrorClientInfo mci, String volumeName, File f) throws IOException {
		if (f.isDirectory()) {
			String[] children = f.list();
			for (int i=0; i<children.length; i++) {
				recursiveDelete(mci, volumeName, new File(f, children[i]));
			}
		}

		f.delete();
		log_audit(mci.getLogDir(), volumeName, "del   " + f.getCanonicalPath());
	}
	
	private void doDir(ServletOutputStream out, File dir) throws IOException {

		for (String nName : dir.list()) {
			File curFile = new File(dir, nName);
			if (curFile.isDirectory()) {
				out.println("D" + nName);
				doDir(out, curFile);
				out.println("E");
			}
			else {
				out.println("F" + nName + "|" + curFile.length() + "|" + curFile.lastModified());
			}
		}
	}
}
