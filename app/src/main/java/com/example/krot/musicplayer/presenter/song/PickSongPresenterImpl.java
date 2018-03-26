package com.example.krot.musicplayer.presenter.song;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.example.krot.musicplayer.model.Item;
import com.example.krot.musicplayer.model.SongItem;
import com.example.krot.musicplayer.presenter.PickSongContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Krot on 3/26/18.
 */

public class PickSongPresenterImpl implements PickSongContract.PickSongPresenter {

    private PickSongContract.PickSongView pickSongView;

    public PickSongPresenterImpl(PickSongContract.PickSongView pickSongView) {
        this.pickSongView = pickSongView;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void updatePlayList(final List<Item> oldList,final int index) {
        new AsyncTask<Void, Void, List<Item>>() {
            @Override
            protected List<Item> doInBackground(Void... voids) {
                List<Item> newList = new ArrayList<>();
                for (int i = 0; i < oldList.size(); i++) {
                    Item currentItem = oldList.get(i);
                    if (currentItem instanceof SongItem) {
                        SongItem songItem = (SongItem) currentItem;
                        newList.add(songItem);
                    } else {
                        newList.add(currentItem);
                    }
                }
                return newList;
            }

            @Override
            protected void onPostExecute(List<Item> itemList) {
                pickSongView.updatePlayListUI(itemList, index);
            }
        }.execute();
    }
}
