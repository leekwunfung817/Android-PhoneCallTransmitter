//package com.cpos.activemq.websocket;
//
//import java.security.Principal;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.simp.annotation.SendToUser;
//import org.springframework.stereotype.Controller;
//
//@Controller
//public class WebsocketController {
//
////	@Autowired
////	private SimpMessageSendingOperations messagingTemplate;
//
//	@MessageMapping("/message")
//	@SendToUser("/queue/reply")
//	public byte[] processMessageFromClient(@Payload byte[] message, Principal principal) throws Exception {
////		String name = new Gson().fromJson(message, Map.class).get("name").toString();
//		//messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/reply", name);
////		return name;
//		System.out.println("Receive message: "+new String(message));
//		return "Reply".getBytes();
//	}
//	
//	@MessageExceptionHandler
//    @SendToUser("/queue/errors")
//    public String handleException(Throwable exception) {
//        return exception.getMessage();
//    }
//}
