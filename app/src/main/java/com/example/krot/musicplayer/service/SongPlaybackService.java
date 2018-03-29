package com.example.krot.musicplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.example.krot.musicplayer.Helper;
import com.example.krot.musicplayer.SongPlaybackManager;
import com.example.krot.musicplayer.notification.PlaybackNotificationManager;

import static com.example.krot.musicplayer.AppConstantTag.ACTION_CREATE_NOTIFICATION;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_DISABLE_SWIPEABLE;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_DISMISS_NOTIFICATION;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_ENABLE_SWIPEABLE;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_UPDATE_NOTIFICATION_IS_PAUSED;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_UPDATE_NOTIFICATION_IS_PLAYING;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_UPDATE_UI;
import static com.example.krot.musicplayer.AppConstantTag.PLAYBACK_NOTI_ID;

/**
 * Created by Krot on 3/14/18.
 */

public class SongPlaybackService extends Service {

    private Context context = SongPlaybackService.this;

    @NonNull
    private SongPlaybackManager manager;

    private PlaybackServiceReceiver receiver;

    private Notification playbackNotification;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("HARRY", "Service: onCreate");
        manager = SongPlaybackManager.getSongPlaybackManagerInstance();

        //Service Broadcast Receiver
        receiver = new PlaybackServiceReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_UPDATE_UI);
        intentFilter.addAction(ACTION_UPDATE_NOTIFICATION_IS_PLAYING);
        intentFilter.addAction(ACTION_UPDATE_NOTIFICATION_IS_PAUSED);
        registerReceiver(receiver, intentFilter);

        playbackNotification = PlaybackNotificationManager.getInstance().getNotificationBuilder().build();
        PlaybackNotificationManager.getInstance().getNotificationManager().notify(PLAYBACK_NOTI_ID, playbackNotification);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("HARRY", "Service: onStartCommand");
        if (intent != null) {
            String action = intent.getAction();
            Log.i("HARRY", "Service: action = " + action);
            if (action != null) {
                if (TextUtils.equals(ACTION_CREATE_NOTIFICATION, action)) {
                    startForeground(PLAYBACK_NOTI_ID, playbackNotification);
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
        super.onDestroy();
        Log.i("HARRY", "Service: onDestroy");

        //TODO: update RemoteViews UI using startForeground
        stopForeground(true);
        unregisterReceiver(receiver);
        manager.saveLastPlayedSong();
//        stopSelf();
    }


    /**Playback Service Broadcast Receiver**/
    private class PlaybackServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (action != null) {
                    if (TextUtils.equals(ACTION_UPDATE_UI, action)) {
                        PlaybackNotificationManager.getInstance().updatePlaybackNotificationUI();
                    } else if (TextUtils.equals(ACTION_UPDATE_NOTIFICATION_IS_PLAYING, action)) {
                        Log.i("HARRY", "SERVICE: received ACTION_UPDATE_NOTIFICATION_IS_PLAYING");
                        PlaybackNotificationManager.getInstance().updateNotificationIsPlayingIcon();
                    } else if (TextUtils.equals(ACTION_UPDATE_NOTIFICATION_IS_PAUSED, action)) {
                        Log.i("HARRY", "SERVICE: received ACTION_UPDATE_NOTIFICATION_IS_PAUSED");
                        PlaybackNotificationManager.getInstance().updateNotificationIsPausedIcon();
                    }
                }
            }
        }
    }


}
