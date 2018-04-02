package com.example.krot.musicplayer.mvp.song;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.krot.musicplayer.SongPlaybackManager;
import com.example.krot.musicplayer.model.Item;
import com.example.krot.musicplayer.model.SongItem;
import com.example.krot.musicplayer.mvp.QueuePlaylistContract;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static com.example.krot.musicplayer.AppConstantTag.CURRENT_PLAYLIST_TAG;

/**
 * Created by Krot on 4/2/18.
 */

public class QueuePlayListPresenterImpl implements QueuePlaylistContract.QueuePlayListPresenter {

    @NonNull
    private QueuePlaylistContract.QueuePlayListView queuePlayListView;

    private SharedPreferences queuePreferences;

    public QueuePlayListPresenterImpl(@NonNull QueuePlaylistContract.QueuePlayListView queuePlayListView, SharedPreferences queuePreferences) {
        this.queuePlayListView = queuePlayListView;
        this.queuePreferences = queuePreferences;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void getQueuePlayList() {
        new AsyncTask<Void, Void, List<Item>>() {

            @Override
            protected void onPreExecute() {
                queuePlayListView.showLoadingQueue();
            }

            @Override
            protected List<Item> doInBackground(Void... voids) {
                Gson gson = new Gson();
                String queueList = queuePreferences.getString(CURRENT_PLAYLIST_TAG, "[]");
                List<SongItem> currentQueueSongItemList = gson.fromJson(queueList, new TypeToken<List<SongItem>>() {
                }.getType());
                List<Item> newList = new ArrayList<>();
                newList.addAll(currentQueueSongItemList);

                return newList;
            }

            @Override
            protected void onPostExecute(List<Item> itemList) {
                queuePlayListView.hideLoadingQueue();
                int currentIndex = SongPlaybackManager.getSongPlaybackManagerInstance().getLastPlayedSongIndex();
                Log.i("LINA", "onPostExecute: index = " + currentIndex);
                queuePlayListView.displayQueuePlaylist(itemList, currentIndex);
            }
        }.execute();
    }
}
