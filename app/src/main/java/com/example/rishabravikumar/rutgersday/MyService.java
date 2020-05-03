package com.example.rishabravikumar.rutgersday;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import java.util.TimerTask;
import java.util.Timer;
import java.util.Calendar;
import android.util.Log;

/**
 * Created by Rishab Ravikumar on 4/12/2018.
 */
public class MyService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Calendar alarm = Calendar.getInstance();
        alarm.set(2018,3,12,4,15);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("worksque","works");
            }
        }, alarm.getTimeInMillis() - System.currentTimeMillis());

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // STOP YOUR TASKS
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}