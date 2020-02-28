package com.onyx.android.sdk.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.onyx.android.sdk.R;

/**
 * <pre>
 *     author : liao lin tao
 *     time   : 2019/6/21 19:25
 *     desc   :
 * </pre>
 */
public class NotificationUtils {

    public static void startForegroundServiceNotification(Service service) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        String channelID = service.getPackageName();
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) {
            return;
        }
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(service, channelID);
        Notification notification = notificationBuilder.setOngoing(true)
                                                       .setContentTitle("App is running in background")
                                                       .setPriority(NotificationManager.IMPORTANCE_MIN)
                                                       .setCategory(Notification.CATEGORY_SERVICE)
                                                       .build();
        service.startForeground(1, notification);
    }
}
