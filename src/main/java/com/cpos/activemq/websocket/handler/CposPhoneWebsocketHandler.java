package com.cpos.activemq.websocket.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.cpos.activemq.media.Response;
import com.cpos.activemq.session.ControlCenterSession;
import com.cpos.activemq.task.TaskBase;
import com.cpos.activemq.task.impl.Step1_Login;
import com.cpos.activemq.task.impl.Step2_RequestDeviceList;
import com.cpos.activemq.websocket.DeviceWebsocketClientsController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CposPhoneWebsocketHandler extends AbstractWebSocketHandler {

	@Autowired
	Step1_Login step1Login;

	@Autowired
	Step2_RequestDeviceList step2RequestDeviceList;

	@Autowired
	private DeviceWebsocketClientsController deviceWebsocketClientsController;
	
	HashMap<String, WebSocketSession> sessionMap = new HashMap<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		deviceWebsocketClientsController.addSession(session);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		super.handleTextMessage(session, message);
		String mix = new String(message.getPayload());
		String response = mixTextHandler(mix);
		TextMessage textMessage = new TextMessage(response);
		session.sendMessage(textMessage);
		
	}

//	@Override
//	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
////		super.handleMessage(session, message);
//		String payload = "Server received message: " + message.getPayload();
//		TextMessage textMessage = new TextMessage(payload);
//		session.sendMessage(textMessage);
//	}

	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
		super.handleBinaryMessage(session, message);
		String payload = "Server received binary: " + message.getPayload();
		TextMessage textMessage = new TextMessage(payload);
		session.sendMessage(textMessage);
	}

//	@Override
//	protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
////		super.handlePongMessage(session, message);
//
//		String payload = "Server received pong: " + message.getPayload();
//		TextMessage textMessage = new TextMessage(payload);
//		session.sendMessage(textMessage);
//	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
		deviceWebsocketClientsController.removeSession(session.getId());
	}

	public String mixTextHandler(String mix) throws Exception {
		log.info("Received mixed code "+mix);
		String[] eles = mix.split(";");
		if (eles.length == 5) {
			String command = eles[0];
			String username = eles[1];
			String password = eles[2];
			int deviceId = Integer.parseInt(eles[3]);
			String deviceName = eles[4];
			ControlCenterSession sCenterSession = new ControlCenterSession(username, password, deviceId, deviceName);
			
			if (command.equals("Login")) {
				return step1Login.requestResultCode(sCenterSession);
			} else if (command.equals("DeviceList")) {
				if (step1Login.requestResultCode(sCenterSession).equals("1")) {
					TaskBase.waitUntilFree(sCenterSession);
					return step2RequestDeviceList.requestDeviceList(sCenterSession);
				}
			} else {
				log.warn("Cannot found element from  mix text first element.");
			}
		} else {
			log.warn("The mix text lenght cannot be "+eles.length);
		}
		return "No response";
	}
}
