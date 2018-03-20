package com.example.krot.musicplayer.presenter.song;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.krot.musicplayer.SongPlaybackManager;
import com.example.krot.musicplayer.model.Item;
import com.example.krot.musicplayer.model.ShuffleAllSongsItem;
import com.example.krot.musicplayer.model.SongItem;
import com.example.krot.musicplayer.playlist.PlayListActivity;
import com.example.krot.musicplayer.repository.SongItemRepository;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Krot on 2/6/18.
 */

public class SongItemPresenterImpl implements SongItemContract.SongItemPresenter {

    @NonNull
    private Context mContext;

    private PlayListActivity activity;

    @NonNull
    private SongItemContract.SongItemView songItemView;

    @NonNull
    private SongItemRepository songItemRepository;

    @NonNull
    private SharedPreferences originalPlaylistPreferences;


    public SongItemPresenterImpl(@NonNull PlayListActivity activity) {
        this.activity = activity;
        this.songItemView = activity;
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
                Log.i("WTF", "onPostExecute");
                songItemView.hideProgressBar();
                songItemView.displaySongItemList(items);
            }
        }.execute();
    }

    @Override
    public List<Item> getItemList() {
        return songItemRepository.retrieveSongListFromExternalStorage();
    }


    @Override
    public SongItem getDefaultSong() {
        SongItem defaultSong = (SongItem) songItemRepository.retrieveSongListFromExternalStorage().get(0);
        return defaultSong;
    }
}
