package com.example.krot.musicplayer.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.example.krot.musicplayer.notification.PlaybackNotificationManager;
import com.example.krot.musicplayer.service.SongPlaybackService;

import static com.example.krot.musicplayer.AppConstantTag.ACTION_DISABLE_SWIPEABLE;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_DISMISS_NOTIFICATION;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_ENABLE_SWIPEABLE;
import static com.example.krot.musicplayer.AppConstantTag.PLAYBACK_NOTIFICATION_ID;

/**
 * Created by Krot on 3/27/18.
 */

public class NotificationReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
//        if (intent != null) {
//            String action = intent.getAction();
//            if (action != null) {
//                if (TextUtils.equals(ACTION_DISMISS_NOTIFICATION, action)) {
//                    int id = intent.getIntExtra(PLAYBACK_NOTIFICATION_ID, -1);
//                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                    Log.i("GODZILLA", "ACTION_DISMISS_NOTIFICATION: id = " + id);
//                    if (notificationManager != null) {
//                        notificationManager.cancel(id);
//                        context.stopService(new Intent(context, SongPlaybackService.class));
//                    }
//                }
//
//                else if (TextUtils.equals(ACTION_ENABLE_SWIPEABLE, action)) {
//                    Log.i("GODZILLA", ACTION_ENABLE_SWIPEABLE);
//                    PlaybackNotificationManager.getInstance().enableSwipeable();
//                }
//
//                else if (TextUtils.equals(ACTION_DISABLE_SWIPEABLE, action)) {
//                    Log.i("GODZILLA", ACTION_DISABLE_SWIPEABLE);
//                    PlaybackNotificationManager.getInstance().disableSwipeable();
//                }
//            }
//        }
    }
}
