package us.ppgs.cam;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import us.ppgs.config.ConfigFacility;

public class CamWebSocketLive extends TextWebSocketHandler {
	
	CamUDPThread udbThread = null;
	public CamWebSocketLive() {
		udbThread = new CamUDPThread(this);
		udbThread.start();
	}

	private static SimpleDateFormat df = new SimpleDateFormat("yyMMdd HH:mm:ss");

	@Data
	@AllArgsConstructor
	public static class Msg {
		private String time;
		private Boolean done;
	}
	
	public void send(String m2) {

		try {
			if (m2 == null) {
				for (var session : sessions) {
					session.sendMessage(new TextMessage("{ \"time\": \"" + df.format(new Date()) + "\", \"image\": \"ka\"}"));
				}
			}
			else {
				File baseDir = new File(ConfigFacility.get("camDir"));
				File f = new File(baseDir, m2);
				byte[] image = new byte[(int) f.length()];
				FileInputStream is = new FileInputStream(f);
				is.read(image);
				is.close();
				for (var session : sessions) {
					session.sendMessage(new TextMessage("{ \"time\": \"" + df.format(new Date(f.lastModified())) + "\", \"image\": \"data:image/jpg;base64," + Base64.getEncoder().encodeToString(image) + "\"}"));
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Set<WebSocketSession> sessions = new HashSet<WebSocketSession>();
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {

		sessions.add(session);
	}

	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		System.out.println("CamWebSocketLive handleTransportError " + exception.getMessage());
		sessions.remove(session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		System.out.println("CamWebSocketLive afterConnectionClosed " + status);
		sessions.remove(session);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		boolean[] done = (boolean[]) session.getAttributes().get("done");
		done[0] = true;
		System.out.println("Handle message " + message + " " + session.toString() + " " + done[0]);
    }
}
