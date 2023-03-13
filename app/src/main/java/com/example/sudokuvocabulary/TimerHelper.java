package com.example.sudokuvocabulary;

import android.os.Handler;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class TimerHelper {
    private double time;
    private final Timer timer = new Timer();
    private final Handler handler;

    public TimerHelper(double startTime, Handler handler) {
        this.time = startTime;
        this.handler = handler;
    }

    public void start() {
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                time++;
                handler.obtainMessage().sendToTarget(); // Update view passed by user
            }
        }, 0, 1000);
    }

    public void stop() {
        timer.cancel();
    }

    public double getTime() {
        return this.time;
    }

    public String getTimerText(){
        int rounded = (int) Math.round(time);

        int seconds = ((rounded % 86400) % 3600) % 60;
        int hours = ((rounded % 86400) / 3600);
        int minutes = ((rounded % 86400) % 3600) / 60;

        return formatTime(seconds,minutes,hours);
    }

    private String formatTime(int seconds, int minutes, int hours) {
        return String.format(Locale.ENGLISH, "%02d",hours) + " : " +
                String.format(Locale.ENGLISH, "%02d",minutes) + " : " +
                String.format(Locale.ENGLISH, "%02d", seconds);
    }
}
