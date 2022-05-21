package com.cpos.activemq.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cpos.activemq.mqtt.MqttInternet;
import com.cpos.activemq.mqtt.Response;
import com.cpos.activemq.session.ControlCenterSession;
import com.cpos.activemq.struct.Constant;
import com.cpos.activemq.task.TaskBase;
import com.cpos.activemq.task.impl.Step1_Login;
import com.cpos.activemq.task.impl.Step2_RequestDeviceList;
import com.cpos.activemq.util.ZLIB;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KeepAliveService extends Thread {
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

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
		ControlCenterSession cSession = ControlCenterSession.create(username, password, deviceId, deviceName);
		step1Login.request(cSession);
		TaskBase.waitUntilFree(cSession);
		
		step2RequestDeviceList.request(cSession, new Response() {
			@Override
			public void OnMessage(String msg) throws Exception {
				
			}
		});
		
		
		
		
		
	}

	@Scheduled(fixedRate = 10000)
	public void scheduleTaskWithFixedRate() {
		log.info("Fixed Rate Task: Current Time - {}", formatter.format(LocalDateTime.now()));
	}
}
