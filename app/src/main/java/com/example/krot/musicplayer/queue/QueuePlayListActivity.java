package com.example.krot.musicplayer.queue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;
import com.example.krot.musicplayer.R;
import com.example.krot.musicplayer.SongPlaybackManager;
import com.example.krot.musicplayer.adapter.QueueAdapter;
import com.example.krot.musicplayer.event_bus.EventPlaySelectedQueueSong;
import com.example.krot.musicplayer.event_bus.RxBus;
import com.example.krot.musicplayer.model.Item;
import com.example.krot.musicplayer.model.SongItem;
import com.example.krot.musicplayer.mvp.QueuePlaylistContract;
import com.example.krot.musicplayer.mvp.song.QueuePlayListPresenterImpl;
import com.example.krot.musicplayer.playlist.PlayListActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.Subject;

import static com.example.krot.musicplayer.AppConstantTag.CURRENT_PLAYLIST_TAG;

public class QueuePlayListActivity extends AppCompatActivity implements QueuePlaylistContract.QueuePlayListView {

    private static final String PREFIX = QueuePlayListActivity.class.getSimpleName();

    @BindView(R.id.icon_exit_queue)
    ImageView icExitQueue;

    @BindView(R.id.playing_queue)
    RecyclerView playingQueueRecyclerView;

    @BindView(R.id.loading_queue_progress_bar)
    ProgressBar loadingQueueProgressBar;

    private List<Item> currentList;

    @OnClick(R.id.icon_exit_queue)
    public void exitQueue() {
        SongPlaybackManager.getSongPlaybackManagerInstance().saveLastPlayedSong();
        SongPlaybackManager.getSongPlaybackManagerInstance().setChangeSong(false);
        Intent exitQueuePlaylist = new Intent(QueuePlayListActivity.this, PlayListActivity.class);
        startActivity(exitQueuePlaylist);
        overridePendingTransition(R.anim.slide_in_from_left_animation, R.anim.slide_out_to_right_animation);
    }

    private Disposable queueDisposable;
    private QueueAdapter queueAdapter;
    private SharedPreferences queuePreferences;
    private int previousSongIndex;
    private int initialCount = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_play_list);
        ButterKnife.bind(this);
        initialCount += 1;
        queuePreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        QueuePlaylistContract.QueuePlayListPresenter queuePlayListPresenter = new QueuePlayListPresenterImpl(this, queuePreferences);
        setUpQueueAdapter();
        queuePlayListPresenter.getQueuePlayList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        queueDisposable = RxBus.getInstance().toObserverable().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (o instanceof EventPlaySelectedQueueSong) {
                    EventPlaySelectedQueueSong eventPlaySelectedQueueSong = (EventPlaySelectedQueueSong) o;
                    SongItem lastPlayedSong = (SongItem) queueAdapter.getItemAt(previousSongIndex);
                    lastPlayedSong.setSelected(false);
                    currentList.remove(previousSongIndex);
                    currentList.add(previousSongIndex, lastPlayedSong);

                    previousSongIndex = eventPlaySelectedQueueSong.getPosition();
                    SongItem newItem = (SongItem) queueAdapter.getItemAt(previousSongIndex);
                    newItem.setSelected(true);
                    currentList.remove(previousSongIndex);
                    currentList.add(previousSongIndex, newItem);


                    queueAdapter.updateListItem(currentList);
                    playingQueueRecyclerView.getItemAnimator().setChangeDuration(0);

                    SongPlaybackManager.getSongPlaybackManagerInstance().setChangeSong(true);

                    //PREPARE SOURCE TO PLAY SELECTED SONG
                    if (SongPlaybackManager.getSongPlaybackManagerInstance().isShuffleOn()) {
                        List<Item> defaultPlayList = SongPlaybackManager.getSongPlaybackManagerInstance().getOriginalList();
                        SongPlaybackManager.getSongPlaybackManagerInstance().setCurrentList(defaultPlayList);
                        SongPlaybackManager.getSongPlaybackManagerInstance().setPlayerShuffleOff();
                    }


                    SongPlaybackManager.getSongPlaybackManagerInstance().setLastPlayedSongIndex(previousSongIndex);

                    if (SongPlaybackManager.getSongPlaybackManagerInstance().isPlaying()) {
                        SongPlaybackManager.getSongPlaybackManagerInstance().pause();
                        SongPlaybackManager.getSongPlaybackManagerInstance().prepareSource(previousSongIndex);
                    } else {
                        SongPlaybackManager.getSongPlaybackManagerInstance().prepareSource(previousSongIndex);
                    }

                    SongPlaybackManager.getSongPlaybackManagerInstance().play();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        queueDisposable.dispose();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void setUpQueueAdapter() {
        queueAdapter = new QueueAdapter(this);
        playingQueueRecyclerView.setAdapter(queueAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        playingQueueRecyclerView.setLayoutManager(manager);
        playingQueueRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Drawable queueItemDecoration = ContextCompat.getDrawable(this, R.drawable.music_divider_decoration);
        playingQueueRecyclerView.addItemDecoration(new DividerItemDecoration(queueItemDecoration));

    }


    @Override
    public void showLoadingQueue() {
        loadingQueueProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingQueue() {
        loadingQueueProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void displayQueuePlaylist(List<Item> newList, int currentPlaybackSongIndex) {
        currentList = newList;
        queueAdapter.setItemList(newList);
        Log.i("LINA", "QueuePlayListActivity: displayQueuePlayList: list:size = " + newList.size() + " - index = " + currentPlaybackSongIndex);
        if (initialCount > 0) {
            SongItem lastPlayedSong = (SongItem) queueAdapter.getItemAt(currentPlaybackSongIndex);
            lastPlayedSong.setSelected(false);
            newList.remove(currentPlaybackSongIndex);
            newList.add(currentPlaybackSongIndex, lastPlayedSong);
        }

        previousSongIndex = currentPlaybackSongIndex;
        SongItem newItem = (SongItem) queueAdapter.getItemAt(previousSongIndex);
        newItem.setSelected(true);
        newList.remove(previousSongIndex);
        newList.add(previousSongIndex, newItem);

        queueAdapter.updateListItem(newList);

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) playingQueueRecyclerView.getLayoutManager();
        linearLayoutManager.scrollToPositionWithOffset(currentPlaybackSongIndex, 0);

    }

}
