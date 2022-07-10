package com.cpos.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

import com.cpos.activemq.media.Response;
import com.cpos.activemq.media.SendingMedia;
import com.cpos.activemq.session.ControlCenterSession;
import com.cpos.activemq.struct.Constant;

public class UdpInternet extends Password {

	public static void send(byte[] bytes) throws IOException {
		InetAddress address = InetAddress.getByName(SERVER_IP);
		DatagramSocket socket = new DatagramSocket();
		DatagramPacket request = new DatagramPacket(bytes, bytes.length, address, UDP_PORT);
		socket.send(request);

		socket.close();
	}

	public static void startCallThread(final ControlCenterSession session, final Response response,
			final SendingMedia sendingMedia) {

		try {
//			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			final DatagramSocket clientSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName(SERVER_IP);
			new Thread() {
				@Override
				public void run() {
					try {
						byte[] sendData = new byte[Constant.UDP_IO_BYTE_ARRAY_LEN];
						while (session.getCallingDevice() != null) {
//							String sentence = inFromUser.readLine();
							sendData = sendingMedia.getPayLoad(); //sentence.getBytes();
							DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress,
									UDP_PORT);
							clientSocket.send(sendPacket);
						}
						clientSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};

			new Thread() {
				@Override
				public void run() {
					try {
						byte[] receiveData = new byte[Constant.UDP_IO_BYTE_ARRAY_LEN];
						while (session.getCallingDevice() != null) {
							DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
							clientSocket.receive(receivePacket);
							response.OnPayLoad(receivePacket.getData());
//							String modifiedSentence = new String(receivePacket.getData());
//							System.out.println("FROM SERVER:" + modifiedSentence);
						}
						clientSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
		} catch (IOException e) {
			e.printStackTrace();
		}
//		new Response();
	}
}
