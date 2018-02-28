package com.example.krot.musicplayer.playlist;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;
import com.example.krot.musicplayer.R;
import com.example.krot.musicplayer.SongPlaybackManager;
import com.example.krot.musicplayer.adapter.SongItemAdapter;
import com.example.krot.musicplayer.event_bus.EventPlaySong;
import com.example.krot.musicplayer.event_bus.EventShuffleAllSongs;
import com.example.krot.musicplayer.event_bus.RxBus;
import com.example.krot.musicplayer.model.Item;
import com.example.krot.musicplayer.model.SongItem;
import com.example.krot.musicplayer.presenter.song.SongItemContract;
import com.example.krot.musicplayer.presenter.song.SongItemPresenterImpl;
import com.google.android.exoplayer2.Player;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class PlayListActivity extends AppCompatActivity implements SongItemContract.SongItemView {

    private static final int PERMISSION_CODE = 101;
    private static final int REQUEST_PERMISSION_SETTING = 90;
    private static final String PLAYLIST_TAG = "PLAY_LIST_TAG";
    private static final String COUNT = "COUNT";


    @BindView(R.id.retrieving_song_progress_bar)
    ProgressBar retrieveProgressbar;
    @BindView(R.id.music_list)
    RecyclerView musicRecyclerView;
    @BindView(R.id.container)
    View bottomSheetPlayList;
    @BindView(R.id.img_track_background)
    ImageView songBackground;
    @BindView(R.id.tv_track_name)
    TextView songName;
    @BindView(R.id.tv_artist_name)
    TextView artistName;
    @BindView(R.id.playback_container)
    FrameLayout playbackContainer;


    @Nullable
    private List<Item> shuffledItemList;
    private SongPlaybackManager manager;
    private BottomSheetBehavior behavior;
    private RxBus bus;
    private Disposable disposable;
    private SharedPreferences lastSongPreferences;
    private int count;

    @OnClick(R.id.bottom_sheet_playlist)
    public void showPlaylist() {
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        }

    }

    private SongItemContract.SongItemPresenter songItemPresenter;
    private SongItemAdapter songItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback_song);
        ButterKnife.bind(this);
        lastSongPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        behavior = BottomSheetBehavior.from(bottomSheetPlayList);
        songItemPresenter = new SongItemPresenterImpl(this);
        setupAdapter();

        count = lastSongPreferences.getInt(COUNT, 0);
        manager = new SongPlaybackManager(PlayListActivity.this, bus, playbackContainer, songName, artistName, songBackground, songItemPresenter.getItemList(), count);
        manager.setOriginalList(songItemPresenter.getItemList());
    }

    @Override
    protected void onStart() {
        super.onStart();
        disposable = bus.toObserverable().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (o instanceof EventPlaySong) {
                    //Kiểm tra xem có shuffle hay ko
                    EventPlaySong eventPlaySong = (EventPlaySong) o;
                    Log.i("WTF", "pos = " + eventPlaySong.getPosition());
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    manager.setLastPlayedSongIndex(eventPlaySong.getPosition() - 1);
                    if (manager.player.getPlaybackState() == Player.STATE_READY && manager.player.getPlayWhenReady()) {
                        manager.pause();
                        manager.prepareSource(eventPlaySong.getPosition() - 1);
                    } else {
                        manager.prepareSource(eventPlaySong.getPosition() - 1);
                    }


                    manager.play();
                    manager.player.setShuffleModeEnabled(false);

                }

                else if (o instanceof EventShuffleAllSongs) {
                    //1. re-shuffle the original list
                    List<Item> originalList = songItemPresenter.getItemList();


                    if (shuffledItemList == null || shuffledItemList.isEmpty()) {
                        shuffledItemList = randomShuffle(originalList);
                    } else {
                        shuffledItemList = randomShuffle(shuffledItemList);
                    }

                    for (int i = 0; i < shuffledItemList.size(); i++) {
                        SongItem currentSong = (SongItem) shuffledItemList.get(i);
                        Log.i("KHIEM", "song[" + i + "] = " + currentSong.getSong().getSongTitle());
                    }

                    manager.setPlayingShuffleAll(true);
                    manager.setCurrentList(shuffledItemList);
                    manager.player.setShuffleModeEnabled(true);
                    if (manager.player.getPlaybackState() == Player.STATE_READY && manager.player.getPlayWhenReady()) {
                        manager.pause();
                    }

                    manager.prepareSource(0);
                    //2. play first song in the new list
                    manager.play();


                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

            }
        });
    }


    private List<Item> randomShuffle(List<Item> currentPlayList) {
        List<Item> newList = new ArrayList<>();
        int index;
        Item tempItem;
        Random random = new Random();
        for (int i = currentPlayList.size() - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            tempItem = currentPlayList.get(index);
            newList.add(tempItem);
        }

        return newList;
    }


    @Override
    protected void onResume() {
        super.onResume();
        //ask for permissions for SDK 23+
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_CODE);
        } else {
            songItemPresenter.loadData();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposable.dispose();
        manager.saveLastPlayedSong();
        count += 1;
        SharedPreferences.Editor countEditor = lastSongPreferences.edit();
        countEditor.putInt(COUNT, count);
        countEditor.apply();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("WTF", "onDestroy");
        manager.releasePlayer();
    }


    private boolean hasPermissions(Context context, String... permissionQueue) {
        if (context != null && permissionQueue != null) {
            for (String permission : permissionQueue) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        songItemPresenter.loadData();
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        boolean shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (shouldShowRationale) {
                            //show the reason why user must grant STORAGE permission
                            //show dialog
                            new AlertDialog.Builder(this).setTitle("Permission Denied").setMessage(R.string.permission_rationale).setPositiveButton("RE-TRY", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(PlayListActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
                                }
                            }).setNegativeButton("I'M SURE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();

                        } else {
                            //never ask again
                            //close dialog and do nothing
                            new AlertDialog.Builder(this)
                                    .setTitle("Grant permission")
                                    .setMessage(R.string.app_setting_permission)
                                    .setPositiveButton("Open", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent appSettingIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                                            appSettingIntent.setData(uri);
                                            startActivityForResult(appSettingIntent, REQUEST_PERMISSION_SETTING);
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).show();
                        }
                    }
                }
                break;
        }
    }


    private void setupAdapter() {
        bus = new RxBus();
        songItemAdapter = new SongItemAdapter(this, bus);
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

    @Override
    public void showProgressBar() {
        retrieveProgressbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        retrieveProgressbar.setVisibility(View.GONE);
    }


}
