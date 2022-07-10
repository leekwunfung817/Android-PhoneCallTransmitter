package com.cpos.mqtt.app3.thread;

import android.util.Log;

import com.cpos.mqtt.app3.MainActivity;
import com.cpos.mqtt.app3.VoiceController;
import com.cpos.mqtt.app3.old.MainActivityOld;
import com.cpos.mqtt.app3.helper.Helper;

public class KeepAlive extends Thread {
    private final String TAG = this.getClass().getCanonicalName();

    final MainActivityOld mainActivity;
    public KeepAlive(MainActivityOld mainActivity) {
        this.mainActivity = mainActivity;
    }

    public String report() {
        Log.d(TAG,"mainActivity="+mainActivity);
        Log.d(TAG,"mainActivity.mqttClient="+mainActivity.mqttClient);
        Log.d(TAG,"mainActivity.mqttClient.connected()="+mainActivity.mqttClient.connected());
        String txt = "KeepAlive:";
        txt += "Is MQTT connect:"+mainActivity.mqttClient.connected();
        Log.d(TAG, txt);
        return txt;
    }

    @Override
    public void run() {
        while (true) {
            Helper.delaySecond(3);

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mainActivity.mq.report();
                        report();

                        String datetime = Helper.getCurrentDate();
                        if (!mainActivity.mqttClient.connected()) {
                            GPMQ.INTERNAL_MESSAGE_QUEUE.add(new VoiceController.Pair(GPMQ.QUEUE_KEY.STAT_MQTT, datetime+":KeepAlive: Connect failed"));
                            mainActivity.mqttClient.connectMQTT();
                        } else {
                            GPMQ.INTERNAL_MESSAGE_QUEUE.add(new VoiceController.Pair(GPMQ.QUEUE_KEY.STAT_MQTT, datetime+":KeepAlive: Connect success"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
