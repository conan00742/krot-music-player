package com.example.krot.musicplayer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.example.krot.musicplayer.R;
import com.example.krot.musicplayer.event_bus.RxBus;
import com.example.krot.musicplayer.model.Item;
import com.example.krot.musicplayer.model.ShuffleAllSongsItem;
import com.example.krot.musicplayer.model.SongItem;
import com.example.krot.musicplayer.viewholder.ItemBaseViewHolder;
import com.example.krot.musicplayer.viewholder.ShuffleAllSongsViewHolder;
import com.example.krot.musicplayer.viewholder.SongItemViewHolder;

/**
 * Created by Krot on 2/6/18.
 */

public class SongItemAdapter extends ItemBaseAdapter {

    private static final int SHUFFLE_ALL_SONGS_ITEM = 1;
    private static final int SONG_ITEM = 2;

    @NonNull
    private Context mContext;

    @NonNull
    private RxBus bus;

    public SongItemAdapter(@NonNull Context mContext, @NonNull RxBus bus) {
        this.mContext = mContext;
        this.bus = bus;
    }

    @Override
    public ItemBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case SHUFFLE_ALL_SONGS_ITEM:
                return new ShuffleAllSongsViewHolder(parent, R.layout.shuffle_all_songs_item, mContext, bus);
            default:
                return new SongItemViewHolder(parent, R.layout.song_item, mContext, bus);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Item currentItem = getItemAt(position);
        if (currentItem instanceof ShuffleAllSongsItem) {
            return SHUFFLE_ALL_SONGS_ITEM;
        }

        else if (currentItem instanceof SongItem) {
            return SONG_ITEM;
        }

        else {
            throw new RuntimeException("not support " + currentItem);
        }
    }
}
