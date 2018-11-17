package com.unifi.comp590.unifi;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.rsz_unifi_welcome)
                .setContentTitle("Test")
                .setContentText("HELLO");

        int mNotificationid = (int) System.currentTimeMillis();
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationid, mBuilder.build());
    }
}
