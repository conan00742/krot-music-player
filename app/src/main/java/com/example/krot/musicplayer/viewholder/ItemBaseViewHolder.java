package com.example.krot.musicplayer.viewholder;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.krot.musicplayer.model.Item;

/**
 * Created by Krot on 2/6/18.
 */

public abstract class ItemBaseViewHolder<T extends Item> extends RecyclerView.ViewHolder {

    @Nullable
    protected T item;

    public ItemBaseViewHolder(ViewGroup parent, int resourceId) {
        super(LayoutInflater.from(parent.getContext()).inflate(resourceId, parent, false));
    }

    @Nullable
    public T getItem() {
        return item;
    }

    public void bindData(@Nullable T item) {
        this.item = item;
    }
}
