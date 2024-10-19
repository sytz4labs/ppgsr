package us.ppgs.qr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;

import lombok.AllArgsConstructor;
import lombok.Data;

public class QrsThread extends Thread {

	private WebSocketSession session;
	public QrsThread(WebSocketSession session) {
		this.session = session;
	}

	private boolean done = false;
	public void setDone() {
		done = true;
	}
	
	private boolean test = true;
	private int index = 0;
	
	@Data
	@AllArgsConstructor
	public static class QrsMsg {
		private String cmd;
		private String image;
	}
	
	private ObjectMapper om = new ObjectMapper();
	private int PRIV_BUF_LEN = 375;
	
	@Override
	public void run() {

		try {
			var fileName = Path.of("C:/my/_pss/system-1.zip");
			var fileBytes = Files.readAllBytes(fileName);
			int pos = 0;
			
			while (!done) {
				try {
					sleep(500);
					index++;
					if (test) {
						byte[] testMsg = new byte[PRIV_BUF_LEN];
						for (int i=0; i<testMsg.length; i++) {
							testMsg[i] = (byte) (i + index);
						}
						var testMsg64 = "TeSt" + new String(Base64.getEncoder().encode(testMsg));

						var baos = new ByteArrayOutputStream();
						ZebraUtil.createQRImage(baos, testMsg64, 100, "png");
						session.sendMessage(new TextMessage(om.writeValueAsString(new QrsMsg("test", "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray())))));
					}
					else {
						var nextLen = fileBytes.length - pos;
						nextLen = nextLen > PRIV_BUF_LEN ? PRIV_BUF_LEN : nextLen;
						byte[] msg = new byte[nextLen];
						for (int i=0; i<nextLen; i++) {
							msg[i] = fileBytes[pos + i];
						}
						pos += PRIV_BUF_LEN;
						var testMsg64 = new String(Base64.getEncoder().encode(msg));
						
						var baos = new ByteArrayOutputStream();
						ZebraUtil.createQRImage(baos, testMsg64, 100, "png");
						session.sendMessage(new TextMessage(om.writeValueAsString(new QrsMsg("Real ", "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray())))));
					}
					
					if (!test && pos >= fileBytes.length) {
						done = true;
					}
				}
				catch (InterruptedException | WriterException e) {
					e.printStackTrace();
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				session.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	public void handle(TextMessage message) {
		if ("start".equals(message.getPayload())) {
			test = false;
			index = 0;
		}
	}
}
