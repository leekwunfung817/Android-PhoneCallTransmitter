package com.cpos.activemq.task.impl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import com.cpos.activemq.bean.CallDevice;
import com.cpos.activemq.mqtt.MqttInternet;
import com.cpos.activemq.mqtt.Response;
import com.cpos.activemq.mqtt.UdpInternet;
import com.cpos.activemq.session.ControlCenterSession;
import com.cpos.activemq.struct.Constant;
import com.cpos.activemq.struct.SoundBytes;
import com.cpos.activemq.struct.SoundInfo;
import com.cpos.activemq.struct.SoundServerExchange;
import com.cpos.activemq.task.TaskBase;
import com.cpos.activemq.util.ZLIB;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Step3_PhoneAnswering extends TaskBase {

	public Step3_PhoneAnswering() {
		super(TaskType.JSON);
	}

	public void waitForAnswer(ControlCenterSession session) {
		new Thread() {
			@Override
			public void run() {
				String receiveTopic = receiveTopic(session);

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
							SoundServerExchange serverExchange = new SoundServerExchange(from, to, null, Constant.CPOS_TEL_FUN_ANSWER);
							SoundBytes.answerMobileToCarPark(null);

						}
					}
				});
			}
		}.run();
	}
	
	public 

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
