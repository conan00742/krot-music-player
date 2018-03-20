package com.example.krot.musicplayer.adapter;

import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.example.krot.musicplayer.R;
import com.example.krot.musicplayer.event_bus.RxBus;
import com.example.krot.musicplayer.viewholder.ItemBaseViewHolder;
import com.example.krot.musicplayer.viewholder.QueueViewHolder;

/**
 * Created by Krot on 3/1/18.
 */

public class QueueAdapter extends ItemBaseAdapter {


    public QueueAdapter() {
    }

    @Override
    public ItemBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new QueueViewHolder(parent, R.layout.queue_item);
    }
}
