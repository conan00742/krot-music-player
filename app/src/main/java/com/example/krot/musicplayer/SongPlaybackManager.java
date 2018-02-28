package com.example.krot.musicplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.krot.musicplayer.event_bus.RxBus;
import com.example.krot.musicplayer.model.Item;
import com.example.krot.musicplayer.model.SongItem;
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
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Krot on 2/9/18.
 */

public class SongPlaybackManager extends Player.DefaultEventListener implements Player.EventListener, AudioManager.OnAudioFocusChangeListener, ImageView.OnClickListener {

    private static final String LAST_PLAYED_SONG_INDEX_TAG = "LASTPLAYEDSONGINDEX";
    private static final String CURRENT_PLAYING_SONG = "CURRENT_PLAYING_SONG";
    private static final String CURRENT_PLAYLIST_TAG = "CURRENT_PLAYLIST_TAG";
    private static final String CURRENT_SHUFFLE_MODE_TAG = "CURRENT_SHUFFLE_MODE_TAG";
    private static final String CURRENT_REPEAT_MODE_TAG = "CURRENT_REPEAT_MODE_TAG";
    private static final String CURRENT_PLAYBACK_POSITION = "CURRENT_PLAYBACK_POSITION";

    private Context context;
    private RxBus bus;
    private ImageView icPlayback;
    private ImageView icShuffle;
    private ImageView icRepeat;
    private ImageView icNext;
    private ImageView icPrevious;

    //PlayListActivity Views
    private TextView songName;
    private TextView artistName;
    private ImageView songBackground;


    //current playlist
    @Nullable
    private List<SongItem> currentList;

    @Nullable
    private List<SongItem> backUpList = new ArrayList<>();

    @Nullable
    private List<SongItem> originalList = new ArrayList<>();


    private int lastPlayedSongIndex = 0;
    private int count = 0;
    @NonNull
    private final DataSource.Factory dataSourceFactory;
    @NonNull
    public final SimpleExoPlayer player;
    @Nullable
    private SimpleExoPlayerView exoPlayerView;
    private AudioManager audioManager;
    private AudioFocusRequest mFocusRequest;
    private FrameLayout container;
    private HashMap<List<Item>, Integer> lastPlayedSongMap;
    private SharedPreferences lastPlayedSongPreferences;
    private boolean isPlayingShuffleAll = false;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private long currentPlaybackPosition = 0;


