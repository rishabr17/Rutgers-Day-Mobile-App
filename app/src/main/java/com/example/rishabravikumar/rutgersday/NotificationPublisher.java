package com.example.rishabravikumar.rutgersday;

import android.util.Log;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class NotificationPublisher extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra("string2");
        int id = intent.getIntExtra("string1", 0);
        Log.d("notification", Integer.toString(id));
        notificationManager.notify(id, notification);
    }
}