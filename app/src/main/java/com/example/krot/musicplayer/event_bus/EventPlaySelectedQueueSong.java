package com.example.krot.musicplayer.event_bus;

import com.example.krot.musicplayer.viewholder.QueueViewHolder;

/**
 * Created by Krot on 3/1/18.
 */

public class EventPlaySelectedQueueSong {
    private int position;

    private final QueueViewHolder holder;

    public EventPlaySelectedQueueSong(int position, QueueViewHolder holder) {
        this.position = position;
        this.holder = holder;
    }

    public int getPosition() {
        return position;
    }

    public QueueViewHolder getHolder() {
        return holder;
    }
}
