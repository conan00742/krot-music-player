package com.example.krot.musicplayer.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.example.krot.musicplayer.SongPlaybackManager;
import com.example.krot.musicplayer.notification.PlaybackNotificationManager;

import static com.example.krot.musicplayer.AppConstantTag.ACTION_CREATE_NOTIFICATION;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_UPDATE_UI;
import static com.example.krot.musicplayer.AppConstantTag.PLAYBACK_NOTI_ID;

/**
 * Created by Krot on 3/14/18.
 */

public class SongPlaybackService extends Service {

    private Context context = SongPlaybackService.this;

    @NonNull
    private SongPlaybackManager manager;

    private BroadcastReceiver updateUIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("LINA", "Update Noti UI: " + intent.getAction());
            String action = intent.getAction();
            if (action != null) {
                if (TextUtils.equals(ACTION_UPDATE_UI, action)) {
                    PlaybackNotificationManager.getInstance().updatePlaybackNotificationUI();
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    if (notificationManager != null) {
                        notificationManager.notify(PLAYBACK_NOTI_ID, PlaybackNotificationManager.getInstance().getNotificationBuilder().build());
                    }
                }
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("VISAGE", "Service: onCreate");
        manager = SongPlaybackManager.getSongPlaybackManagerInstance();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_UPDATE_UI);
        registerReceiver(updateUIReceiver, filter);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("VISAGE", "Service: onStartCommand");
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (TextUtils.equals(ACTION_CREATE_NOTIFICATION, action)) {
                    Log.i("VISAGE", "ACTION_CREATE_NOTIFICATION");
                    NotificationCompat.Builder builder = PlaybackNotificationManager.getInstance().getNotificationBuilder();
                    startForeground(PLAYBACK_NOTI_ID, builder.build());
                }

            }
        }

        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        Log.i("VISAGE", "Service: onDestroy");
        Log.i("VISAGE", "________________________________________");
        super.onDestroy();
        //TODO: update RemoteViews UI using startForeground
        manager.saveLastPlayedSong();
        unregisterReceiver(updateUIReceiver);
    }


}
