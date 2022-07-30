package com.cpos.mqtt.app3;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cpos.mqtt.app3.old.MainActivityOld;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class VoiceController {

    private final String TAG = this.getClass().getCanonicalName();

    boolean isRecording = false;
    //    AudioManager am = null;
    AudioRecord record = null;
    AudioTrack track = null;

    MainActivity mainActivity;


    private static int RECORDER_SAMPLERATE = 44100;
    private static int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
    private static int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;


    public static LinkedBlockingQueue<byte[]> audioQueue = new LinkedBlockingQueue<byte[]>();

    public static LinkedBlockingQueue<Pair<short[], Integer>> voicePlayingQueue = new LinkedBlockingQueue<Pair<short[], Integer>>();

    public static class Pair<F, S> {
        public F first = null;
        public S second = null;

        public Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }
    }

    Pair<short[], Integer> pair = new Pair<>(new short[1024], 0);

    public VoiceController(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
//        initRecordAndTrack();
    }

    public boolean isAllow(String permissionName) {
        int result = ContextCompat.checkSelfPermission(mainActivity, permissionName);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(mainActivity, new String[]{permissionName}, 1);
            int resultCode = ContextCompat.checkSelfPermission(mainActivity, permissionName);
            if (resultCode > 0 && resultCode == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mainActivity, "You accepted " + permissionName + " permission.", Toast.LENGTH_SHORT);
                return true;
            } else {
                Toast.makeText(mainActivity, "You refused " + permissionName + " permission.", Toast.LENGTH_SHORT);
                return false;
            }
        }
    }

    public boolean isCallAllow() {
        return isAllow(Manifest.permission.CALL_PHONE) && isAllow(Manifest.permission.RECORD_AUDIO) && isAllow(Manifest.permission.CAMERA) && isAllow(Manifest.permission.ACCESS_FINE_LOCATION);

    }

//    private void initRecordAndTrack() {
//        mainActivity.setVolumeControlStream(AudioManager.MODE_IN_COMMUNICATION);
//        int min = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
//        if (ActivityCompat.checkSelfPermission(mainActivity.getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            Log.e(TAG,"RECORD_AUDIO not PERMISSION_GRANTED");
////            return;
//        }
//        record = new AudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, 8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,
//                min);
//        if (AcousticEchoCanceler.isAvailable())
//        {
//            AcousticEchoCanceler echoCancler = AcousticEchoCanceler.create(record.getAudioSessionId());
//            echoCancler.setEnabled(true);
//        }
//        int maxJitter = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
//        track = new AudioTrack(AudioManager.MODE_IN_COMMUNICATION, 8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, maxJitter,
//                AudioTrack.MODE_STREAM);
////        am = new AudioManager();
//


        // https://stackoverflow.com/questions/51419542/detect-voice-by-audio-recorder-in-android-studio
        // Get the minimum buffer size required for the successful creation of an AudioRecord object.
//        int bufferSizeInBytes = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
//                RECORDER_CHANNELS,
//                RECORDER_AUDIO_ENCODING
//        );
//        // Initialize Audio Recorder.
//        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        AudioRecord audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
//                RECORDER_SAMPLERATE,
//                RECORDER_CHANNELS,
//                RECORDER_AUDIO_ENCODING,
//                bufferSizeInBytes
//        );
        // Start Recording.
//        txv.setText("Ing");
//        audioRecorder.startRecording();
//        byte[] audioBuffer      = new  byte[bufferSizeInBytes];
//        int numberOfReadBytes = audioRecorder.read( audioBuffer, 0, bufferSizeInBytes );
//        audioQueue.add(audioBuffer);
//        isRecording = true;
//    }

    public void recordAndPlay()
    {
//        am.setMode(AudioManager.MODE_IN_COMMUNICATION);
        while (true)
        {
            if (isRecording)
            {
                Pair<short[], Integer> pair = getVoice();
                playVoice(pair);
            }
        }
    }

    public Pair<short[], Integer> getVoice() {
        if (record == null) {
            Log.d(TAG, "record == null");
        }
        if (pair == null) {
            Log.d(TAG, "pair == null");
        }
        if (pair.first == null) {
            Log.d(TAG, "pair.first == null");
        }
        int num = record.read(pair.first, 0, 1024);
        pair.second = Integer.valueOf(num);
        return pair;
    }

    public int playVoice(Pair<short[], Integer> pair) {
        return track.write(pair.first, 0, pair.second);
    }

    public static String shortArrToStr(short[] intArray) {
        // input primitive integer array
        String[] strArray = new String[intArray.length];
        for (int i = 0; i < intArray.length; i++) {
            strArray[i] = String.valueOf(intArray[i]);
        }
        return Arrays.toString(strArray);
    }
}
