package com.cpos.mqtt.app3.thread;

import android.util.Log;

import com.cpos.mqtt.app3.MainActivity;
import com.cpos.mqtt.app3.VoiceController;
import com.cpos.mqtt.app3.old.MainActivityOld;

import java.util.LinkedList;

public class GPMQ extends Thread {
    private final String TAG = this.getClass().getCanonicalName();

    public static final LinkedList<VoiceController.Pair<String, Object>> INTERNAL_MESSAGE_QUEUE = new LinkedList<>();
    public static class QUEUE_KEY {
        public static final String STAT_MQTT = "STAT_MQTT";
        public static final String STAT_ACTION = "STAT_ACTION";
        public static final String STAT_VOICE = "STAT_VOICE";
        public static final String MQTT_CALL_IN = "MQTT_CALL_IN";
        public static final String MQTT_CALL_ANSWER = "MQTT_CALL_ANSWER";
        public static final String MQTT_IN_SOUND = "MQTT_IN_SOUND";
        public static final String MQTT_OUT_SOUND = "MQTT_OUT_SOUND";
    }

    final MainActivityOld mainActivity;
    public GPMQ(MainActivityOld mainActivity) {
        this.mainActivity = mainActivity;
    }

    public static void enqueue(String key, Object obj) {
        INTERNAL_MESSAGE_QUEUE.add(new VoiceController.Pair(key, obj));
    }

    public String report() {
        int waitingMsg = INTERNAL_MESSAGE_QUEUE.size();
        String txt = "GPMQ: Report: Waiting message:"+waitingMsg;
        txt += "GPMQ: Report: Waiting message:"+waitingMsg;
        Log.d(TAG, txt);
        return txt;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (INTERNAL_MESSAGE_QUEUE.size() == 0) {
                continue;
            }
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        VoiceController.Pair pair = INTERNAL_MESSAGE_QUEUE.remove();
                        if (pair.second.getClass() == String.class) {
                            String mess = (String) pair.second;
                            if (pair.first.equals(QUEUE_KEY.STAT_MQTT)) {
                                mainActivity.updateMQTTStatus(mess);
                            } else if (pair.first.equals(QUEUE_KEY.STAT_ACTION)) {
                                mainActivity.updateActionStatus(mess);
                            } else if (pair.first.equals(QUEUE_KEY.STAT_VOICE)) {
                                mainActivity.inputVoiceReport(mess);
                            }
                            Log.i(TAG, "GPMQ"+mess) ;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
