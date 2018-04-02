package com.example.krot.musicplayer.mvp;

import com.example.krot.musicplayer.model.Item;

import java.util.List;

/**
 * Created by Krot on 4/2/18.
 */

public interface QueuePlaylistContract {

    interface QueuePlayListView {
        void showLoadingQueue();
        void hideLoadingQueue();
        void displayQueuePlaylist(List<Item> newList, int currentPlaybackSongIndex);
    }

    interface QueuePlayListPresenter {
        void getQueuePlayList();
    }
}
