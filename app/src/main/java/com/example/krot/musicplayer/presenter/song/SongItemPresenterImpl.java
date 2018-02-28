package com.example.krot.musicplayer.presenter.song;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.krot.musicplayer.model.Item;
import com.example.krot.musicplayer.model.ShuffleAllSongsItem;
import com.example.krot.musicplayer.model.SongItem;
import com.example.krot.musicplayer.playlist.PlayListActivity;
import com.example.krot.musicplayer.repository.SongItemRepository;

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
                songItemView.hideProgressBar();
                songItemView.displaySongItemList(items);
            }
        }.execute();
        songItemView.displaySongItemList(songItemRepository.retrieveSongListFromExternalStorage());
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
