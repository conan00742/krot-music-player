package com.example.krot.musicplayer.presenter;

import com.example.krot.musicplayer.model.Item;
import com.example.krot.musicplayer.model.SongItem;

import java.util.List;

/**
 * Created by Krot on 2/6/18.
 */

public interface SongItemContract {

    interface SongItemView {
        void showProgressBar();
        void hideProgressBar();
        void setOriginalList(List<Item> originalList);
    }

    interface SongItemPresenter {
        void loadData();
    }
}
