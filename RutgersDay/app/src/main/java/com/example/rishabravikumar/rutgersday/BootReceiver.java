package com.example.rishabravikumar.rutgersday;

/**
 * Created by Rishab Ravikumar on 4/12/2018.
 */
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent serviceIntent = new Intent("com.example.rishabravikumar.rutgersday.MyService");
            context.startService(serviceIntent);
        }
    }
}