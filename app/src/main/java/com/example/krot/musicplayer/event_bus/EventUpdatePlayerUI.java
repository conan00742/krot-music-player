package com.example.krot.musicplayer.event_bus;

import android.support.annotation.Nullable;

import com.example.krot.musicplayer.model.SongItem;

/**
 * Created by Krot on 3/5/18.
 */

public class EventUpdatePlayerUI {

    @Nullable
    private final SongItem currentSongItem;

    private int currentPlaybackPosition;

    public EventUpdatePlayerUI(SongItem currentSongItem, int currentPlaybackPosition) {
        this.currentSongItem = currentSongItem;
        this.currentPlaybackPosition = currentPlaybackPosition;
    }

    @Nullable
    public SongItem getCurrentSongItem() {
        return currentSongItem;
    }

    public int getCurrentPlaybackPosition() {
        return currentPlaybackPosition;
    }
}
