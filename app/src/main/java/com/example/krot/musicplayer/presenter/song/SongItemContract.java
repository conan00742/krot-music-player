package com.example.krot.musicplayer.presenter.song;

import com.example.krot.musicplayer.model.Item;
import com.example.krot.musicplayer.model.SongItem;

import java.util.List;

/**
 * Created by Krot on 2/6/18.
 */

public interface SongItemContract {

    interface SongItemView {
        void displaySongItemList(List<Item> itemList);
        void showProgressBar();
        void hideProgressBar();
        void updatePlayListUI(List<Item> newList, int index);
    }

    interface SongItemPresenter {
        void loadData();
        List<Item> getItemList();
        SongItem getDefaultSong();
        void updatePlayList(List<Item> oldList, int index);
    }
}
