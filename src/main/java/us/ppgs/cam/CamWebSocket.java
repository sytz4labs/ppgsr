package us.ppgs.cam;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import us.ppgs.config.ConfigFacility;

public class CamWebSocket extends TextWebSocketHandler {

	private static SimpleDateFormat df = new SimpleDateFormat("yyMMdd HH:mm:ss");

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

		boolean[] done = new boolean[] { false };
		session.getAttributes().put("done", done);
		File baseDir = new File(ConfigFacility.get("camDir", "C:\\temp"));
		File dir = new File(baseDir, session.getUri().getQuery());
		String[] fNames = dir.list();
		Arrays.sort(fNames);
		
		new Thread() {

			@Override
			public void run() {
	        	try {
			        for (String fName : fNames) {
			    		if (fName.toLowerCase().endsWith(".jpg")) {
							File f = new File(dir, fName);
							byte[] image = new byte[(int) f.length()];
							FileInputStream is = new FileInputStream(f);
							is.read(image);
							is.close();
							if (done[0]) {
								return;
							}
							else {
								session.sendMessage(new TextMessage("{ \"time\": \"" + df.format(new Date(f.lastModified())) + "\", \"image\": \"data:image/jpg;base64," + Base64.getEncoder().encodeToString(image) + "\"}"));
							}
			    		}
			        }
					session.sendMessage(new TextMessage("{ \"done\": true }"));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}.start();
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		boolean[] done = (boolean[]) session.getAttributes().get("done");
		done[0] = true;
		System.out.println("Handle message " + message + " " + session.toString() + " " + done[0]);
    }
}
