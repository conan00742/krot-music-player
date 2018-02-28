package com.example.krot.musicplayer.event_bus;

import com.example.krot.musicplayer.viewholder.SongItemViewHolder;

/**
 * Created by Krot on 2/9/18.
 */

public class EventPlaySong {

    private int position;

    private final SongItemViewHolder holder;

    public EventPlaySong(int position, SongItemViewHolder holder) {
        this.position = position;
        this.holder = holder;
    }

    public int getPosition() {
        return position;
    }

    public SongItemViewHolder getHolder() {
        return holder;
    }
}
