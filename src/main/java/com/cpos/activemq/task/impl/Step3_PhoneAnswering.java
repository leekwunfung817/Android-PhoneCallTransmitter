package com.cpos.activemq.task.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import com.cpos.activemq.bean.CallDevice;
import com.cpos.activemq.media.Response;
import com.cpos.activemq.session.ControlCenterSession;
import com.cpos.activemq.struct.Constant;
import com.cpos.activemq.struct.SoundBytes;
import com.cpos.activemq.struct.SoundInfo;
import com.cpos.activemq.struct.SoundServerExchange;
import com.cpos.activemq.task.TaskBase;
import com.cpos.activemq.util.ZLIB;
import com.cpos.net.MqttInternet;
import com.cpos.net.UdpInternet;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Step3_PhoneAnswering extends TaskBase {

//	public static final LinkedBlockingQueue<SoundServerExchange> PHONE_ANSWER_DEVICE_QUEUE = new LinkedBlockingQueue<>();
	public static final LinkedBlockingQueue<SoundServerExchange> DEVICE_CALL_PHONE_QUEUE = new LinkedBlockingQueue<>();
	public static final HashMap<String, Thread> CALL_LISTENER_THREAD = new HashMap<String, Thread>();
	


	public Step3_PhoneAnswering() {
		super(TaskType.JSON);
//		answerCallToDevice(1);
	}

	// When mobile push call button
	public void mobileCallDevice(ControlCenterSession session, CallDevice callDevice) {

		try {
			SoundInfo to = new SoundInfo(callDevice);
			SoundInfo from = new SoundInfo(session);

			// Forward calling signal to phone
			SoundServerExchange serverExchange = new SoundServerExchange(from, to, Constant.EMPTY_REC_BUF,
					Constant.CPOS_TEL_FUN_CALL);

			byte[] bytes = SoundBytes.structMobileToCarPark(serverExchange);

			// Sending
			UdpInternet.send(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// call this method when Phone connect - Listen for device calling
	public void waitForCall(ControlCenterSession session) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				String receiveTopic = receiveTopic(session);

				try {
					MqttInternet.MQTT.subscribe(receiveTopic, new Response() {
						@Override
						public void OnMessage(String msg) throws Exception {
							if (msg.charAt(msg.length() - 1) == 0x00) {
								msg = msg.substring(0, msg.length() - 1);
							}
							msg = msg.replaceAll("\n", "").replaceAll("\r", "");

							List<JSONObject> jsonObjects = jsonStringtoJsonObjList(msg);
							JSONObject jsonObject = jsonObjects.get(0);

							int msg_type = (Integer) jsonObject.get("msg_type");
							if (msg_type == 18) { // Receive call
								String udp_ip = (String) jsonObject.get("udp_ip");
								short udp_port = Short.parseShort((String) jsonObject.get("udp_port"));
								String car_park_id = (String) jsonObject.get("car_park_id");
								int device_id = Integer.parseInt((String) jsonObject.get("device_id"));
								int device_type = Integer.parseInt((String) jsonObject.get("device_type"));

								SoundInfo to = new SoundInfo(udp_ip, udp_port, car_park_id, device_id, device_type);
								SoundInfo from = new SoundInfo( //
										udp_ip // Dummy
								, udp_port // Dummy
								, session.getUsername(), session.getDeviceId(), session.getDeviceType());

								// Forward calling signal to phone
								SoundServerExchange serverExchange = new SoundServerExchange(from, to,
										Constant.EMPTY_REC_BUF, Constant.CPOS_TEL_FUN_ANSWER);

								// Wait for answer
								DEVICE_CALL_PHONE_QUEUE.add(serverExchange);

							}
						}
					});
				} catch (MqttPersistenceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MqttException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		CALL_LISTENER_THREAD.put(session.toString(), thread);
		thread.run();
	}

	// When mobile answer the call
	public void mobileAnswerDevice(ControlCenterSession session, CallDevice callDevice) {

		try {
			SoundInfo to = new SoundInfo(callDevice);
			SoundInfo from = new SoundInfo(session);

			// Forward calling signal to phone
			SoundServerExchange serverExchange = new SoundServerExchange(from, to, Constant.EMPTY_REC_BUF,
					Constant.CPOS_TEL_FUN_ANSWER);

			byte[] bytes = SoundBytes.structMobileToCarPark(serverExchange);

			// Sending
			UdpInternet.send(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// When mobile sending sound to device
	public void mobileTransferSoundToDevice(ControlCenterSession session, CallDevice callDevice, byte[] sound) {

		try {
			SoundInfo to = new SoundInfo(callDevice);
			SoundInfo from = new SoundInfo(session);

			// Forward calling signal to phone
			SoundServerExchange serverExchange = new SoundServerExchange(from, to, sound,
					Constant.CPOS_TEL_FUN_DATA);

			byte[] bytes = SoundBytes.structMobileToCarPark(serverExchange);

			// Sending
			UdpInternet.send(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// start a thread for
//	public void answerCallToDevice(int threadCount) {
//		for (int i = 0; i < threadCount; i++) {
//			new Thread() {
//				@Override
//				public void run() {
//					while (true) {
//						try {
//							while (true) {
//								SoundServerExchange serverExchange = PHONE_ANSWER_DEVICE_QUEUE.take();
//
//								// Answer
//								byte[] bytes = SoundBytes.structMobileToCarPark(serverExchange);
//
//								// Sending
//								UdpInternet.send(bytes);
//							}
//
//						} catch (Exception e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//				}
//			}.start();
//		}
//	}

	public void OnMobileDisconnect(ControlCenterSession session) {
		String receiveTopic = receiveTopic(session);
		try {
			MqttInternet.MQTT.unsubscribe(receiveTopic);
		} catch (MqttPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	
//	@Override
//	protected String requestJson(ControlCenterSession session) {
//		if (session.callingDevice == null) {
//			log.error("Calling device not found.");
//			throw new NullPointerException();
//		}
//		CallDevice callingDevice = session.callingDevice;
//		return "[{"
//					+ "\"msg_type\":\"18\","
//					+ "\"car_park_id\":\""+callingDevice.getCardParkId()+"\","
//					+ "\"device_id\":\""+callingDevice.getDeviceId()+"\","
//					+ "\"device_type\":\""+callingDevice.getDeviceType()+"\","
//					+ "\"call_carpark\":\""+callingDevice.getCallCarPark()+"\","
//					+ "\"call_device\":\""+callingDevice.getCallDevice()+"\","
//					+ "\"udp_ip\":\""+callingDevice.getUdpId()+"\","
//					+ "\"udp_port\":\""+callingDevice.getUdpPort()+"\""
//				+ "}]";
//	}
//
//	@Override
//	protected void responseJson(ControlCenterSession session, String msg) throws Exception {
//		if (msg.charAt(msg.length()-1) == 0x00) {
//			msg = msg.substring(0, msg.length()-1);
//		}
//		msg = msg.replaceAll("\n", "").replaceAll("\r", "");
//		log.info("Step2_RequestDeviceList response:{}",msg);
//		
//		List<JSONObject> jsonObjects = jsonStringtoJsonObjList(msg);
//		String[] devices = new String[jsonObjects.size()];
//		for (int i = 0; i < jsonObjects.size(); i++) {
//			String[] parameters = new String[jsonObjects.get(i).size()];
//			int counterParameter = 0;
//			for (Object o : jsonObjects.get(i).entrySet()) {
//				parameters[counterParameter++] = o.toString();
////				log.info("obj {}", o);
//			}
//			String parameterStr = String.join(",", parameters);
////			log.info("Parameters {}", parameterStr);
//			devices[i] = parameterStr;
//		}
//		String deviceStr = String.join("\n", devices);
//		
//		
//		log.info("Devices: {}", deviceStr);
//		
//	}
}
