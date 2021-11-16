package us.ppgs.cam;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import us.ppgs.config.ConfigFacility;

public class CamUDPThread extends Thread {

	@Component
	public static class AppClose implements ApplicationListener<ContextClosedEvent> {

		@Override
		public void onApplicationEvent(ContextClosedEvent event) {
			System.out.println("Ending ======================");
			done.set(true);
		}
	}

	static AtomicBoolean done = new AtomicBoolean(false);
	CamWebSocketLive socket;
	
	public CamUDPThread(CamWebSocketLive socket) {
		this.socket = socket;
	}

	@Override
	public void run() {

		int port = ConfigFacility.getInt("camUdpPort", 12345);
		
		DatagramSocket serverSocket = null;
		try {
			serverSocket = new DatagramSocket(port);
			serverSocket.setSoTimeout(20000);

			byte[] receiveData = new byte[80];
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

			while (!done.get()) {
				try {
					serverSocket.receive(receivePacket);
					var msg = new String(receivePacket.getData(), 0, receivePacket.getLength());
					System.out.println(receivePacket.getAddress() + " " + msg);
					socket.send(msg);
				}
				catch (SocketTimeoutException ste) {
					socket.send(null);
				}
			}
		}
		catch (IOException e) {
			System.out.println(e);
		}
		finally {
			if (serverSocket != null) {
				serverSocket.close();
			}
		}
	}
}
