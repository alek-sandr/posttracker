package com.kodingen.cetrin.posttracker;

import android.support.v4.app.NotificationCompat;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.TimerTask;

public class TrackService extends Service {
    NotificationManager nm;

    @Override
    public void onCreate() {
        super.onCreate();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendNotification("");
//        TimerTask tt = new TimerTask() {
//            @Override
//            public void run() {
//
//            }
//        };
        return super.onStartCommand(intent, flags, startId);
    }

    void sendNotification(String barcode) {
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentInfo(getString(R.string.change_in_code) + " " + barcode)
                .setContentTitle(getString(R.string.change_in_code) + " " + barcode)
                .setContentText(getString(R.string.click_for_detail))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true);

        Intent intent = new Intent(TrackCodeInfo.ACTION_SHOWINFO);
        intent.putExtra(TrackCodeInfo.TRACKCODE, barcode);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        notifBuilder.setContentIntent(pIntent);
        Notification notif = notifBuilder.build();

        nm.notify(1, notif);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
