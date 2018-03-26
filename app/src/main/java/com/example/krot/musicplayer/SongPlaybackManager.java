package com.example.krot.musicplayer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.krot.musicplayer.event_bus.EventEndSong;
import com.example.krot.musicplayer.event_bus.EventIsPaused;
import com.example.krot.musicplayer.event_bus.EventIsPlaying;
import com.example.krot.musicplayer.event_bus.EventRepeatOff;
import com.example.krot.musicplayer.event_bus.EventRepeatOn;
import com.example.krot.musicplayer.event_bus.EventShuffleOff;
import com.example.krot.musicplayer.event_bus.EventShuffleOn;
import com.example.krot.musicplayer.event_bus.EventUpdateMiniPlaybackUI;
import com.example.krot.musicplayer.event_bus.EventUpdatePlayerUI;
import com.example.krot.musicplayer.event_bus.RxBus;
import com.example.krot.musicplayer.model.Item;
import com.example.krot.musicplayer.model.ShuffleAllSongsItem;
import com.example.krot.musicplayer.model.SongItem;
import com.example.krot.musicplayer.playlist.PlayListActivity;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.krot.musicplayer.AppConstantTag.ACTION_UPDATE_NOTIFICATION_IS_PAUSED;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_UPDATE_NOTIFICATION_IS_PLAYING;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_UPDATE_UI;
import static com.example.krot.musicplayer.AppConstantTag.CURRENT_PLAYBACK_POSITION;
import static com.example.krot.musicplayer.AppConstantTag.CURRENT_PLAYING_SONG;
import static com.example.krot.musicplayer.AppConstantTag.CURRENT_PLAYLIST_TAG;
import static com.example.krot.musicplayer.AppConstantTag.CURRENT_REPEAT_MODE_TAG;
import static com.example.krot.musicplayer.AppConstantTag.CURRENT_SHUFFLE_MODE_TAG;
import static com.example.krot.musicplayer.AppConstantTag.FIRST_TIME_INSTALL;
import static com.example.krot.musicplayer.AppConstantTag.LAST_PLAYED_SONG_INDEX_TAG;
import static com.example.krot.musicplayer.AppConstantTag.ORIGINAL_PLAYLIST;

/**
 * Created by Krot on 2/9/18.
 */

//TODO: SongPlaybackManager extends Service
public class SongPlaybackManager implements Player.EventListener, AudioManager.OnAudioFocusChangeListener {

    private static SongPlaybackManager manager;
    private SimpleExoPlayer player;
    private Context context;
    private RxBus bus;
    private boolean firstTimeInstantiate = false;

    //current playlist
    @Nullable
    private List<SongItem> currentList;

    //original playlist from storage
    @Nullable
    private List<SongItem> originalList = new ArrayList<>();

    private int lastPlayedSongIndex = 0;

    @NonNull
    private DataSource.Factory dataSourceFactory;
    private SharedPreferences lastPlayedSongPreferences;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private long currentPlaybackPosition = 0;

    /**DEFAUTL CONSTRUCTOR to initialize SimpleExoPlayer**/
    private SongPlaybackManager() {
        firstTimeInstantiate = true;
        //init bus
        bus = RxBus.getInstance();

        //init context
        context = MusicPlayerApp.getAppContext();

        //init player//constructor
        this.lastPlayedSongPreferences = this.context.getSharedPreferences(this.context.getPackageName(), Context.MODE_PRIVATE);

        Gson gson = new Gson();

        //last played playlist
        String data = lastPlayedSongPreferences.getString(CURRENT_PLAYLIST_TAG, "[]");

        //original playlist from STORAGE
        String defaultListInString = lastPlayedSongPreferences.getString(ORIGINAL_PLAYLIST, "[]");
        List<SongItem> defaultList = gson.fromJson(defaultListInString, new TypeToken<List<SongItem>>() {
        }.getType());

        //flag to check if user first install this app or not
        boolean isFirstTimeInstall = lastPlayedSongPreferences.getBoolean(FIRST_TIME_INSTALL, true);

        //check if first time install this app
        if (isFirstTimeInstall) {
            currentList = defaultList;
        } else {
            currentList = gson.fromJson(data, new TypeToken<List<SongItem>>() {
            }.getType());
            if (currentList == null || currentList.isEmpty()) {
                currentList = defaultList;
            }
        }

        //setOriginalList for later use
        setOriginalList(defaultList);

        //set the current displayed list
        saveCurrentPlayList(currentList);

        //Retrieve data from sharedpreferences
        lastPlayedSongIndex = lastPlayedSongPreferences.getInt(LAST_PLAYED_SONG_INDEX_TAG, 0);
        isShuffle = lastPlayedSongPreferences.getBoolean(CURRENT_SHUFFLE_MODE_TAG, false);
        isRepeat = lastPlayedSongPreferences.getBoolean(CURRENT_REPEAT_MODE_TAG, false);
        currentPlaybackPosition = lastPlayedSongPreferences.getLong(CURRENT_PLAYBACK_POSITION, 0);

        Log.i("WTF", "INITIALIZATION PLAYER: lastPlayedSongIndex = " + lastPlayedSongIndex + " - currentPlaybackPosition = " + currentPlaybackPosition);

        //create SimpleExoPlayer
        player = createPlayer(this.context);

        //prepareSource for player (lastPlayedSong)
        prepareSource(lastPlayedSongIndex);

        //set shuffle icon
        if (isShuffle) {
            bus.send(new EventShuffleOn());
        } else {
            bus.send(new EventShuffleOff());
        }

        //set repeat icon
        if (isRepeat) {
            bus.send(new EventRepeatOn());
        } else {
            bus.send(new EventRepeatOff());
        }

        //add listener to SimpleExoPlayer
        player.addListener(this);

        firstTimeInstantiate = false;
    }


