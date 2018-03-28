package com.example.krot.musicplayer.service;

import android.app.Notification;
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
        Log.i("GODZILLA", "Service: onCreate");
        manager = SongPlaybackManager.getSongPlaybackManagerInstance();

        //Service Broadcast Receiver
        receiver = new PlaybackServiceReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_UPDATE_UI);
        intentFilter.addAction(ACTION_UPDATE_NOTIFICATION_IS_PLAYING);
        intentFilter.addAction(ACTION_UPDATE_NOTIFICATION_IS_PAUSED);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("GODZILLA", "Service: onStartCommand");
        if (intent != null) {
            String action = intent.getAction();
            Log.i("GODZILLA", "Service: action = " + action);
            if (action != null) {
                if (TextUtils.equals(ACTION_CREATE_NOTIFICATION, action)) {
                    playbackNotification = PlaybackNotificationManager.getInstance().getNotificationBuilder().build();
                    PlaybackNotificationManager.getInstance().getNotificationManager().notify(PLAYBACK_NOTI_ID, playbackNotification);
                    startForeground(PLAYBACK_NOTI_ID, playbackNotification);
                }

                else if (TextUtils.equals(ACTION_DISMISS_NOTIFICATION, action)) {
                    Log.i("GODZILLA", "HERE");
                    stopSelf();
                }
            }
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("GODZILLA", "-@-@-@-@-@-@-@-@-@-@-@: service DESTROYED");
        Log.i("GODZILLA", "isAppRunning = " + Helper.isAppRunning(context, "com.example.krot.musicplayer"));
        unregisterReceiver(receiver);
        manager.saveLastPlayedSong();
//        if (!Helper.isAppRunning(context, "com.example.krot.musicplayer")) {
//            Log.i("GODZILLA", "release player luôn");
//            manager.releasePlayer();
//        }
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
                        Log.i("GODZILLA", "ACTION_UPDATE_NOTIFICATION_IS_PLAYING: đang play");
                        PlaybackNotificationManager.getInstance().updateNotificationIsPlayingIcon();
                    } else if (TextUtils.equals(ACTION_UPDATE_NOTIFICATION_IS_PAUSED, action)) {
                        Log.i("GODZILLA", "ACTION_UPDATE_NOTIFICATION_IS_PAUSED: đéo play");
                        PlaybackNotificationManager.getInstance().updateNotificationIsPausedIcon();
                        stopForeground(false);
                        PlaybackNotificationManager.getInstance().getNotificationManager().notify(PLAYBACK_NOTI_ID, PlaybackNotificationManager.getInstance().getNotificationBuilder().build());
                    }
                }
            }
        }
    }


}
