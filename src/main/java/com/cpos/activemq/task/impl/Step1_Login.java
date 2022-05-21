package com.cpos.activemq.task.impl;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cpos.activemq.mqtt.MqttInternet;
import com.cpos.activemq.session.ControlCenterSession;
import com.cpos.activemq.task.TaskBase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Step1_Login extends TaskBase {
	
	public Step1_Login() {
		super(TaskType.JSON);
	}

	@Override
	protected String requestJson(ControlCenterSession session) {
		return "[{"
					+ "\"username\":\"" + session.getUsername() + "\","
					+ "\"password\":\""+session.getPassword()+"\","
					+ "\"device_type\":\"6\","
					+ "\"device_id\":\"" + session.getDeviceId() + "\","
					+ "\"device_name\":\""+session.getDeviceName()+"\","
					+ "\"auto_login\":\"0\","
					+ "\"password_type\":\"0\","
					+ "\"msg_type\":\"16\""
				+ "}]";
	}

	@Override
	protected void responseJson(ControlCenterSession session, String msg) throws Exception {
		log.info("Step1_Login response:{}", msg);
		try {
			if (msg.charAt(msg.length()-1) == 0x00) {
				msg = msg.substring(0, msg.length()-1);
			}
			msg = msg.replaceAll("\n", "").replaceAll("\r", "");
			
			JSONObject item = jsonStringtoJsonObjList(msg).get(0);
	        if ( item.get("result").equals("1") && item.get("msg_type").equals("16") ) {
	        	log.info("Login success");
	        } else {
	        	log.warn("Login failed");
	        }
		} catch (Exception e) {
			log.error("responseJson", e);
		}
	}
}