    /**SINGLETON getInstance()**/
    public static SongPlaybackManager getSongPlaybackManagerInstance() {
        if (manager == null) {
            manager = new SongPlaybackManager();
        }

        return manager;
    }


    public void setCurrentList(@Nullable List<Item> currentList) {
        List<SongItem> convertedList = new ArrayList<>();
        for (int i = 0; i < currentList.size(); i++) {
            Item currentItem = currentList.get(i);
            if (currentItem instanceof ShuffleAllSongsItem) {
                continue;
            }
            SongItem currentSongItem = (SongItem) currentList.get(i);
            convertedList.add(currentSongItem);
        }

        this.currentList.clear();
        this.currentList.addAll(convertedList);
        saveCurrentPlayList(this.currentList);
    }


    public void setOriginalList(@Nullable List<SongItem> originalList) {
        this.originalList = originalList;
    }

    @Nullable
    public List<Item> getOriginalList() {
        List<Item> originalItemList = new ArrayList<>();
        if (originalList != null) {
            originalItemList.addAll(this.originalList);
        }
        return originalItemList;
    }

    public void setLastPlayedSongIndex(int lastPlayedSongIndex) {
        this.lastPlayedSongIndex = lastPlayedSongIndex;
    }

    public SimpleExoPlayer createPlayer(Context context) {
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        dataSourceFactory = new DefaultDataSourceFactory(
                context,
                Util.getUserAgent(context, context.getResources().getString(R.string.app_name)));
        SimpleExoPlayer simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

        return simpleExoPlayer;
    }


    public void prepareSource(int lastPlayedSongIndex) {

        if (firstTimeInstantiate) {
            player.seekTo(currentPlaybackPosition);
        } else {
            player.seekToDefaultPosition();
        }

        this.lastPlayedSongIndex = lastPlayedSongIndex;
        SongItem currentSongItem = currentList.get(this.lastPlayedSongIndex);

        //event bus update mini playback control UI in home
        bus.send(new EventUpdateMiniPlaybackUI(currentSongItem, (int) currentPlaybackPosition, this.lastPlayedSongIndex));

        //event bus update main player UI for next song
        bus.send(new EventUpdatePlayerUI(currentSongItem, (int)currentPlaybackPosition, this.lastPlayedSongIndex));

        //put Parcelable extra and sendBroadcast
        Intent notificationUpdateUIIntent = new Intent(ACTION_UPDATE_UI);
        context.sendBroadcast(notificationUpdateUIIntent);

        MediaSource currentMediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(currentSongItem.getSong().getSongUri()));
        player.prepare(currentMediaSource, true, false);

