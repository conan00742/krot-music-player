package com.example.krot.musicplayer.presenter.song;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.krot.musicplayer.home.HomeActivity;
import com.example.krot.musicplayer.model.Item;
import com.example.krot.musicplayer.model.ShuffleAllSongsItem;
import com.example.krot.musicplayer.model.SongItem;
import com.example.krot.musicplayer.playlist.PlayListActivity;
import com.example.krot.musicplayer.presenter.SongItemContract;
import com.example.krot.musicplayer.repository.SongItemRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Krot on 2/6/18.
 */

public class SongItemPresenterImpl implements SongItemContract.SongItemPresenter {

    @NonNull
    private SongItemContract.SongItemView songItemView;

    @NonNull
    private SongItemRepository songItemRepository;

    private HomeActivity activity;


    public SongItemPresenterImpl(@NonNull HomeActivity activity) {
        this.songItemView = activity;
        this.activity = activity;
        songItemRepository = new SongItemRepository(activity);
    }


    @SuppressLint("StaticFieldLeak")
    @Override
    public void loadData() {
        new AsyncTask<Void, Void, List<Item>>() {

            @Override
            protected void onPreExecute() {
                songItemView.showProgressBar();
            }

            @Override
            protected List<Item> doInBackground(Void... voids) {
                List<Item> songItemList = songItemRepository.retrieveSongListFromExternalStorage();
                songItemList.add(0, new ShuffleAllSongsItem());
                return songItemList;
            }

            @Override
            protected void onPostExecute(List<Item> items) {
                songItemView.hideProgressBar();
                songItemView.setOriginalList(items);
            }
        }.execute();
    }


}
