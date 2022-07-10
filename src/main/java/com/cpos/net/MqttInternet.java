package com.cpos.net;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.internal.security.SSLSocketFactoryFactory;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.cpos.activemq.media.Response;
import com.cpos.net.Password;

@Slf4j
public class MqttInternet extends Password {
	
	public static MqttInternet MQTT = init();

	@Getter
	public IMqttClient mqttClient;
	MqttConnectOptions options;
	
	public MqttInternet() throws MqttException {

		String userDir = System.getProperty("user.dir");
		log.debug("userDir:" + userDir);
//		new MqttDefaultFilePersistence();

		options = new MqttConnectOptions();
		options.setAutomaticReconnect(true);
		options.setCleanSession(true);
		options.setConnectionTimeout(10);
		options.setUserName(MQTT_USERNAME);
		options.setPassword(MQTT_PWD.toCharArray());

		Properties properties = new Properties();
		properties.put(SSLSocketFactoryFactory.TRUSTSTORE, TRUST_STORE_PATH);
		properties.put(SSLSocketFactoryFactory.KEYSTOREPWD, SSL_PWD);
		properties.put(SSLSocketFactoryFactory.KEYSTORETYPE, SSL_TYPE);
		properties.put(SSLSocketFactoryFactory.KEYSTORE, KEY_STORE_PATH);
		properties.put(SSLSocketFactoryFactory.KEYSTOREPWD, SSL_PWD);
		properties.put(SSLSocketFactoryFactory.KEYSTORETYPE, SSL_TYPE);
		properties.put(SSLSocketFactoryFactory.CLIENTAUTH, true);
		options.setSSLProperties(properties);

		
		String publisherId = UUID.randomUUID().toString();
		log.debug("Publisher ID: " + publisherId + "");
		mqttClient = new MqttClient(MQTT_URL, publisherId);
		connect();
	}

	public static MqttInternet init()  {

		try {
			MqttInternet mqttInternet = new MqttInternet();
			return mqttInternet;
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new NullPointerException();
		}
		
//		mqttClient.connect(options);
//		CountDownLatch receivedSignal = new CountDownLatch(10);
//		System.out.println("subscribe");
//		mqttClient.subscribe(TOPIC, (topic, msg) -> {
//			byte[] payload = msg.getPayload();
//			System.out.println(new String(payload));
//			receivedSignal.countDown();
//		});
//		new Thread() {
//			@Override
//			public void run() {
//				while (true) {
//					try {
//						Thread.sleep(1000);
//						System.out.println("Publisher ID: " + publisherId + " connected:" + mqttClient.isConnected());
//						publish(TOPIC, TOPIC);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//				}
//			}
//		}.start();

	}

	public void connect() throws MqttSecurityException, MqttException {
		if (!mqttClient.isConnected()) {
			mqttClient.connect(options);
		}
	}

	public void subscribe(String topic, Response response) throws MqttPersistenceException, MqttException {
		log.info("Subscript {}", topic);
		mqttClient.subscribe(topic, (rtopic, msg) -> {
			byte[] payload = msg.getPayload();
			response.OnPayLoad(payload);
		});
	}
	public void unsubscribe(String... topic) throws MqttPersistenceException, MqttException {
		log.info("Unsubscript {}", topic);
		mqttClient.unsubscribe(topic);
	}

	public void publish(String topic, String msg) throws MqttPersistenceException, MqttException {
		log.info("publish {} > {}", topic, msg);
		publish(topic, msg.getBytes());
	}

	public void publish(String topic, byte[] bytes) throws MqttPersistenceException, MqttException {
		mqttClient.publish(topic, new MqttMessage(bytes));
	}

}
