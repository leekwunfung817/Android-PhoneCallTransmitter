package com.cpos.mqtt.app3.io;

import com.cpos.mqtt.app3.old.CposMqttClient;

public class ReceiveCall {
    public final static CposMqttClient.CallBack STEP_2_RECEIVE_CALL_REQUEST = new CposMqttClient.CallBack() {
        @Override
        public void OnMessage(String message) {

        }
    };
}
