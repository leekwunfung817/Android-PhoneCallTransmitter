package com.cpos.activemq.websocket.handler;

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

import com.cpos.activemq.websocket.DeviceWebsocketClientsController;

@Service
public class SimpleServerWebSocketHandler extends AbstractWebSocketHandler {

//	@Autowired
//	private RandomTicker randomTicker;

//	@Override
//	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
////		randomTicker.addSession(session);
//	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		super.handleTextMessage(session, message);
		String payload = "Server received text: " + message.getPayload();
		TextMessage textMessage = new TextMessage(payload);
		session.sendMessage(textMessage);
	}

	@Override
	public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
		super.handleMessage(session, message);
		String payload = "Server received message: " + message.getPayload();
		TextMessage textMessage = new TextMessage(payload);
		session.sendMessage(textMessage);
	}

	@Override
	protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
		super.handleBinaryMessage(session, message);
		String payload = "Server received binary: " + message.getPayload();
		TextMessage textMessage = new TextMessage(payload);
		session.sendMessage(textMessage);
	}

	@Override
	protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
		super.handlePongMessage(session, message);
		String payload = "Server received pong: " + message.getPayload();
		TextMessage textMessage = new TextMessage(payload);
		session.sendMessage(textMessage);
	}

//	@Override
//	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
////		randomTicker.removeSession(session.getId());
//	}
}
