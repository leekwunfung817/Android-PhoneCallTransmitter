package com.cpos.mqtt.app3.helper;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Helper {

    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd.hh.mm.ss.SSS");
        return sdf.format(new Date());
    }

    public static void delaySecond(double second) {
        try {
            Thread.sleep((long)second*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
