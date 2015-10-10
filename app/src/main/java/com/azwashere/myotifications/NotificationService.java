package com.azwashere.myotifications;

import android.content.Context;
import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by Jeff on 10/9/2015.
 */
public class NotificationService extends NotificationListenerService {

    Context context;

    @Override

    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();

    }

    @Override

    public void onNotificationPosted(StatusBarNotification sbn) {

        Intent notifyRec = new Intent("notify_rec");

        LocalBroadcastManager.getInstance(context).sendBroadcast(notifyRec);


    }

    @Override

    public void onNotificationRemoved(StatusBarNotification sbn) {

    }
}
