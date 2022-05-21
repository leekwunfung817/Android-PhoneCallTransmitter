package com.cpos.activemq.mqtt;

public class Response {
	public void OnPayLoad(byte[] bytes) throws Exception {
		OnMessage(new String(bytes));
	}

	public void OnMessage(String msg) throws Exception {

	}

}
