package us.ppgs.udp.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;

import us.ppgs.cam.CamConsts;
import us.ppgs.config.ConfigFacility;

class UDPClient {
	public static void main(String args[]) throws Exception
	{
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		String sentence = inFromUser.readLine();

		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		sendData = sentence.getBytes();
		//DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length);
		System.out.println(sendData.length);
		sendPacket.setPort(ConfigFacility.getInt("camPort", CamConsts.DEFAULT_PORT));
		sendPacket.setAddress(InetAddress.getByAddress(new byte[] { -1, -1, -1, -1}));

		DatagramSocket clientSocket = new DatagramSocket();
		clientSocket.setSoTimeout(ConfigFacility.getInt("camTimeoutMs", CamConsts.DEFAULT_TIMEOUT));
		clientSocket.send(sendPacket);
		InetAddress lh = InetAddress.getLocalHost();
		System.out.println("lh " + lh.getHostAddress());
		
		InetAddress localHost = Inet4Address.getLocalHost();
		NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);

		for (InterfaceAddress address : networkInterface.getInterfaceAddresses()) {
		    System.out.println(address.getNetworkPrefixLength());
		}
		
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket);
		String modifiedSentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
		System.out.println("FROM SERVER:" + modifiedSentence);

		InetAddress saddr = receivePacket.getAddress();
		System.out.println(saddr);

		clientSocket.close();

		
		
		Socket s = new Socket(saddr, ConfigFacility.getInt("camPort"));
		
		OutputStream os = s.getOutputStream();
		os.write(sentence.getBytes());
		
		InputStream is = s.getInputStream();
		byte[] buf = new byte[2024];
		int l = is.read(buf);
		System.out.print(new String(buf, 0, l));
		
		s.close();
	}
}