    public SongPlaybackManager(Context context, RxBus bus,
                               FrameLayout playbackContainer,
                               TextView songName,
                               TextView artistName,
                               ImageView songBackground,
                               @Nullable List<Item> defaultList,
                               int inAppCount) {

        this.context = context;
        this.bus = bus;
        container = playbackContainer;
        this.songName = songName;
        this.artistName = artistName;
        this.songBackground = songBackground;
        this.count = inAppCount;
        this.lastPlayedSongPreferences = this.context.getSharedPreferences(this.context.getPackageName(), Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String data = lastPlayedSongPreferences.getString(CURRENT_PLAYLIST_TAG, "[]");


        if (count == 0) {
            currentList = getPlayList(defaultList);
        } else {
            currentList = gson.fromJson(data, new TypeToken<List<SongItem>>() {
            }.getType());
            if (currentList == null || currentList.isEmpty()) {
                currentList = getPlayList(defaultList);
            }
        }

        backUpList = currentList;
        lastPlayedSongIndex = lastPlayedSongPreferences.getInt(LAST_PLAYED_SONG_INDEX_TAG, 0);
        isShuffle = lastPlayedSongPreferences.getBoolean(CURRENT_SHUFFLE_MODE_TAG, false);
        isRepeat = lastPlayedSongPreferences.getBoolean(CURRENT_REPEAT_MODE_TAG, false);
        currentPlaybackPosition = lastPlayedSongPreferences.getLong(CURRENT_PLAYBACK_POSITION, 0);


        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        dataSourceFactory = new DefaultDataSourceFactory(
                context,
                Util.getUserAgent(context, context.getResources().getString(R.string.app_name)));
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);




        //init exoplayerview
        exoPlayerView = (SimpleExoPlayerView) LayoutInflater.from(this.context).inflate(R.layout.music_player_view, null, false);
        container.addView(exoPlayerView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        exoPlayerView.setUseController(true);
        exoPlayerView.setControllerShowTimeoutMs(0);
        exoPlayerView.setControllerHideOnTouch(false);
        exoPlayerView.setUseArtwork(false);

        //findChildViewByIds
        findChildViewById();
        exoPlayerView.setPlayer(player);
        prepareSource(lastPlayedSongIndex);

        player.addListener(this);
        player.setShuffleModeEnabled(isShuffle);
        if (isRepeat) {
            player.setRepeatMode(Player.REPEAT_MODE_ONE);
        } else {
            player.setRepeatMode(Player.REPEAT_MODE_OFF);
        }
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(audioAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setWillPauseWhenDucked(true)
                    .setOnAudioFocusChangeListener(this)
                    .build();

        }
    }


    public void setCurrentList(@Nullable List<Item> currentList) {
        Log.i("WTF", "currentList: size = " + this.currentList.size());
        this.currentList.clear();
        for (int i = 0; i < currentList.size(); i++) {
            this.currentList.add((SongItem) currentList.get(i));
        }

    }

    public void setOriginalList(@Nullable List<Item> originalList) {
        for (int i = 0; i < originalList.size(); i++) {
            this.originalList.add((SongItem) originalList.get(i));
        }
    }

    public void setLastPlayedSongIndex(int lastPlayedSongIndex) {
        this.lastPlayedSongIndex = lastPlayedSongIndex;
    }

    public void setPlayingShuffleAll(boolean playingShuffleAll) {
        isPlayingShuffleAll = playingShuffleAll;
    }


    public void prepareSource(int lastPlayedSongIndex) {
        this.lastPlayedSongIndex = lastPlayedSongIndex;
        SongItem currentSongItem = currentList.get(this.lastPlayedSongIndex);
        MediaSource currentMediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(currentSongItem.getSong().getSongUri()));
        player.prepare(currentMediaSource, true, false);
        player.seekTo(currentPlaybackPosition);
        updatePlayerView(currentSongItem);
    }


