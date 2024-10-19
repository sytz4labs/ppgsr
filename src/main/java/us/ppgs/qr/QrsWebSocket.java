package us.ppgs.qr;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class QrsWebSocket extends TextWebSocketHandler {
	
	private static final String QRS_THREAD = "QrsThread";

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		var t = new QrsThread(session);
		t.start();
		session.getAttributes().put(QRS_THREAD, t);
	}

	
	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
		System.out.println("QrsWebSocket handleTransportError " + exception.getMessage());
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		System.out.println("QrsWebSocket afterConnectionClosed " + status);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		var t = (QrsThread) session.getAttributes().get(QRS_THREAD);
		t.handle(message);
		System.out.println("QrsWebSocket Handle message " + message + " " + session.toString());
    }
}
