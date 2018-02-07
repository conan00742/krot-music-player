package com.example.krot.musicplayer.presenter.song;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.krot.musicplayer.MainActivity;
import com.example.krot.musicplayer.model.SongItem;
import com.example.krot.musicplayer.repository.SongItemRepository;

import java.util.List;

/**
 * Created by Krot on 2/6/18.
 */

public class SongItemPresenterImpl implements SongItemContract.SongItemPresenter {

    @NonNull
    private Context mContext;

    private MainActivity activity;

    @NonNull
    private SongItemContract.SongItemView songItemView;

    @NonNull
    private SongItemRepository songItemRepository;


    public SongItemPresenterImpl(@NonNull MainActivity activity) {
        this.activity = activity;
        this.songItemView = activity;
        songItemRepository = new SongItemRepository(activity);
    }



    @Override
    public void loadData() {
        songItemView.displaySongItemList(songItemRepository.retrieveSongListFromExternalStorage());
    }
}
