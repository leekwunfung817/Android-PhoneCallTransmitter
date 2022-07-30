package com.cpos.mqtt.app3;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.cpos.mqtt.app3.databinding.ActivityCallScreenBinding;

import org.w3c.dom.Text;

public class CallScreenActivity extends AppCompatActivity {

//    private AppBarConfiguration appBarConfiguration;
    private ActivityCallScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCallScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//
//        setSupportActionBar(binding.toolbar);
//
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_call_screen);
//        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//
//        binding.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        {
            TextView callDeviceInformation = (TextView) findViewById(R.id.CallDeviceInformation);
            String deviceInformation =getIntent().getStringExtra(MainActivity.DEVICE_INFORMATION);
            callDeviceInformation.setText(MainActivity.deviceInfoToDeviceDisplay(deviceInformation));
        }
        {
            ImageButton hangOnButton = (ImageButton) findViewById(R.id.HangOn);
            hangOnButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            TextView callStatus = (TextView) findViewById(R.id.CallStatus);
                            callStatus.setText("Hang on and cancel.");
                            callStatus.setBackgroundColor(Color.RED);
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(CallScreenActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            });
        }
        TextView callStatus = (TextView) findViewById(R.id.CallStatus);
        callStatus.setText("Call ready.");
        callStatus.setBackgroundColor(Color.CYAN);


        ImageButton callButton = (ImageButton) findViewById(R.id.CallButton);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView callStatus = (TextView) findViewById(R.id.CallStatus);
                callStatus.setText("Calling.");
                callStatus.setBackgroundColor(Color.YELLOW);
            }
        });


    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_call_screen);
//        return NavigationUI.navigateUp(navController, appBarConfiguration)
//                || super.onSupportNavigateUp();
//    }
}