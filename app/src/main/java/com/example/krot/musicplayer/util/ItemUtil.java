package com.example.krot.musicplayer.util;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.example.krot.musicplayer.model.Item;

import java.util.List;

/**
 * Created by Krot on 3/22/18.
 */

public class ItemUtil extends DiffUtil.Callback {

    @Nullable
    private List<Item> oldList;

    @Nullable
    private List<Item> newList;

    public ItemUtil(List<Item> oldList, List<Item> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return (oldList == null ? 0 : oldList.size());
    }

    @Override
    public int getNewListSize() {
        return (newList == null ? 0 : newList.size());
    }

    @Override
    public boolean areItemsTheSame(int i, int i1) {
        return (oldList.get(i).equals(newList.get(i1)));
    }

    @Override
    public boolean areContentsTheSame(int i, int i1) {
        return (oldList.get(i).sameContent(newList.get(i1)));
    }
}
