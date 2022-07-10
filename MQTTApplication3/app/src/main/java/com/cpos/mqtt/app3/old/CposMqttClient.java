package com.cpos.mqtt.app3.old;
// https://www.alibabacloud.com/help/en/doc-detail/146630.htm
// https://wildanmsyah.wordpress.com/2017/05/11/mqtt-android-client-tutorial/

import android.content.Context;
import android.util.Log;

import com.cpos.mqtt.app3.MainActivity;
import com.cpos.mqtt.app3.helper.Helper;
import com.cpos.mqtt.app3.thread.GPMQ;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.internal.security.SSLSocketFactoryFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

public class CposMqttClient { // extends AppCompatActivity {

    private final String TAG = this.getClass().getCanonicalName();


    public static final String TOPIC = "KEEP_ALIVE";

    public static final String MQTT_URL = "ssl://115.160.170.198:1394";
    public static final String MQTT_USERNAME = "paho1609138314988000000";
    public static final String MQTT_PWD = "paho1609138314988000000";

    public static final String SSL_TYPE = "PKCS12";
    public static final String KEY_STORE_PATH = "client_key.keystore";
    public static final String TRUST_STORE_PATH = "client_trust.truststore";
    public static final String SSL_PWD = "client1392419926";


    private MainActivityOld mainActivity;
    private Context applicationContext;
    public final IMqttClient client;

    private HashMap<String, CallBack> callBackHashMap = new HashMap<>();
    String clientId = CposMqttClient.generateClientId();

    public CposMqttClient(MainActivityOld mainActivity) throws MqttException {
        this.mainActivity = mainActivity;
        this.applicationContext = mainActivity.getApplicationContext();
        client = mainActivity.client;
    }

    public static String generateClientId() {
        return UUID.randomUUID().toString();
    }

    public boolean connected() {
        boolean isConnect=true;
        if (!client.isConnected()) {
            isConnect = false;
        }
        if (!isConnect) {
            GPMQ.enqueue(GPMQ.QUEUE_KEY.STAT_ACTION, Helper.getCurrentDate()+"Not connect yet.");
//            Toast.makeText(applicationContext, "Failed to connect to MQTT ", Toast.LENGTH_SHORT).show();
//        } else {
//            GPMQ.enqueue(GPMQ.QUEUE_KEY.STAT_ACTION, Helper.getCurrentDate()+"Connected.");
        }
        return isConnect;
    }

