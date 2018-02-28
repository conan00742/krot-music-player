package com.example.krot.musicplayer.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.example.krot.musicplayer.model.Item;
import com.example.krot.musicplayer.model.ShuffleAllSongsItem;
import com.example.krot.musicplayer.model.SongItem;
import com.example.krot.musicplayer.viewholder.ItemBaseViewHolder;

import java.util.List;

/**
 * Created by Krot on 2/6/18.
 */

public abstract class ItemBaseAdapter extends RecyclerView.Adapter<ItemBaseViewHolder> {


    @Nullable
    private List<Item> mCurrentItemList;

    @Nullable
    public List<Item> getCurrentItemList() {
        return mCurrentItemList;
    }

    public void updateListItem(@Nullable List<Item> newItemList) {

    }

    public void setItemList(@Nullable List<Item> mItemList) {
        this.mCurrentItemList = mItemList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ItemBaseViewHolder holder, int position) {
        holder.bindData(getItemAt(position));
    }


    public Item getItemAt(int position) {
        return (mCurrentItemList != null ? mCurrentItemList.get(position) : null);
    }

    @Override
    public int getItemCount() {
        return (mCurrentItemList != null ? mCurrentItemList.size() : 0);
    }

}
