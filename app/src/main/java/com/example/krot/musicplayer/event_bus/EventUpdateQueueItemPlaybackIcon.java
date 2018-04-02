package com.example.krot.musicplayer.event_bus;

import android.support.annotation.NonNull;

import com.example.krot.musicplayer.viewholder.QueueViewHolder;

/**
 * Created by Krot on 4/2/18.
 */

public class EventUpdateQueueItemPlaybackIcon {

    @NonNull
    private final QueueViewHolder queueViewHolder;

    public EventUpdateQueueItemPlaybackIcon(@NonNull QueueViewHolder queueViewHolder) {
        this.queueViewHolder = queueViewHolder;
    }

    @NonNull
    public QueueViewHolder getQueueViewHolder() {
        return queueViewHolder;
    }
}
