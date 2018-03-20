package com.example.krot.musicplayer.queue;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;
import com.example.krot.musicplayer.R;
import com.example.krot.musicplayer.SongPlaybackManager;
import com.example.krot.musicplayer.adapter.QueueAdapter;
import com.example.krot.musicplayer.event_bus.EventPlaySelectedQueueSong;
import com.example.krot.musicplayer.event_bus.RxBus;
import com.example.krot.musicplayer.model.Item;
import com.example.krot.musicplayer.model.SongItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

import static com.example.krot.musicplayer.AppConstantTag.CURRENT_PLAYLIST_TAG;

public class QueuePlayListActivity extends AppCompatActivity {

    private static final String PREFIX = QueuePlayListActivity.class.getSimpleName();

    @BindView(R.id.icon_exit_queue)
    ImageView icExitQueue;
    @BindView(R.id.playing_queue)
    RecyclerView playingQueueRecyclerView;

    @OnClick(R.id.icon_exit_queue)
    public void exitQueue() {
        finish();
        overridePendingTransition(R.anim.slide_in_from_left_animation, R.anim.slide_out_to_right_animation);
    }

    private QueueAdapter queueAdapter;
    private SharedPreferences queuePreferences;
    private SongPlaybackManager manager;
    private RxBus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("WTF", PREFIX + ": onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_play_list);
        ButterKnife.bind(this);
        queuePreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        Gson gson = new Gson();
        String queueList = queuePreferences.getString(CURRENT_PLAYLIST_TAG, "[]");
        List<SongItem> currentQueueSongItemList = gson.fromJson(queueList, new TypeToken<List<SongItem>>(){}.getType());
        List<Item> itemList = new ArrayList<>();
        itemList.addAll(currentQueueSongItemList);
        setUpQueueAdapter(itemList);
    }

    @Override
    protected void onStart() {
        Log.i("WTF", PREFIX + ": onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.i("WTF", PREFIX + ": onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i("WTF", PREFIX + ": onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i("WTF", PREFIX + ": onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i("WTF", PREFIX + ": onDestroy");
        super.onDestroy();
    }

    private void setUpQueueAdapter(List<Item> itemList) {
        queueAdapter = new QueueAdapter();
        queueAdapter.setItemList(itemList);
        playingQueueRecyclerView.setAdapter(queueAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        playingQueueRecyclerView.setLayoutManager(manager);
        playingQueueRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Drawable queueItemDecoration = ContextCompat.getDrawable(this, R.drawable.music_divider_decoration);
        playingQueueRecyclerView.addItemDecoration(new DividerItemDecoration(queueItemDecoration));

    }


}