    public void play() {
        player.setPlayWhenReady(true);
    }

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
                    icPlayback.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pause));
                } else {
                    icPlayback.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play));
                }
                break;
            case Player.STATE_ENDED:
                //if repeat mode OFF -> auto play next song
                //else repeat the current song
                if (!isRepeat){
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
                icRepeat.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_repeat_off));
                break;
            case Player.REPEAT_MODE_ONE:
                isRepeat = true;
                icRepeat.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_repeat_on));
                break;
        }
    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
        if (shuffleModeEnabled) {
            isShuffle = true;
            icShuffle.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_shuffle_on));
        } else {
            isShuffle = false;
            if (isPlayingShuffleAll) {
                SongItem currentTrack = currentList.get(lastPlayedSongIndex);
                currentList.clear();
                currentList.addAll(originalList);
                lastPlayedSongIndex = currentList.indexOf(currentTrack);
            }
            icShuffle.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_shuffle_off));
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

    //TODO: tự handle shuffle, khi play sẽ tạo ra source


    public void findChildViewById() {
        icPlayback = exoPlayerView.findViewById(R.id.ic_playback);
        icPlayback.setOnClickListener(this);

        icNext = exoPlayerView.findViewById(R.id.ic_next);
        icNext.setOnClickListener(this);

        icPrevious = exoPlayerView.findViewById(R.id.ic_previous);
        icPrevious.setOnClickListener(this);

        icShuffle = exoPlayerView.findViewById(R.id.ic_shuffle);
        icShuffle.setOnClickListener(this);

        icRepeat = exoPlayerView.findViewById(R.id.ic_repeat);
        icRepeat.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ic_playback:
                if (player.getPlaybackState() == Player.STATE_READY && player.getPlayWhenReady()) {
                    pause();
                } else {
                    play();
                }
                break;
            case R.id.ic_next:
                next();
                break;
            case R.id.ic_previous:
                previous();
                break;
            case R.id.ic_shuffle:
                if (player.getShuffleModeEnabled()) {
                    isShuffle = false;
                    player.setShuffleModeEnabled(false);
                } else {
                    isShuffle = true;
                    player.setShuffleModeEnabled(true);
                }
                break;
            case R.id.ic_repeat:
                if (player.getRepeatMode() == Player.REPEAT_MODE_OFF) {
                    isRepeat = true;
                    player.setRepeatMode(Player.REPEAT_MODE_ONE);
                } else if (player.getRepeatMode() == Player.REPEAT_MODE_ONE) {
                    isRepeat = false;
                    player.setRepeatMode(Player.REPEAT_MODE_OFF);
                }
                break;
        }
    }


    public void next() {
        if (player.getShuffleModeEnabled()) {
            //random
            Random shuffleIndex = new Random();
            this.lastPlayedSongIndex = shuffleIndex.nextInt(currentList.size());
        }


        else if (!player.getShuffleModeEnabled() || isPlayingShuffleAll){
            if (this.lastPlayedSongIndex < currentList.size() - 1) {
                this.lastPlayedSongIndex += 1;
            } else {
                this.lastPlayedSongIndex = 0;
            }
        }

        prepareSource(this.lastPlayedSongIndex);
        play();
    }


    public void previous() {
        if (player.getShuffleModeEnabled()) {
            //random
            Random shuffleIndex = new Random();
            this.lastPlayedSongIndex = shuffleIndex.nextInt(currentList.size());
            prepareSource(this.lastPlayedSongIndex);
            play();
        }

        else if (!player.getShuffleModeEnabled() || isPlayingShuffleAll){
            if (this.lastPlayedSongIndex == 0) {
                player.seekTo(0, 0);
                play();
            } else if (this.lastPlayedSongIndex > 0) {
                this.lastPlayedSongIndex -= 1;
                prepareSource(this.lastPlayedSongIndex);
                play();
            }
        }

    }


    public void updatePlayerView(SongItem currentSongItem) {
        if (currentSongItem.getSong() != null) {
            String songCover = null;
            Cursor coverCursor = context.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                    MediaStore.Audio.Albums._ID + " = ?",
                    new String[]{String.valueOf(currentSongItem.getSong().getAlbumId())},
                    null);
            if (coverCursor.moveToFirst()) {
                songCover = coverCursor.getString(coverCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
            }

            if (songCover != null) {
                Glide.with(context).load(songCover).apply(new RequestOptions().centerCrop()).into(songBackground);
            } else {
                Glide.with(context).load(R.drawable.default_song_image).apply(new RequestOptions().centerCrop()).into(songBackground);
            }

            songName.setText(currentSongItem.getSong().getSongTitle());
            artistName.setText(currentSongItem.getSong().getArtistName());
        } else {
            songName.setText("???");
            artistName.setText("???");
            Glide.with(context).load(R.drawable.default_song_image).apply(new RequestOptions().centerCrop()).into(songBackground);
        }
    }


    public void saveLastPlayedSong() {
        Gson gson = new Gson();

        SharedPreferences.Editor editor = lastPlayedSongPreferences.edit();
        //current play list
        String currentPlayListInString = gson.toJson(currentList);
        editor.putString(CURRENT_PLAYLIST_TAG, currentPlayListInString);

        //current song index
        editor.putInt(LAST_PLAYED_SONG_INDEX_TAG, this.lastPlayedSongIndex);

        //current shuffle mode
        editor.putBoolean(CURRENT_SHUFFLE_MODE_TAG, isShuffle);

        //current repeat mode
        editor.putBoolean(CURRENT_REPEAT_MODE_TAG, isRepeat);

        //current song playback position
        editor.putLong(CURRENT_PLAYBACK_POSITION, player.getCurrentPosition());

        editor.apply();
    }

    private List<SongItem> getPlayList(List<Item> itemList) {
        List<SongItem> songItemList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            songItemList.add((SongItem) itemList.get(i));
        }
        return songItemList;
    }




}

