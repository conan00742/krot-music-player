package com.example.krot.musicplayer.mvp;

import com.example.krot.musicplayer.model.Item;

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
