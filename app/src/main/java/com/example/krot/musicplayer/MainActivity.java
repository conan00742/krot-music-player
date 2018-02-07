package com.example.krot.musicplayer;

import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;
import com.example.krot.musicplayer.adapter.SongItemAdapter;
import com.example.krot.musicplayer.model.Item;
import com.example.krot.musicplayer.presenter.song.SongItemContract;
import com.example.krot.musicplayer.presenter.song.SongItemPresenterImpl;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements SongItemContract.SongItemView{

    @BindView(R.id.music_list)
    RecyclerView musicRecyclerView;

    private SongItemContract.SongItemPresenter songItemPresenter;
    private SongItemAdapter songItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        songItemPresenter = new SongItemPresenterImpl(this);
        setupAdapter();
        songItemPresenter.loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //ask for permissions
    }


    private void setupAdapter() {
        songItemAdapter = new SongItemAdapter(this);
        musicRecyclerView.setAdapter(songItemAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        musicRecyclerView.setLayoutManager(manager);
        musicRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Drawable musicDividerDecoration = ContextCompat.getDrawable(this, R.drawable.music_divider_decoration);
        musicRecyclerView.addItemDecoration(new DividerItemDecoration(musicDividerDecoration));
    }


    @Override
    public void displaySongItemList(List<Item> itemList) {

        songItemAdapter.setItemList(itemList);
    }
}
