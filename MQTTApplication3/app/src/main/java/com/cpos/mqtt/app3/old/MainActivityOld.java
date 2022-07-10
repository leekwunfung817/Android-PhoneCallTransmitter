package com.cpos.mqtt.app3.old;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.cpos.mqtt.app3.R;
import com.cpos.mqtt.app3.io.PhoneHandleService;
import com.cpos.mqtt.app3.thread.GPMQ;
import com.cpos.mqtt.app3.thread.KeepAlive;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

public class MainActivityOld extends AppCompatActivity {

    public static final int CPOS_TEL_FUN_FREE = 0;
    public static final int CPOS_TEL_FUN_CALL = 1;
    public static final int CPOS_TEL_FUN_HANG_UP = 2;
    public static final int CPOS_TEL_FUN_ANSWER = 3;
    public static final int CPOS_TEL_FUN_DATA = 4;
    public static final int CPOS_TEL_FUN_BUSY = 5;
    public static final int CPOS_TEL_FUN_CONNECT = 6;
    public static final int CPOS_TEL_FUN_INCOMING = 7;
    public static final int CPOS_REC_BUF_SIZE =  600;

    private final String TAG = this.getClass().getCanonicalName();

    public static class Topic {
        public static final String SUBSCRIPT_CHANNEL = "cpos/carpark/admin/receive";
        public static final String ANSWER_CALL = "cpos/carpark/admin/send";
        public static String LOGIN(String username,String device_id) {
            return "cpos/carpark/"+username+"/"+device_id+"/receive";
        }
        public static String RECEIVE_FROM_CARPARK(String car_park_id, String device_id) {
            return "cpos/carpark/"+car_park_id+"/"+device_id+"/send";
        }
    }

    public static class Json {
        public static String LOGIN(String username,String device_id) {
            return "[{\"username\":\""+username+"\",\"password\":\"psd\",\"device_type\":\"6\",\"device_id\":\""+device_id+"\",\"device_name\":\"android\",\"auto_login\":\"0\",\"password_type\":\"0\",\"msg_type\":\"16\"}]";
        }
        public static String LOGIN_RESPONSE = "[{\"result\":\"1\"}]";
        public static String QUERY_DEVICE_LIST(String username,String device_id) {
            return "[{\"username\":\""+username+"\",\"device_type\":\"6\",\"device_id\":\""+device_id+"\",\"msg_type\":\"17\"}]";
        }
    }
    public GPMQ mq;
    public PhoneHandleService phoneHandleService;
    public KeepAlive keepAlive;
    public CposMqttClient mqttClient;
    public MqttClient client;

    public String applicationPath, clientKeyPath, clientTrustPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new org.eclipse.paho.client.mqttv3.internal.security.SSLSocketFactoryFactory();
        try {

            applicationPath = getApplicationContext().getFilesDir().getAbsolutePath();
            //        String userDir = System.getProperty("user.dir");
            String userDir = applicationPath;
            File userDirFile = new File(userDir);
            if (!userDirFile.exists()) {
                userDirFile.mkdirs();
            }
            Log.d(TAG, "/data/:"+new File("/data/").exists());
            Log.d(TAG, "/data/com.cpos.mqtt.app3:"+userDirFile.exists());
            showFoldersFile(userDir);
            //        Log.d(TAG, "userDir:"+userDir);
            Log.d(TAG, "userDir:"+userDir+" "+userDirFile.exists()+" "+userDirFile.canWrite()+" "+userDirFile.exists()+" "+userDirFile.getAbsolutePath()+" "+userDirFile.getCanonicalPath());



            moveFileForward(clientKeyPath=userDir+"/"+CposMqttClient.KEY_STORE_PATH, R.raw.client_key);
            moveFileForward(clientTrustPath=userDir+"/"+CposMqttClient.TRUST_STORE_PATH, R.raw.client_trust);


            MqttDefaultFilePersistence mqttDefaultFilePersistence = new MqttDefaultFilePersistence(userDir) {
                @Override
                public void open(String clientId, String theConnection) throws MqttPersistenceException {
                    //                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //                    Log.d(TAG ,"getDataDir().getAbsolutePath():"+getDataDir().getAbsolutePath());
                    //                }
                    super.open(clientId, theConnection);
                }
            };
            client = new MqttClient(CposMqttClient.MQTT_URL, CposMqttClient.generateClientId(), mqttDefaultFilePersistence);










//            keepAlive = new KeepAlive(this);
//            mq = new GPMQ(this);
//            try {
//                mqttClient = new CposMqttClient(this);
//                phoneHandleService = new PhoneHandleService(this);
//            } catch (MqttException e) {
//                e.printStackTrace();
//            }
            Context context = getApplicationContext();

            Log.d(TAG,"Application onCreate");

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mq.start();
        keepAlive.start();
//        setDeviceList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mq.interrupt();
    }

    public void updateMQTTStatus(String statusDescription) {
        TextView appStatus = (TextView) findViewById(R.id.mqtt_description);
        appStatus.setText(statusDescription);
    }

    public void updateActionStatus(String statusDescription) {
        TextView appStatus = (TextView) findViewById(R.id.action_description);
        appStatus.setText(statusDescription);
    }
    public void inputVoiceReport(String statusDescription) {
        TextView appStatus = (TextView) findViewById(R.id.inputSoundVolumn);
        appStatus.setText(statusDescription);
    }

    public void showFoldersFile(String path) {
        Collection<File> all = new ArrayList<File>();
//        addTree(new File("."), all);
        addTree(new File(path), all);
        Log.d(TAG, all.toString());
        Log.d(TAG, "showFoldersFile:"+path);
//        System.out.println(all);
    }

    void addTree(File file, Collection<File> all) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                Log.d(TAG, "child:"+child.getAbsolutePath());
                all.add(child);
                addTree(child, all);
            }
        }
    }

    void moveFileForward(String systemPath,int resourceKey) throws IOException {
        InputStream is = getResources().openRawResource(R.raw.client_key);
        FileOutputStream os = new FileOutputStream(systemPath);
        byte[] data = new byte[1024];
        int i;
        while ((i=is.read(data))>0) {
            os.write(data);
        }
        is.close();
        os.close();
    }
    @Override
    protected void onStop() {
        super.onStop();
        mq.interrupt();
    }


}
