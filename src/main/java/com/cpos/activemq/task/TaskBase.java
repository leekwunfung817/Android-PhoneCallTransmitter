package com.cpos.activemq.task;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.security.auth.callback.Callback;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cpos.activemq.mqtt.MqttInternet;
import com.cpos.activemq.mqtt.Response;
import com.cpos.activemq.session.ControlCenterSession;
import com.cpos.activemq.struct.Constant;
import com.cpos.activemq.struct.StaticFormat;
import com.cpos.activemq.util.ZLIB;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskBase {

	public static HashMap<String, TaskBase> DEVICE_RESPONSES = new HashMap<>();

	public enum TaskType {
		JSON, BYTES
	}

	final TaskType taskType;
	final TaskBase _this = this;

	public TaskBase(TaskType taskType) {
		this.taskType = taskType;
	}

	protected String sendTopic(ControlCenterSession session) {
		return StaticFormat.Topic.SERVER_RECEIVE;
	}

	protected String receiveTopic(ControlCenterSession session) {
		return responseTopic(session);
	}
	

	public void response(final ControlCenterSession session, Response callback) throws Exception {
		
	}

	public void request(final ControlCenterSession session) throws Exception {
		request(session, null);
	}

	public void request(final ControlCenterSession session, Response callback) throws Exception {
		if (session.requestingTaskBase != null) {
			log.error("Cannot request twice at the same time.");
			return;
		}
		session.requestingTaskBase = this;
		String receiveTopic = receiveTopic(session);
		String sendTopic = sendTopic(session);
		switch (taskType) {
		case JSON:
			String json = requestJson(session);
			request(session, sendTopic, receiveTopic, json, new Response() {
				@Override
				public void OnMessage(String msg) throws Exception {
					responseJson(session, msg);
					log.debug("OnMessage:{}:{}", receiveTopic, msg);
					if (callback != null) {
						callback.OnMessage(msg);
					}
				}
			});
			break;
		case BYTES:
			request(session, sendTopic, receiveTopic, requestBytes(session), new Response() {
				@Override
				public void OnPayLoad(byte[] payload) throws Exception {
					responseBytes(payload);
					log.info("OnPayLoad:{}:{}", receiveTopic, payload);
					if (callback != null) {
						callback.OnPayLoad(payload);
					}
				}
			});
			break;
		default:
			break;
		}
	}

	private static void request(final ControlCenterSession session, String sendTopic, String receiveTopic,
			String sending, Response callback) throws Exception {
		new Thread() {
			@Override
			public void run() {
				log.info(" ============================= Request Start ============================= ");
				try {
					AtomicBoolean received = new AtomicBoolean(false);
					MqttInternet.MQTT.subscribe(receiveTopic, new Response() {
						@Override
						public void OnPayLoad(byte[] payload) throws Exception {
							received.set(true);
							String msg = new String(ZLIB.decompress(payload), StandardCharsets.ISO_8859_1);
							callback.OnMessage(msg);
							MqttInternet.MQTT.unsubscribe(receiveTopic);
							session.requestingTaskBase = null;
							log.info(" ============================= Request End ============================= ");
						}
					});
					log.info("Publish:{}:{}", sendTopic, sending);
					MqttInternet.MQTT.publish(sendTopic, ZLIB.compress(sending.getBytes(StandardCharsets.ISO_8859_1)));
					Thread.sleep(Constant.TIMEOUT);
					if (!received.get()) {
						log.error("Timeout: {}", receiveTopic);
						MqttInternet.MQTT.unsubscribe(receiveTopic);
						session.requestingTaskBase = null;
						log.error(" ============================= Request End ============================= ");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private static void request(final ControlCenterSession session, String sendTopic, String receiveTopic,
			byte[] sendingBytes, Response callback) throws Exception {
		new Thread() {
			@Override
			public void run() {
				log.info(" ============================= Request Start ============================= ");
				try {
					AtomicBoolean received = new AtomicBoolean(false);
					MqttInternet.MQTT.subscribe(receiveTopic, new Response() {
						@Override
						public void OnPayLoad(byte[] bytes) throws Exception {
							received.set(true);
							callback.OnPayLoad(bytes);
							MqttInternet.MQTT.unsubscribe(receiveTopic);
							session.requestingTaskBase = null;
							log.info(" ============================= Request End ============================= ");
						}
					});
					MqttInternet.MQTT.publish(sendTopic, sendingBytes);
					Thread.sleep(Constant.TIMEOUT);
					if (!received.get()) {
						log.error("Timeout: {}", receiveTopic);
						MqttInternet.MQTT.unsubscribe(receiveTopic);
						session.requestingTaskBase = null;
						log.error(" ============================= Request End ============================= ");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	protected byte[] requestBytes(ControlCenterSession session) throws Exception {
		throw new Exception();
	}

	protected void responseBytes(byte[] bytes) throws Exception {
		throw new Exception();
	}

	protected String requestJson(ControlCenterSession session) throws Exception {
		throw new Exception();
	}

	protected void responseJson(ControlCenterSession session, String msg) throws Exception {
		throw new Exception();
	}

	public List<JSONObject> jsonStringtoJsonObjList(String json) throws ParseException {
		Object ob = new JSONParser().parse(json);
		JSONArray js = (JSONArray) ob;
		return jsonArrayToJsonObjectlist(js);
	}

	public List<JSONObject> jsonArrayToJsonObjectlist(JSONArray data) throws ParseException {
		List<JSONObject> list = (List<JSONObject>) data.stream().map(o -> (JSONObject) o).collect(Collectors.toList());
		return list;
	}

	protected static String requestTopic( // Send to MQTT
			String username, // car park management organization's name
			String device_id // the called/calling device
	) {
		return "cpos/carpark/" + username + "/" + device_id + "/send";
	}

	protected static String responseTopic(ControlCenterSession session) {
		return responseTopic(session.getUsername(), session.getDeviceId());
	}

	protected static String responseTopic(String username, int device_id) {
		return "cpos/carpark/" + username + "/" + device_id + "/receive";
	}

	public static void waitUntilFree(ControlCenterSession cSession) throws InterruptedException {
		int count = 0;
		do {
			Thread.sleep(Constant.TIMEOUT);
			if (count++ > 0) {
				log.warn("The request is too long");
			}
		} while (cSession.requestingTaskBase != null);

	}

}
