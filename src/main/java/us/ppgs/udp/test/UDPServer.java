package us.ppgs.udp.test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class UDPServer {
	public static void main(String args[]) throws Exception
	{
		DatagramSocket serverSocket = null;
		try {
			serverSocket = new DatagramSocket(9876);
			byte[] receiveData = new byte[1024];
			byte[] sendData = new byte[1024];
			int ii=0;
			while(true)
			{
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
	
				String sentence = new String( receivePacket.getData(), 0, receivePacket.getLength());
				System.out.println("item " + ii++ + " len " + receivePacket.getLength());
				System.out.println("RECEIVED: " + sentence + " " + sentence.length());
	
				InetAddress IPAddress = receivePacket.getAddress();
				System.out.println("From " + IPAddress);
				int port = receivePacket.getPort();
				String capitalizedSentence = sentence.toUpperCase();
	
				sendData = capitalizedSentence.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
				serverSocket.send(sendPacket);
			}
		}
		finally {
			serverSocket.close();
		}
	}
}