package com.example.krot.musicplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.krot.musicplayer.R;
import com.example.krot.musicplayer.SongPlaybackManager;
import com.example.krot.musicplayer.model.SongItem;

import static com.example.krot.musicplayer.AppConstantTag.ACTION_CREATE_NOTIFICATION;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_NOTIFICATION_NEXT;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_NOTIFICATION_PLAYBACK;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_NOTIFICATION_PREVIOUS;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_PLAYBACK;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_PLAY_NEXT_SONG;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_PLAY_PREVIOUS_SONG;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_UPDATE_NOTIFICATION_IS_PAUSED;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_UPDATE_NOTIFICATION_IS_PLAYING;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_UPDATE_UI;
import static com.example.krot.musicplayer.AppConstantTag.CURRENT_PLAYING_SONG;

/**
 * Created by Krot on 3/14/18.
 */

public class SongPlaybackService extends Service {

    private static final String PREFIX = SongPlaybackService.class.getSimpleName();
    private Context context = SongPlaybackService.this;

    @NonNull
    private SongPlaybackManager manager;

    private PlaybackServiceReceiver receiver;

    private RemoteViews songPlaybackRemoteViews;

    private NotificationManager notificationManager;

    private Notification playbackNotification;


    @Override
    public void onCreate() {
        Log.i("WTF", PREFIX + ": onCreate");
        super.onCreate();
        manager = SongPlaybackManager.getSongPlaybackManagerInstance();
        receiver = new PlaybackServiceReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_UPDATE_UI);
        intentFilter.addAction(ACTION_UPDATE_NOTIFICATION_IS_PLAYING);
        intentFilter.addAction(ACTION_UPDATE_NOTIFICATION_IS_PAUSED);
        registerReceiver(receiver, intentFilter);
        songPlaybackRemoteViews = new RemoteViews(getPackageName(), R.layout.player_notification);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        songPlaybackRemoteViews = new RemoteViews(getPackageName(), R.layout.player_notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (TextUtils.equals(ACTION_CREATE_NOTIFICATION, action)) {
                    Log.i("WTF", "----------------CREATE NOTIFICATION--------------");
                    createSongPlaybackNotification();
                    //start foreground service
                    startForeground(1001, playbackNotification);
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
        Log.i("WTF", PREFIX + ": onDestroy");
        super.onDestroy();
        unregisterReceiver(receiver);
        manager.releasePlayer();
    }

    //create song playback notification
    private void createSongPlaybackNotification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.playback_channel_id))
                .setCustomBigContentView(getRemoteView())
                .setContentTitle("SongPlayback Notification")
                .setSmallIcon(R.drawable.ic_playback_notification_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_playback_notification_icon))
                .setContentText("this is content text")
                .setSubText("sub text")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MAX);

        playbackNotification = builder.build();
    }

    //create remote view
    private RemoteViews getRemoteView() {

        if (manager.isPlaying()) {
            songPlaybackRemoteViews.setImageViewResource(R.id.ic_noti_play, R.drawable.ic_notification_pause);
        } else {
            songPlaybackRemoteViews.setImageViewResource(R.id.ic_noti_play, R.drawable.ic_notification_play);
        }
        updatePlaybackNotificationUI();

        Intent actionPlaybackIntent = new Intent(ACTION_PLAYBACK);
        Intent actionNextIntent = new Intent(ACTION_PLAY_NEXT_SONG);
        Intent actionPreviousIntent = new Intent(ACTION_PLAY_PREVIOUS_SONG);

        PendingIntent playbackPendingIntent = PendingIntent.getBroadcast(context, 0, actionPlaybackIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent playNextPendingIntent = PendingIntent.getBroadcast(context, 0, actionNextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent playPreviousPendingIntent = PendingIntent.getBroadcast(context, 0, actionPreviousIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        songPlaybackRemoteViews.setOnClickPendingIntent(R.id.ic_noti_play, playbackPendingIntent);
        songPlaybackRemoteViews.setOnClickPendingIntent(R.id.ic_noti_next, playNextPendingIntent);
        songPlaybackRemoteViews.setOnClickPendingIntent(R.id.ic_noti_previous, playPreviousPendingIntent);

        return songPlaybackRemoteViews;
    }

    private void updatePlaybackNotificationUI() {
        SongItem currentPlaybackSongItem = manager.getCurrentPlaybackSong();
        Log.i("NHI", "updatePlaybackNotificationUI: songName = " + currentPlaybackSongItem.getSong().getSongTitle());
        if (currentPlaybackSongItem.getSong() != null) {
            String songCover = null;
            Cursor coverCursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                    MediaStore.Audio.Albums._ID + " = ?",
                    new String[]{String.valueOf(currentPlaybackSongItem.getSong().getAlbumId())},
                    null);

            if (coverCursor.moveToFirst()) {
                songCover = coverCursor.getString(coverCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
            }

            //set noti song cover
            if (songCover != null) {
                songPlaybackRemoteViews.setImageViewUri(R.id.img_noti_song_cover, Uri.parse(songCover));
            } else {
                songPlaybackRemoteViews.setImageViewResource(R.id.img_noti_song_cover, R.drawable.default_song_image);
            }

            //set noti song title
            songPlaybackRemoteViews.setTextViewText(R.id.tv_noti_song_name, currentPlaybackSongItem.getSong().getSongTitle());
            //set noti song artist
            songPlaybackRemoteViews.setTextViewText(R.id.tv_noti_artist_name, currentPlaybackSongItem.getSong().getArtistName());
        } else {
            songPlaybackRemoteViews.setImageViewResource(R.id.img_noti_song_cover, R.drawable.default_song_image);
            songPlaybackRemoteViews.setTextViewText(R.id.tv_noti_song_name, "???");
            songPlaybackRemoteViews.setTextViewText(R.id.tv_noti_artist_name, "???");
        }

    }

    private void updateNotificationIsPlayingIcon() {
        songPlaybackRemoteViews.setImageViewResource(R.id.ic_noti_play, R.drawable.ic_notification_pause);
        notificationManager.notify(1001, playbackNotification);
    }

    private void updateNotificationIsPausedIcon() {
        songPlaybackRemoteViews.setImageViewResource(R.id.ic_noti_play, R.drawable.ic_notification_play);
        notificationManager.notify(1001, playbackNotification);
    }

    private class PlaybackServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (action != null) {
                    if (TextUtils.equals(ACTION_UPDATE_UI, action)) {
                        Log.i("NHI", "ACTION_UPDATE_UI");
                        updatePlaybackNotificationUI();
                        notificationManager.notify(1001, playbackNotification);
                    }

                    else if (TextUtils.equals(ACTION_UPDATE_NOTIFICATION_IS_PLAYING, action)) {
                        updateNotificationIsPlayingIcon();
                    }

                    else if (TextUtils.equals(ACTION_UPDATE_NOTIFICATION_IS_PAUSED, action)) {
                        updateNotificationIsPausedIcon();
                    }
                }
            }
        }
    }


}
