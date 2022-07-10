package com.cpos.mqtt.app3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ContentInfoCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cpos.mqtt.app3.io.PhoneHandleService;
import com.cpos.mqtt.app3.old.CposMqttClient;
import com.cpos.mqtt.app3.thread.GPMQ;
import com.cpos.mqtt.app3.thread.KeepAlive;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

import tech.gusavila92.websocketclient.WebSocketClient;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getCanonicalName();

    private WebSocketClient webSocketClient;
    public MainActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("System","OnCreate begin ===============================");

        VoiceController voiceController = new VoiceController(this);

        if (voiceController.isCallAllow()) {
            Toast.makeText(this, "Call allowed", Toast.LENGTH_SHORT);
        }

        new Thread() {
            @Override
            public void run() {
                voiceController.recordAndPlay();
            }
        }.start();

        createWebSocketClient();
        Log.i("System","OnCreate end ===============================");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        setDeviceList("device 1;device 2;device 3");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setDeviceList(String txt) {

        System.out.println(txt);
        String[] deviceStrList = txt.split(";");
        ArrayList deviceArr = new ArrayList<String>();
        for (String deviceStr: deviceStrList) {
            deviceArr.add(deviceStr);
        }
        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(
            this
            ,android.R.layout.simple_list_item_1
            ,deviceArr
        );

        ListView deviceList = (ListView)findViewById(R.id.deviceListView);
        deviceList.setAdapter(listAdapter);

//        ArrayList<View> views = new ArrayList<>();
//        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.vertical_base);
//
//        TextView text=new TextView(this);
//        text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//        text.setText("Gate 1.1");
//        views.add(text);
//
//        text=new TextView(this);
//        text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//        text.setText("Gate 1.2");
//        views.add(text);
//
//        text=new TextView(this);
//        text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//        text.setText("Gate 2.1");
//        views.add(text);
//
//        text=new TextView(this);
//        text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//        text.setText("Gate 2.2");
//        views.add(text);
//
//        linearLayout.addTouchables(views);
//
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    
    private void createWebSocketClient() {
        Log.i("System","createWebSocketClient begin ===============================");
        URI uri;
        try {
            // Connect to local host
            uri = new URI("ws://192.168.8.71:8081/cpos");
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.i("WebSocket", "Session is starting");
                webSocketClient.send("Login;cpostest;test;3;android");
            }

            @Override
            public void onTextReceived(String s) {
                Log.i("WebSocket", "Message received:["+s+"]");
//                final String message = s;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try{
//                            TextView textView = findViewById(R.id.animalSound);
//                            textView.setText(message);
//                        } catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//                });
            }

            @Override
            public void onBinaryReceived(byte[] data) {
            }

            @Override
            public void onPingReceived(byte[] data) {
            }

            @Override
            public void onPongReceived(byte[] data) {
            }

            @Override
            public void onException(Exception e) {
                Log.e("Error",e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onCloseReceived() {
                Log.i("WebSocket", "Closed ");
            }
        };

        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();

        Log.i("System","createWebSocketClient end ===============================");
    }
}