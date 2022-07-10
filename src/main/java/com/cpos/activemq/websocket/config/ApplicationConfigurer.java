package com.cpos.activemq.websocket.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.cpos.activemq.websocket.handler.CposPhoneWebsocketHandler;
import com.cpos.activemq.websocket.handler.SimpleServerWebSocketHandler;

@Configuration
@EnableWebSocket
public class ApplicationConfigurer implements WebSocketConfigurer {

	@Autowired
	SimpleServerWebSocketHandler simpleServerWebSocketHandler;

	@Autowired
	CposPhoneWebsocketHandler cposPhoneWebsocketHandler;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
		webSocketHandlerRegistry.addHandler(simpleServerWebSocketHandler, "/echo").setAllowedOrigins("*");
		webSocketHandlerRegistry.addHandler(cposPhoneWebsocketHandler, "/cpos").setAllowedOrigins("*");
	}
}
