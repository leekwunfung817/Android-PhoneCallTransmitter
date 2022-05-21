package com.cpos.activemq.mqtt;
//package com.cpos.activemq.activemq.activemq;
//
//import java.util.UUID;
//import java.util.concurrent.Callable;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
//import javax.annotation.PostConstruct;
//
//import org.eclipse.paho.client.mqttv3.IMqttClient;
//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.springframework.stereotype.Component;
//
//@Component
//public class Mqtt {
//	@PostConstruct
//	public void init() throws MqttException, InterruptedException {
//		String publisherId = UUID.randomUUID().toString();
//
//	    System.out.println("Publisher ID: "+publisherId+"");
//		
//		
//		final IMqttClient mqttClient = new MqttClient("tcp://localhost:1883", publisherId);
//		
//		
//		
//		MqttConnectOptions options = new MqttConnectOptions();
//		options.setAutomaticReconnect(true);
//		options.setCleanSession(true);
//		options.setConnectionTimeout(10);
//		options.setUserName("admin");
//		options.setPassword("admin".toCharArray());
//		mqttClient.connect(options);
//		
//		
//		CountDownLatch receivedSignal = new CountDownLatch(10);
//	    System.out.println("subscribe");
//		mqttClient.subscribe(EngineTemperatureSensor.TOPIC, (topic, msg) -> {
//		    byte[] payload = msg.getPayload();
//		    System.out.println(new String(payload));
//		    receivedSignal.countDown();
//		});    
//		new Thread() {
//			@Override
//			public void run() {
//				while(true) {
//					try {
//						Thread.sleep(1000);
//					    System.out.println("Publisher ID: "+publisherId+" connected:"+mqttClient.isConnected());
//						mqttClient.publish(EngineTemperatureSensor.TOPIC, new MqttMessage("Hello Ivan".getBytes()));
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				}
//			}
//		}.start();
//		receivedSignal.await(1, TimeUnit.MINUTES);
//	}
//
//	public class EngineTemperatureSensor implements Callable<Void> {
//
//		// ... private members omitted
//		final IMqttClient client;
//		public static final String TOPIC = "Hello";
//		Integer rnd = 0;
//
//		public EngineTemperatureSensor(IMqttClient client) {
//			this.client = client;
//		}
//
//		@Override
//		public Void call() throws Exception {
//			if (!client.isConnected()) {
//				return null;
//			}
//			MqttMessage msg = readEngineTemp();
//			msg.setQos(0);
//			msg.setRetained(true);
//			client.publish(TOPIC, msg);
//			return null;
//		}
//
//		private MqttMessage readEngineTemp() {
//			double temp = 80 + 20.0;
//			byte[] payload = String.format("T:%04.2f", temp).getBytes();
//			return new MqttMessage(payload);
//		}
//	}
//}
