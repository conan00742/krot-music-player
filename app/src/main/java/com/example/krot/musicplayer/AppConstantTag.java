package com.example.krot.musicplayer;

/**
 * Created by Krot on 3/14/18.
 */

public class AppConstantTag {

    public static final String FIRST_TIME_INSTALL = "FIRST_TIME_INSTALL";
    public static final String ORIGINAL_PLAYLIST = "ORIGINAL_PLAYLIST";
    public static final String LAST_PLAYED_SONG_INDEX_TAG = "LASTPLAYEDSONGINDEX";
    public static final String CURRENT_PLAYING_SONG = "CURRENT_PLAYING_SONG";
    public static final String CURRENT_PLAYLIST_TAG = "CURRENT_PLAYLIST_TAG";
    public static final String CURRENT_SHUFFLE_MODE_TAG = "CURRENT_SHUFFLE_MODE_TAG";
    public static final String CURRENT_REPEAT_MODE_TAG = "CURRENT_REPEAT_MODE_TAG";
    public static final String CURRENT_PLAYBACK_POSITION = "CURRENT_PLAYBACK_POSITION";
    public static final String SELECTED_SONG_INDEX = "SELECTED_SONG_INDEX";

    //BROADCAST RECEIVER ACTION

    //global action
    public static final String ACTION_PLAYBACK = "com.example.krot.musicplayer.ACTION_PLAYBACK";
    public static final String ACTION_PLAY_NEXT_SONG = "com.example.krot.musicplayer.ACTION_PLAY_NEXT_SONG";
    public static final String ACTION_PLAY_PREVIOUS_SONG = "com.example.krot.musicplayer.ACTION_PLAY_PREVIOUS_SONG";
    public static final String ACTION_UPDATE_UI = "com.example.krot.musicplayer.ACTION_UPDATE_UI";
    public static final String ACTION_DISMISS_NOTIFICATION = "com.example.krot.musicplayer.ACTION_DISMISS_NOTIFICATION";
    public static final String ACTION_ENABLE_SWIPEABLE = "com.example.krot.musicplayer.ACTION_ENABLE_SWIPEABLE";
    public static final String ACTION_DISABLE_SWIPEABLE = "com.example.krot.musicplayer.ACTION_DISABLE_SWIPEABLE";

    //local action
    public static final String ACTION_SAVE_CURRENT_PLAYLIST = "com.example.krot.musicplayer.ACTION_SAVE_CURRENT_PLAYLIST";
    public static final String ACTION_CREATE_NOTIFICATION = "com.example.krot.musicplayer.ACTION_CREATE_NOTIFICATION";
    public static final String ACTION_NOTIFICATION_PLAYBACK = "com.example.krot.musicplayer.ACTION_NOTIFICATION_PLAYBACK";
    public static final String ACTION_NOTIFICATION_NEXT = "com.example.krot.musicplayer.ACTION_NOTIFICATION_NEXT";
    public static final String ACTION_NOTIFICATION_PREVIOUS = "com.example.krot.musicplayer.ACTION_NOTIFICATION_PREVIOUS";
    public static final String ACTION_UPDATE_NOTIFICATION_IS_PLAYING = "com.example.krot.musicplayer.ACTION_UPDATE_NOTIFICATION_IS_PLAYING";
    public static final String ACTION_UPDATE_NOTIFICATION_IS_PAUSED = "com.example.krot.musicplayer.ACTION_UPDATE_NOTIFICATION_IS_PAUSED";

    public static final int PERMISSION_CODE = 101;
    public static final int REQUEST_PERMISSION_SETTING = 90;
    public static final int PLAYBACK_NOTI_ID = 1001;
    public static final String PLAYLIST_TAG = "PLAY_LIST_TAG";
    public static final String MANAGER_OBJECT_EXTRA_TAG = "MANAGER_OBJECT_EXTRA_TAG";
    public static final String PRESENTER_OBJECT_EXTRA_TAG = "PRESENTER_OBJECT_EXTRA_TAG";
    public static final String PLAYBACK_NOTIFICATION_ID = "PLAYBACK_NOTIFICATION_ID";
}
