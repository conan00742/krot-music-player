package com.example.krot.musicplayer.presenter;

import com.example.krot.musicplayer.model.Item;

import java.util.List;

/**
 * Created by Krot on 3/26/18.
 */

public interface PickSongContract {

    interface PickSongView {
        void updatePlayListUI(List<Item> newList, int index);
    }


    interface PickSongPresenter {
        void updatePlayList(List<Item> oldList, int index);
    }
}