    public void connectMQTT() {
        if (connected()) {
            return;
        }
        try {
            GPMQ.enqueue(GPMQ.QUEUE_KEY.STAT_ACTION, "Connecting......");
            GPMQ.enqueue(GPMQ.QUEUE_KEY.STAT_ACTION, "Connect preparing......");

            Log.d(TAG, "mainActivity.clientKeyPath exists:"+new File(mainActivity.clientKeyPath).exists());
            Log.d(TAG, "mainActivity.clientTrustPath exists:"+new File(mainActivity.clientTrustPath).exists());
            // MQTT development reference
            MqttConnectOptions options = new MqttConnectOptions();
            {
                options.setUserName(MQTT_USERNAME);
                options.setPassword(MQTT_PWD.toCharArray());
                options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);

                {
                    Properties sslClientProperties = new Properties();
                    sslClientProperties.setProperty(SSLSocketFactoryFactory.KEYSTORETYPE, SSL_TYPE);
                    sslClientProperties.setProperty(SSLSocketFactoryFactory.KEYSTORE, mainActivity.clientKeyPath);
                    sslClientProperties.setProperty(SSLSocketFactoryFactory.KEYSTOREPWD, SSL_PWD);
                    sslClientProperties.setProperty(SSLSocketFactoryFactory.TRUSTSTORETYPE, SSL_TYPE);
                    sslClientProperties.setProperty(SSLSocketFactoryFactory.TRUSTSTORE, mainActivity.clientTrustPath);
                    sslClientProperties.setProperty(SSLSocketFactoryFactory.TRUSTSTOREPWD, SSL_PWD);
                    sslClientProperties.put(SSLSocketFactoryFactory.CLIENTAUTH, true);
                    options.setSSLProperties(sslClientProperties);
                }
            }
//        return;
//        android.support.v4.content.LocalBroadcastManager l;
            GPMQ.enqueue(GPMQ.QUEUE_KEY.STAT_ACTION, "Token preparing......");
            {
                client.setCallback(new MqttCallbackExtended() {
                    @Override
                    public void connectComplete(boolean b, String s) {
                        GPMQ.enqueue(GPMQ.QUEUE_KEY.STAT_MQTT, Helper.getCurrentDate() + "Connect complete.");
                    }

                    @Override
                    public void connectionLost(Throwable throwable) {
                        GPMQ.enqueue(GPMQ.QUEUE_KEY.STAT_MQTT, Helper.getCurrentDate() + "Connection list.");
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                        GPMQ.enqueue(GPMQ.QUEUE_KEY.STAT_MQTT, Helper.getCurrentDate() + "Message arrived.");
                        CallBack callBack = callBackHashMap.get(topic);
                        if (callBack != null) {
                            callBack.OnMessage(mqttMessage);
                        }
//                dataReceived.setText(mqttMessage.toString());
//                mChart.addEntry(Float.valueOf(mqttMessage.toString()));
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {
                        GPMQ.enqueue(GPMQ.QUEUE_KEY.STAT_MQTT, Helper.getCurrentDate() + "Delivery complete.");
                    }

                });

                GPMQ.enqueue(GPMQ.QUEUE_KEY.STAT_ACTION, "Connect by token......");
                client.connect(options);

                System.out.println("subscribe");
                new Thread() {
                    @Override
                    public void run() {
                        while (true) {
                            try {
                                Thread.sleep(1000);
                                System.out.println("Publisher ID: " + clientId + " connected:" + client.isConnected());
                                client.publish(TOPIC, new MqttMessage(TOPIC.getBytes()));
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }
                    }
                }.start();
            }
            GPMQ.enqueue(GPMQ.QUEUE_KEY.STAT_ACTION, Helper.getCurrentDate() + "Waiting for callback.");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Connect failed with exception.", e);
        }
    }

    public void publishMessage(String topic,String message) {
        GPMQ.enqueue(GPMQ.QUEUE_KEY.STAT_ACTION, Helper.getCurrentDate()+"Sending message.");
        publishMessage(topic, message.getBytes());
    }
    public void publishMessage(String topic,byte[] payload) {
        if (!connected()) {
            return;
        }
        GPMQ.enqueue(GPMQ.QUEUE_KEY.STAT_ACTION, Helper.getCurrentDate()+"publish message "+topic);
        String PUB_TOPIC = topic;
        IMqttClient mqttAndroidClient = client;
        try {
            if (!mqttAndroidClient.isConnected()) {
                GPMQ.enqueue(GPMQ.QUEUE_KEY.STAT_ACTION, Helper.getCurrentDate()+"Connecting MQTT, because connect not working");
                mqttAndroidClient.connect();
            }

            MqttMessage message = new MqttMessage();
            message.setPayload(payload);
            message.setQos(0);
            mqttAndroidClient.publish(PUB_TOPIC, message);
        } catch (MqttException e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
    }
    public void subscribeTopic(String topic,CallBack callBack) throws MqttException {
        if (!connected()) {
            GPMQ.enqueue(GPMQ.QUEUE_KEY.STAT_MQTT, Helper.getCurrentDate()+ "Connecting MQTT, because connect not working");
            return;
        }
        GPMQ.enqueue(GPMQ.QUEUE_KEY.STAT_ACTION, Helper.getCurrentDate()+"subscribe topic "+topic);
        client.subscribe(TOPIC,new IMqttMessageListener() {
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                byte[] payload = message.getPayload();
                String strMsg = new String(payload);
                System.out.println(strMsg);
            }
        });
    }

    public static class CallBack {
        public void OnMessage(MqttMessage mqttMessage) {
            OnMessage(mqttMessage.getPayload());
        }
        public void OnMessage(byte[] payload) {
            OnMessage(new String(payload));
        }
        public void OnMessage(String message) {

        }
    }

}
