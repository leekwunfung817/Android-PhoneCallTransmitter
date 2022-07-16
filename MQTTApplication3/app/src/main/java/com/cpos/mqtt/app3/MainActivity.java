package com.cpos.mqtt.app3;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ContentInfoCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import tech.gusavila92.websocketclient.WebSocketClient;

public class MainActivity extends AppCompatActivity {

    private String keepAliveString = "...KeepAlive...";

    private final String TAG = this.getClass().getCanonicalName();


    private WebSocketClient webSocketClient = null;
    public MainActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("System","OnCreate begin ===============================");


        Button refresh_button = (Button)findViewById(R.id.refresh_button);
        refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createWebSocketClient();
                clearDeviceList();
                try {
                    updateDeviceList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        EditText serverIpAddress = (EditText)findViewById(R.id.server_ip_address);
        EditText usernameInput = (EditText)findViewById(R.id.username);
        EditText passwordInput = (EditText)findViewById(R.id.password);
        serverIpAddress.setText("ws://192.168.8.71:8081/cpos");
        usernameInput.setText("cpostest");
        passwordInput.setText("test");


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
        clearDeviceList();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void clearDeviceList() {
        setDeviceList("Cannot detect any device.");
    }
    private void setDeviceList(String txt) {
        MainActivity mainActivity_this = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                System.out.println(txt);
                String[] deviceStrList = txt.split(";");
                ArrayList deviceArr = new ArrayList<String>();
                for (String deviceStr: deviceStrList) {
                    deviceArr.add(deviceStr);
                }
                ArrayAdapter<String> deviceListAdapter = new ArrayAdapter<String>(
                        mainActivity_this
                        ,android.R.layout.simple_list_item_1
                        ,deviceArr
                );

                ListView deviceList = (ListView)findViewById(R.id.deviceListView);
                deviceList.setAdapter(deviceListAdapter);

            }
        });
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
        if (webSocketClient != null) {
            webSocketClient.close();
            webSocketClient = null;
        }
        Log.i("System","createWebSocketClient begin ===============================");
        URI uri;
        try {
            // Connect to local host
            EditText serverIpAddress = (EditText)findViewById(R.id.server_ip_address);
            uri = new URI(serverIpAddress.getText().toString());
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.i("WebSocket", "Session is starting");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView server_connection_status = (TextView)findViewById(R.id.server_connection_status);
                        server_connection_status.setText("Websocket connected");
                        server_connection_status.setBackgroundColor(Color.GREEN);
                    }
                });
                try {
                    login();
                    updateDeviceList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTextReceived(String s) {
                if (!s.contains(keepAliveString)) {
                    Log.i("WebSocket", "Message received:["+s+"].");
                    String[] eles = s.split(";");
                    if (eles[0].equals("DeviceList")) {
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i=1;i<eles.length;i++) {
                            if (i>1) {
                                stringBuilder.append(";");
                            }
                            HashMap<String, String> map = new HashMap<String, String>();
                            String[] keyVals = eles[i].split(",");
                            for (String keyVal: keyVals) {
                                String[] keyValo = keyVal.split(":");
                                if (keyValo.length == 2) {
                                    String key = keyValo[0];
                                    String val = keyValo[1];
                                    map.put(key, val);
                                }
                            }
                            Device device = new Device(map);
                            stringBuilder.append(device.display());
                        }
                        Log.i("Report","Update device list "+stringBuilder.toString());
                        setDeviceList(stringBuilder.toString());
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"Server return ["+s+"]",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Log.i("Websocket","Keep alive received.");
                    try {
                        updateDeviceList();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView server_connection_status = (TextView)findViewById(R.id.server_connection_status);
                        server_connection_status.setText("Websocket error (disconnected)");
                        server_connection_status.setBackgroundColor(Color.RED);
                    }
                });
            }

            @Override
            public void onCloseReceived() {

                Log.i("WebSocket", "Closed ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView server_connection_status = (TextView)findViewById(R.id.server_connection_status);
                        server_connection_status.setText("Websocket error (disconnected)");
                        server_connection_status.setBackgroundColor(Color.RED);
                    }
                });

            }
        };

        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();

        Log.i("System","createWebSocketClient end ===============================");
    }

    public void login() throws Exception {
        EditText usernameInput = (EditText)findViewById(R.id.username);
        EditText passwordInput = (EditText)findViewById(R.id.password);

        int deviceId = getDeviceId();
        webSocketClient.send("Login;"+usernameInput.getText()+";"+passwordInput.getText()+";"+deviceId+";"+getAndroidVersion()+"-"+deviceId);
    }

    public static String getAndroidVersion() {
        String versionName = "";
        try {
            versionName = String.valueOf(Build.VERSION.RELEASE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public void updateDeviceList() throws Exception {
        EditText usernameInput = (EditText)findViewById(R.id.username);
        EditText passwordInput = (EditText)findViewById(R.id.password);
        int deviceId = getDeviceId();
        webSocketClient.send("DeviceList;"+usernameInput.getText()+";"+passwordInput.getText()+";"+deviceId+";"+getAndroidVersion()+"-"+deviceId);
    }

    public int getDeviceId() throws PackageManager.NameNotFoundException {
        PackageManager pm =  getBaseContext().getPackageManager();
        PackageInfo packageInfo = pm.getPackageInfo(getBaseContext().getPackageName(), PackageManager.GET_PERMISSIONS);
        Date installTime = new Date( packageInfo.firstInstallTime );
        int firstInstallTime = (int) packageInfo.firstInstallTime/100;
        firstInstallTime = firstInstallTime < 0? firstInstallTime*-1:firstInstallTime;
        Log.d("Conversion","First install time:"+packageInfo.firstInstallTime+":"+firstInstallTime);
        return firstInstallTime;
    }

    public void updateDeviceListResponse() {

    }
}