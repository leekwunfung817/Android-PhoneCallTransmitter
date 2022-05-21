package com.cpos.activemq.mqtt;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

public class UdpInternet extends Password {
	
	public static void send(byte[] bytes) throws IOException {
		InetAddress address = InetAddress.getByName(SERVER_IP);
		DatagramSocket socket = new DatagramSocket();
		DatagramPacket request = new DatagramPacket(bytes, bytes.length,  address, UDP_PORT);
		socket.send(request);
		socket.close();
	}
}
