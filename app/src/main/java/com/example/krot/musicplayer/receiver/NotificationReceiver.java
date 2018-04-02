package com.example.krot.musicplayer.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.example.krot.musicplayer.SongPlaybackManager;
import com.example.krot.musicplayer.notification.PlaybackNotificationManager;
import com.example.krot.musicplayer.service.SongPlaybackService;

import static com.example.krot.musicplayer.AppConstantTag.ACTION_DISABLE_SWIPEABLE;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_DISMISS_NOTIFICATION;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_ENABLE_SWIPEABLE;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_NOTIFICATION_PLAYBACK;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_UPDATE_NOTIFICATION_IS_PAUSED;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_UPDATE_NOTIFICATION_IS_PLAYING;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_UPDATE_UI;
import static com.example.krot.musicplayer.AppConstantTag.PLAYBACK_NOTIFICATION_ID;
import static com.example.krot.musicplayer.AppConstantTag.PLAYBACK_NOTI_ID;

/**
 * Created by Krot on 3/27/18.
 */

public class NotificationReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (TextUtils.equals(ACTION_NOTIFICATION_PLAYBACK, action)) {
                    if (SongPlaybackManager.getSongPlaybackManagerInstance().isPlaying()) {
                        SongPlaybackManager.getSongPlaybackManagerInstance().pause();
                        PlaybackNotificationManager.getInstance().updateNotificationIsPausedIcon();
                    } else {
                        SongPlaybackManager.getSongPlaybackManagerInstance().play();
                        PlaybackNotificationManager.getInstance().updateNotificationIsPlayingIcon();
                    }
                }

            }
        }
    }
}