        //seekTo


    }

    /**
     * PLAY
     **/
    public void play() {
        player.setPlayWhenReady(true);
    }

    /**
     * PAUSE
     **/
    public void pause() {
        player.setPlayWhenReady(false);
    }

    public void releasePlayer() {
        player.release();
    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case Player.STATE_IDLE:
                break;
            case Player.STATE_BUFFERING:
                break;
            case Player.STATE_READY:
                if (playWhenReady) {
                    bus.send(new EventIsPlaying());
                    context.sendBroadcast(new Intent(ACTION_UPDATE_NOTIFICATION_IS_PLAYING));
                } else {
                    bus.send(new EventIsPaused());
                    context.sendBroadcast(new Intent(ACTION_UPDATE_NOTIFICATION_IS_PAUSED));
                }
                break;
            case Player.STATE_ENDED:
                bus.send(new EventEndSong());
                if (!isRepeatOn()) {
                    next();
                }
                break;
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
        switch (repeatMode) {
            case Player.REPEAT_MODE_OFF:
                isRepeat = false;
                bus.send(new EventRepeatOff());
                break;
            case Player.REPEAT_MODE_ONE:
                isRepeat = true;
                bus.send(new EventRepeatOn());
                break;
        }
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
        if (shuffleModeEnabled) {
            isShuffle = true;
            //bắn bus ra đổi icon shuffle_on
            bus.send(new EventShuffleOn());
            //shuffle play list

            //lấy bài đầu tiên bỏ vô list mới
            //mấy bài sau random
            List<SongItem> shuffledList = new ArrayList<>();
            SongItem currentPlayingSong = currentList.get(lastPlayedSongIndex);
            shuffledList.add(currentPlayingSong);

            //random đống còn lại bỏ bài đang play
            currentList.remove(currentPlayingSong);
            //do random
            List<SongItem> newList = randomPlayList(currentList);
            shuffledList.addAll(newList);

            //set vô currentList
            currentList.clear();
            currentList.addAll(shuffledList);
            saveCurrentPlayList(currentList);
            lastPlayedSongIndex = 0;
        } else {
            isShuffle = false;
            SongItem currentTrack = currentList.get(lastPlayedSongIndex);
            currentList.clear();
            currentList.addAll(originalList);
            saveCurrentPlayList(currentList);
            lastPlayedSongIndex = currentList.indexOf(currentTrack);
            bus.send(new EventShuffleOff());
            //bắn bus ra đổi icon shuffle_off
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }

    @Override
    public void onPositionDiscontinuity(int reason) {
    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }

    @Override
    public void onSeekProcessed() {
    }


    @Override
    public void onAudioFocusChange(int focusChange) {

    }


    public boolean isPlaying() {
        if (player.getPlaybackState() == Player.STATE_READY && player.getPlayWhenReady()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * SHUFFLE FEATURE
     **/
    public boolean isShuffleOn() {
        if (player.getShuffleModeEnabled()) {
            return true;
        } else {
            return false;
        }
    }

    public void setPlayerShuffleOn() {
        player.setShuffleModeEnabled(true);
        isShuffle = true;
    }

    public void setPlayerShuffleOff() {
        player.setShuffleModeEnabled(false);
        isShuffle = false;
    }

    /**
     * REPEAT FEATURE
     **/
    public boolean isRepeatOn() {
        if (player.getRepeatMode() == Player.REPEAT_MODE_ONE) {
            return true;
        } else {
            return false;
        }
    }

    public void setRepeatOn() {
        player.setRepeatMode(Player.REPEAT_MODE_ONE);
        isRepeat = true;
    }

    public void setRepeatOff() {
        player.setRepeatMode(Player.REPEAT_MODE_OFF);
        isRepeat = false;
    }

    /**
     * PLAY NEXT SONG FEATURE
     **/
    public void next() {
        if (this.lastPlayedSongIndex < currentList.size() - 1) {
            this.lastPlayedSongIndex += 1;
        } else {
            this.lastPlayedSongIndex = 0;
        }

        prepareSource(this.lastPlayedSongIndex);

    }

    /**
     * PLAY PREVIOUS SONG FEATURE
     **/
    public void previous() {
        if (this.lastPlayedSongIndex == 0) {
            player.seekTo(0, 0);
        } else if (this.lastPlayedSongIndex > 0) {
            this.lastPlayedSongIndex -= 1;
            prepareSource(this.lastPlayedSongIndex);
        }

    }

    /**SAVE LAST PLAYED SONG**/
    public void saveLastPlayedSong() {
        Log.i("WTF", "saveLastPlayedSong");
        saveCurrentPlayList(currentList);

        SharedPreferences.Editor editor = lastPlayedSongPreferences.edit();

        //current song index
        editor.putInt(LAST_PLAYED_SONG_INDEX_TAG, this.lastPlayedSongIndex);

        //current shuffle mode
        editor.putBoolean(CURRENT_SHUFFLE_MODE_TAG, isShuffle);

        //current repeat mode
        editor.putBoolean(CURRENT_REPEAT_MODE_TAG, isRepeat);

        //current song playback position
        editor.putLong(CURRENT_PLAYBACK_POSITION, player.getCurrentPosition());

        Log.i("WTF", "SAVELASTPLAYEDSONG: lastPlayedSongIndex = " + lastPlayedSongIndex + " - isShuffle = " + isShuffle + " - isRepeat = " + isRepeat + " - currentPlaybackPosition = " + player.getContentPosition());


        editor.apply();
    }


    private List<SongItem> getPlayList(List<Item> itemList) {
        List<SongItem> songItemList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            songItemList.add((SongItem) itemList.get(i));
        }
        return songItemList;
    }


    public void saveCurrentPlayList(List<SongItem> currentPlayList) {
        Log.i("WTF", "saveCurrentPlayList");
        Gson gson = new Gson();
        SharedPreferences.Editor editor = lastPlayedSongPreferences.edit();
        String data = gson.toJson(currentPlayList);
        editor.putString(CURRENT_PLAYLIST_TAG, data);
        editor.apply();
    }


    private List<SongItem> randomPlayList(List<SongItem> currentList) {
        List<SongItem> newList = new ArrayList<>();
        int index;
        SongItem tempSongItem;
        Random random = new Random();
        for (int i = currentList.size() - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            tempSongItem = currentList.get(index);
            newList.add(tempSongItem);
        }

        return newList;
    }


    public int getState() {
        return player.getPlaybackState();
    }


    public boolean getReady() {
        return player.getPlayWhenReady();
    }


    public void seek(int position) {
        player.seekTo(position);
    }

    public int getCurrentPlaybackPosition() {
        return (int)player.getCurrentPosition();
    }

    public SongItem getCurrentPlaybackSong() {
        return (currentList != null ? currentList.get(this.lastPlayedSongIndex) : null);
    }

}

