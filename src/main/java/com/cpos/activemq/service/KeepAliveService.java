package com.cpos.activemq.service;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import com.cpos.activemq.media.Response;
import com.cpos.activemq.session.ControlCenterSession;
import com.cpos.activemq.struct.Constant;
import com.cpos.activemq.task.TaskBase;
import com.cpos.activemq.task.impl.Step1_Login;
import com.cpos.activemq.task.impl.Step2_RequestDeviceList;
import com.cpos.activemq.util.ZLIB;
import com.cpos.activemq.websocket.handler.CposPhoneWebsocketHandler;
import com.cpos.net.MqttInternet;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KeepAliveService extends Thread {
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	@Autowired
	CposPhoneWebsocketHandler cposHandler;
	
	@Autowired
	Step1_Login step1Login;

	@Autowired
	Step2_RequestDeviceList step2RequestDeviceList;

	@PostConstruct
	public void init() throws Exception {
		log.info("KeepAliveService init");
		log.info("Test encode function: {}",
				new String(ZLIB.decompress(ZLIB.compress("{\"key\":\"value\"}".getBytes()))));
//		TaskBase.request("test_queue", "test_queue", "test_message", new Response() {
//			@Override
//			public void OnMessage(String msg) throws Exception {
//				// TODO Auto-generated method stub
//				log.info("Test message queue: response:[{}]", msg);
//			}
//		});

		String username = "cpostest", password = "test";
		int deviceId = 3;
		String deviceName = "android";
		String loginResultCode = cposHandler.mixTextHandler(String.format("Login;%s;%s;%d;%s",username, password, deviceId, deviceName));
		log.info("Login result Code:"+loginResultCode);
		
//		ControlCenterSession cSession = ControlCenterSession.create(username, password, deviceId, deviceName);
//		step1Login.request(cSession);
		
		String devideList = cposHandler.mixTextHandler(String.format("DeviceList;%s;%s;%d;%s",username, password, deviceId, deviceName));
		log.info("Device list:"+devideList);
		
//		Thread.sleep(5000);
//
//		WebSocketClient client = new StandardWebSocketClient();
//
//		WebSocketStompClient stompClient = new WebSocketStompClient(client);
//		stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//
//		new Thread() {
//
//			@Override
//			public synchronized void start() {
//				StompSessionHandler sessionHandler = new MyStompSessionHandler();
//
//				log.info("Point 1");
//				stompClient.connect("ws://127.0.0.1:8081/random_text", sessionHandler);
//				stompClient.start();
//				log.info("Point 2");
//			}
//			
//		}.start();
//		
//		log.info("Point 3");
	}

	class MyStompSessionHandler implements StompSessionHandler {
		
		
		
		@Override
		public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
//			session.subscribe("/topic/messages", this);
//			session.send("/app/chat", getSampleMessage());
			log.info("Websocket connected");
			session.send("/random_text", "Hello");
		}

		@Override
		public void handleFrame(StompHeaders headers, Object payload) {
//			Message msg = (Message) payload;
//			log.info("Received : " + msg.getText() + " from : " + msg.getFrom());
		}

		@Override
		public Type getPayloadType(StompHeaders headers) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
				Throwable exception) {
			// TODO Auto-generated method stub
			log.error("Websocket error ",exception);
		}

		@Override
		public void handleTransportError(StompSession session, Throwable exception) {
			// TODO Auto-generated method stub
			
		}
	}

	@Scheduled(fixedRate = 10000)
	public void scheduleTaskWithFixedRate() {
		log.info("Fixed Rate Task: Current Time - {}", formatter.format(LocalDateTime.now()));
	}
}
