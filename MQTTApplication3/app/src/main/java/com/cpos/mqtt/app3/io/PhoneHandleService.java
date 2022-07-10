package com.cpos.mqtt.app3.io;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.cpos.mqtt.app3.old.MainActivityOld;
import com.cpos.mqtt.app3.thread.GPMQ;
import com.cpos.mqtt.app3.MainActivity;
import com.cpos.mqtt.app3.old.CposMqttClient;
import com.cpos.mqtt.app3.VoiceController;

import org.eclipse.paho.client.mqttv3.MqttException;

public class PhoneHandleService extends Service {

    final MainActivityOld mainActivity;
    final Context context;

    public PhoneHandleService(MainActivityOld mainActivity) throws MqttException {
        this.mainActivity = mainActivity;
        context = mainActivity.getApplicationContext();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();

    }
    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "CPOS Phone Service Started", Toast.LENGTH_LONG).show();
        {
            mainActivity.mqttClient.connectMQTT();
            String testTopic = "test-dev-android";
            {
                try {
                    mainActivity.mqttClient.subscribeTopic(testTopic, new CposMqttClient.CallBack() {
                        @Override
                        public void OnMessage(byte[] payload) {
                            GPMQ.INTERNAL_MESSAGE_QUEUE.add(new VoiceController.Pair<>(GPMQ.QUEUE_KEY.MQTT_CALL_IN, payload));
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                mainActivity.mqttClient.publishMessage(testTopic, "No one wants to see you.");
            }
        }
        {
            VoiceController voiceController = new VoiceController(null);
            VoiceController.Pair<short[], Integer> pair = voiceController.getVoice();
            short[] sounds = pair.first;
            String soundStr = VoiceController.shortArrToStr(sounds);

        }
    }
    @Override
    public void onDestroy() {
        Toast.makeText(this, "CPOS Phone Service Stopped", Toast.LENGTH_LONG).show();
    }
}
