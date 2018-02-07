package com.example.krot.musicplayer.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.example.krot.musicplayer.R;
import com.example.krot.musicplayer.viewholder.ItemBaseViewHolder;
import com.example.krot.musicplayer.viewholder.SongItemViewHolder;

/**
 * Created by Krot on 2/6/18.
 */

public class SongItemAdapter extends ItemBaseAdapter {

    @NonNull
    private Context mContext;

    public SongItemAdapter(@NonNull Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ItemBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SongItemViewHolder(parent, R.layout.song_item, mContext);
    }
}
