package com.example.krot.musicplayer.event_bus;

import android.support.annotation.Nullable;

import com.example.krot.musicplayer.model.SongItem;

/**
 * Created by Krot on 3/26/18.
 */

public class EventUpdateMiniPlaybackUI {

    @Nullable
    private final SongItem currentSongItem;

    private int currentPlaybackPosition;

    private int currentSongIndex;

    public EventUpdateMiniPlaybackUI(SongItem currentSongItem, int currentPlaybackPosition, int currentSongIndex) {
        this.currentSongItem = currentSongItem;
        this.currentPlaybackPosition = currentPlaybackPosition;
        this.currentSongIndex = currentSongIndex;
    }

    @Nullable
    public SongItem getCurrentSongItem() {
        return currentSongItem;
    }

    public int getCurrentPlaybackPosition() {
        return currentPlaybackPosition;
    }

    public int getCurrentSongIndex() {
        return currentSongIndex;
    }
}
