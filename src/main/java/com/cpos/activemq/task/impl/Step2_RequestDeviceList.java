package com.cpos.activemq.task.impl;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cpos.activemq.session.ControlCenterSession;
import com.cpos.activemq.task.TaskBase;
import com.cpos.net.MqttInternet;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Step2_RequestDeviceList extends TaskBase {
	
	public Step2_RequestDeviceList() {
		super(TaskType.JSON);
	}
	
	@Override
	protected String requestJson(ControlCenterSession session) {
		return "[{"
					+ "\"username\":\"" + session.getUsername() + "\","
					+ "\"device_type\":\"6\","
					+ "\"device_id\":\"" + session.getDeviceId() + "\","
					+ "\"msg_type\":\"17\""
				+ "}]";
	}

	@Override
	protected void responseJson(ControlCenterSession session, String msg) throws Exception {
		if (msg.charAt(msg.length()-1) == 0x00) {
			msg = msg.substring(0, msg.length()-1);
		}
		msg = msg.replaceAll("\n", "").replaceAll("\r", "");
		log.info("Step2_RequestDeviceList response:{}",msg);
		
		List<JSONObject> jsonObjects = jsonStringtoJsonObjList(msg);
		String[] devices = new String[jsonObjects.size()];
		for (int i = 0; i < jsonObjects.size(); i++) {
			String[] parameters = new String[jsonObjects.get(i).size()];
			int counterParameter = 0;
			for (Object o : jsonObjects.get(i).entrySet()) {
				parameters[counterParameter++] = o.toString();
			}
			String parameterStr = String.join(",", parameters);
			devices[i] = parameterStr;
		}
		String deviceStr = String.join("\n", devices);
		log.info("Devices: {}", deviceStr);
		
	}
	
	public String requestDeviceList(ControlCenterSession session) throws Exception {
		List<JSONObject> list = jsonStringtoJsonObjList(request(session));
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			stringBuilder.append((stringBuilder.length()==0?"":";"));
			
			JSONObject o = list.get(i);
			o.keySet().stream().forEach(key -> stringBuilder.append(
					(stringBuilder.length()==0?"":",")+key+":"+((String)o.get(key)).replace(":", "-").replace(",", "-")
					
					));
		}
		return "DeviceList;"+stringBuilder.toString();
	}
}
