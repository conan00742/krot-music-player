package com.example.krot.musicplayer.notification;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.krot.musicplayer.MusicPlayerApp;
import com.example.krot.musicplayer.R;
import com.example.krot.musicplayer.SongPlaybackManager;
import com.example.krot.musicplayer.model.SongItem;
import com.example.krot.musicplayer.receiver.NotificationReceiver;
import com.example.krot.musicplayer.service.SongPlaybackService;

import static com.example.krot.musicplayer.AppConstantTag.ACTION_DISMISS_NOTIFICATION;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_PLAYBACK;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_PLAY_NEXT_SONG;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_PLAY_PREVIOUS_SONG;
import static com.example.krot.musicplayer.AppConstantTag.PLAYBACK_NOTIFICATION_ID;
import static com.example.krot.musicplayer.AppConstantTag.PLAYBACK_NOTI_ID;

/**
 * Created by Krot on 3/27/18.
 */

public class PlaybackNotificationManager {

    private static PlaybackNotificationManager manager;

    private NotificationCompat.Builder builder;

    private RemoteViews songPlaybackRemoteViews;

    private NotificationManager notificationManager;


    private PlaybackNotificationManager() {

        songPlaybackRemoteViews = new RemoteViews(MusicPlayerApp.getAppContext().getPackageName(), R.layout.player_notification);
        notificationManager = (NotificationManager) MusicPlayerApp.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = MusicPlayerApp.getAppContext().getResources().getString(R.string.playback_channel_id);
            String channelName = MusicPlayerApp.getAppContext().getResources().getString(R.string.playback_channel_name);
            int importance = NotificationManager.IMPORTANCE_LOW;
            String channelDescription = MusicPlayerApp.getAppContext().getResources().getString(R.string.playback_channel_description);
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(MusicPlayerApp.getAppContext(), channelId);
        } else {
            builder = new NotificationCompat.Builder(MusicPlayerApp.getAppContext());
        }


        Intent actionCancelNotification = new Intent(MusicPlayerApp.getAppContext(), SongPlaybackService.class);
        actionCancelNotification.setAction(ACTION_DISMISS_NOTIFICATION);
        actionCancelNotification.putExtra(PLAYBACK_NOTIFICATION_ID, PLAYBACK_NOTI_ID);

        PendingIntent dismissNotiPendingIntent = PendingIntent.getService(MusicPlayerApp.getAppContext(),
                0, actionCancelNotification, PendingIntent.FLAG_CANCEL_CURRENT);

        //NotificationCompat.Builder
        builder.setCustomBigContentView(getRemoteView())
                .setContentTitle("SongPlayback Notification")
                .setSmallIcon(R.drawable.ic_playback_notification_icon)
                .setLargeIcon(BitmapFactory.decodeResource(MusicPlayerApp.getAppContext().getResources(), R.drawable.ic_playback_notification_icon))
                .setContentText("this is content text")
                .setSubText("sub text")
                .setDeleteIntent(dismissNotiPendingIntent);


        Log.i("GODZILLA", "init PlaybackNotificationManager: builder = " + builder);
        //setContentIntent(PendingIntent intent) ===> khi click vào notification thì mở PlayListActivity
    }

    public static PlaybackNotificationManager getInstance() {
//        if (manager == null) {
//            manager = new PlaybackNotificationManager();
//        }

        manager = new PlaybackNotificationManager();
        return manager;
    }

    public NotificationCompat.Builder getNotificationBuilder() {
        Log.i("GODZILLA", "getNotificationBuilder: builder = " + builder);
        return builder;
    }

    //create remote view
    private RemoteViews getRemoteView() {

        if (SongPlaybackManager.getSongPlaybackManagerInstance().isPlaying()) {
            Log.i("GODZILLA", "getRemoteView: đang play");
            songPlaybackRemoteViews.setImageViewResource(R.id.ic_noti_play, R.drawable.ic_notification_pause);
        } else {
            Log.i("GODZILLA", "getRemoteView: đéo play");
            songPlaybackRemoteViews.setImageViewResource(R.id.ic_noti_play, R.drawable.ic_notification_play);
        }

        updatePlaybackNotificationUI();

        Intent actionPlaybackIntent = new Intent(ACTION_PLAYBACK);
        Intent actionNextIntent = new Intent(ACTION_PLAY_NEXT_SONG);
        Intent actionPreviousIntent = new Intent(ACTION_PLAY_PREVIOUS_SONG);




        PendingIntent playbackPendingIntent = PendingIntent.getBroadcast(MusicPlayerApp.getAppContext(), 0, actionPlaybackIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent playNextPendingIntent = PendingIntent.getBroadcast(MusicPlayerApp.getAppContext(), 0, actionNextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent playPreviousPendingIntent = PendingIntent.getBroadcast(MusicPlayerApp.getAppContext(), 0, actionPreviousIntent, PendingIntent.FLAG_UPDATE_CURRENT);



        songPlaybackRemoteViews.setOnClickPendingIntent(R.id.ic_noti_play, playbackPendingIntent);
        songPlaybackRemoteViews.setOnClickPendingIntent(R.id.ic_noti_next, playNextPendingIntent);
        songPlaybackRemoteViews.setOnClickPendingIntent(R.id.ic_noti_previous, playPreviousPendingIntent);


        return songPlaybackRemoteViews;
    }

    public void updatePlaybackNotificationUI() {
        SongItem currentPlaybackSongItem = SongPlaybackManager.getSongPlaybackManagerInstance().getCurrentPlaybackSong();
        if (currentPlaybackSongItem.getSong() != null) {
            String songCover = null;
            Cursor coverCursor = MusicPlayerApp.getAppContext().getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
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

    public void updateNotificationIsPlayingIcon() {
        songPlaybackRemoteViews.setImageViewResource(R.id.ic_noti_play, R.drawable.ic_notification_pause);
        if (notificationManager != null) {
            notificationManager.notify(PLAYBACK_NOTI_ID, builder.build());
        }

    }

    public void updateNotificationIsPausedIcon() {
        songPlaybackRemoteViews.setImageViewResource(R.id.ic_noti_play, R.drawable.ic_notification_play);
        if (notificationManager != null) {
            notificationManager.notify(PLAYBACK_NOTI_ID, builder.build());
        }

    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public void enableSwipeable() {
        Log.i("GODZILLA", "enableSwipeable: builder = " + builder);
    }

    public void disableSwipeable() {
        Log.i("GODZILLA", "disableSwipeable: builder = " + builder);
        builder.setOngoing(true);
    }



}
