package com.example.krot.musicplayer.playlist;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dgreenhalgh.android.simpleitemdecoration.linear.DividerItemDecoration;
import com.example.krot.musicplayer.R;
import com.example.krot.musicplayer.SongPlaybackManager;
import com.example.krot.musicplayer.adapter.SongItemAdapter;
import com.example.krot.musicplayer.event_bus.EventEndSong;
import com.example.krot.musicplayer.event_bus.EventIsPaused;
import com.example.krot.musicplayer.event_bus.EventIsPlaying;
import com.example.krot.musicplayer.event_bus.EventPlaySong;
import com.example.krot.musicplayer.event_bus.EventRepeatOff;
import com.example.krot.musicplayer.event_bus.EventRepeatOn;
import com.example.krot.musicplayer.event_bus.EventShuffleAllSongs;
import com.example.krot.musicplayer.event_bus.EventShuffleOff;
import com.example.krot.musicplayer.event_bus.EventShuffleOn;
import com.example.krot.musicplayer.event_bus.EventUpdatePlayerUI;
import com.example.krot.musicplayer.event_bus.RxBus;
import com.example.krot.musicplayer.model.Item;
import com.example.krot.musicplayer.model.SongItem;
import com.example.krot.musicplayer.presenter.song.SongItemContract;
import com.example.krot.musicplayer.presenter.song.SongItemPresenterImpl;
import com.example.krot.musicplayer.queue.QueuePlayListActivity;
import com.example.krot.musicplayer.repository.SongItemRepository;
import com.example.krot.musicplayer.service.ServiceUtils;
import com.example.krot.musicplayer.service.SongPlaybackService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.example.krot.musicplayer.AppConstantTag.ACTION_CREATE_NOTIFICATION;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_PLAYBACK;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_SAVE_CURRENT_PLAYLIST;
import static com.example.krot.musicplayer.AppConstantTag.FIRST_TIME_INSTALL;
import static com.example.krot.musicplayer.AppConstantTag.ORIGINAL_PLAYLIST;
import static com.example.krot.musicplayer.AppConstantTag.PERMISSION_CODE;
import static com.example.krot.musicplayer.AppConstantTag.REQUEST_PERMISSION_SETTING;

public class PlayListActivity extends AppCompatActivity implements SongItemContract.SongItemView, SeekBar.OnSeekBarChangeListener {

    private static final String PREFIX = PlayListActivity.class.getSimpleName();

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
    @BindView(R.id.icon_playback_option)
    ImageView iconViewQueue;

    //playback controller
    @BindView(R.id.ic_shuffle)
    ImageView icShuffle;

    @OnClick(R.id.ic_shuffle)
    public void doToggleShuffle() {
        if (SongPlaybackManager.getSongPlaybackManagerInstance().isShuffleOn()) {
            SongPlaybackManager.getSongPlaybackManagerInstance().setPlayerShuffleOff();
        } else {
            SongPlaybackManager.getSongPlaybackManagerInstance().setPlayerShuffleOn();
        }
    }

    @BindView(R.id.ic_previous)
    ImageView icPrevious;

    @OnClick(R.id.ic_previous)
    public void doPlayPreviousSong() {
        if (counter != null) {
            counter.cancel();
        }

        SongPlaybackManager.getSongPlaybackManagerInstance().previous();
    }

    @BindView(R.id.ic_playback)
    ImageView icPlayback;

    @OnClick(R.id.ic_playback)
    public void doPlayBack() {
//        if (SongPlaybackManager.getSongPlaybackManagerInstance().isPlaying()) {
//            SongPlaybackManager.getSongPlaybackManagerInstance().pause();
//        } else {
//            SongPlaybackManager.getSongPlaybackManagerInstance().play();
//        }
        sendBroadcast(new Intent(ACTION_PLAYBACK));
    }

    @BindView(R.id.ic_next)
    ImageView icNext;

    @OnClick(R.id.ic_next)
    public void doPlayNextSong() {
        if (counter != null) {
            counter.cancel();
        }

        SongPlaybackManager.getSongPlaybackManagerInstance().next();
    }

    @BindView(R.id.ic_repeat)
    ImageView icRepeat;

    @OnClick(R.id.ic_repeat)
    public void doToggleRepeatMode() {
        if (SongPlaybackManager.getSongPlaybackManagerInstance().isRepeatOn()) {
            SongPlaybackManager.getSongPlaybackManagerInstance().setRepeatOff();
        } else {
            SongPlaybackManager.getSongPlaybackManagerInstance().setRepeatOn();
        }
    }

    @BindView(R.id.tv_current_position)
    TextView tvCurrentPosition;
    @BindView(R.id.song_time_bar)
    SeekBar songTimeBar;
    @BindView(R.id.tv_duration)
    TextView tvDuration;


    @Nullable
    private List<Item> shuffledList;
    private BottomSheetBehavior behavior;
    private Disposable disposable;
    private SharedPreferences lastSongPreferences;
    private int count;
    private Timer counter;
    private long songDuration;
    private int currentSongPosition;
    private int minute = 0;
    private int second = 0;
    private String secondInString;
    private String minuteInString;

    @OnClick(R.id.bottom_sheet_playlist)
    public void showPlaylist() {
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        }

    }

    @OnClick(R.id.icon_playback_option)
    public void viewQueue() {
        Intent viewQueueIntent = new Intent(PlayListActivity.this, QueuePlayListActivity.class);
        startActivity(viewQueueIntent);
        overridePendingTransition(R.anim.slide_in_from_right_animation, R.anim.slide_out_to_left_animation);
    }

    private SongItemContract.SongItemPresenter songItemPresenter;
    private SongItemAdapter songItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback_song);
        ButterKnife.bind(this);


        disposable = RxBus.getInstance().toObserverable().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                //Khi user click vào 1 bài nhạc trong list
                if (o instanceof EventPlaySong) {
                    //Kiểm tra xem có shuffle hay ko
                    EventPlaySong eventPlaySong = (EventPlaySong) o;
                    int selectedSongIndex = eventPlaySong.getPosition() - 1;
                    //EVENT_PICK_A_SONG
                    if (SongPlaybackManager.getSongPlaybackManagerInstance().isShuffleOn()) {
                        List<Item> defaultPlayList = songItemPresenter.getItemList();
                        SongPlaybackManager.getSongPlaybackManagerInstance().setCurrentList(defaultPlayList);
                        SongPlaybackManager.getSongPlaybackManagerInstance().setPlayerShuffleOff();
                    }


                    SongPlaybackManager.getSongPlaybackManagerInstance().setLastPlayedSongIndex(selectedSongIndex);

                    if (SongPlaybackManager.getSongPlaybackManagerInstance().isPlaying()) {
                        SongPlaybackManager.getSongPlaybackManagerInstance().pause();
                        SongPlaybackManager.getSongPlaybackManagerInstance().prepareSource(selectedSongIndex);
                    } else {
                        SongPlaybackManager.getSongPlaybackManagerInstance().prepareSource(selectedSongIndex);
                    }

                    SongPlaybackManager.getSongPlaybackManagerInstance().play();
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                }

                //khi user chọn shuffle all
                else if (o instanceof EventShuffleAllSongs) {
                    //EVENT_SHUFFLE_ALL_SONGS
                    // 1. Re-shuffle the original list
                    List<Item> originalList = songItemPresenter.getItemList();

                    if (shuffledList == null || shuffledList.isEmpty()) {
                        shuffledList = randomShuffle(originalList);
                    } else {
                        shuffledList = randomShuffle(shuffledList);
                    }

                    SongPlaybackManager.getSongPlaybackManagerInstance().setCurrentList(shuffledList);
                    SongPlaybackManager.getSongPlaybackManagerInstance().saveLastPlayedSong();
                    SongPlaybackManager.getSongPlaybackManagerInstance().setPlayerShuffleOn();
                    if (SongPlaybackManager.getSongPlaybackManagerInstance().isPlaying()) {
                        SongPlaybackManager.getSongPlaybackManagerInstance().pause();
                    }

                    SongPlaybackManager.getSongPlaybackManagerInstance().prepareSource(0);

                    //2. Play first song of the shuffled list
                    SongPlaybackManager.getSongPlaybackManagerInstance().play();
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                }

                //event update UI khi play 1 bài nhạc
                else if (o instanceof EventUpdatePlayerUI) {
                    EventUpdatePlayerUI eventUpdatePlayerUI = (EventUpdatePlayerUI) o;
                    currentSongPosition = eventUpdatePlayerUI.getCurrentPlaybackPosition();
                    songDuration = eventUpdatePlayerUI.getCurrentSongItem().getSong().getDuration();
                    updatePlayerView(eventUpdatePlayerUI.getCurrentSongItem(), eventUpdatePlayerUI.getCurrentPlaybackPosition());
                }

                else if (o instanceof EventIsPlaying) {
                    icPlayback.setImageDrawable(ContextCompat.getDrawable(PlayListActivity.this, R.drawable.ic_pause));
                    doSeekToPosition();
                    //start service
                    if (!ServiceUtils.isServiceStarted(SongPlaybackService.class.getName())) {
                        Intent playSongServiceIntent = new Intent(PlayListActivity.this, SongPlaybackService.class);
                        playSongServiceIntent.setAction(ACTION_CREATE_NOTIFICATION);
                        startService(playSongServiceIntent);
                    }
                }

                else if (o instanceof EventIsPaused) {
                    icPlayback.setImageDrawable(ContextCompat.getDrawable(PlayListActivity.this, R.drawable.ic_play));
                    if (counter != null) {
                        stopSeek();
                    }
                }

                else if (o instanceof EventEndSong) {
                    stopSeek();
                }

                else if (o instanceof EventShuffleOn) {
                    icShuffle.setImageDrawable(ContextCompat.getDrawable(PlayListActivity.this, R.drawable.ic_shuffle_on));
                } else if (o instanceof EventShuffleOff) {
                    icShuffle.setImageDrawable(ContextCompat.getDrawable(PlayListActivity.this, R.drawable.ic_shuffle_off));
                } else if (o instanceof EventRepeatOn) {
                    icRepeat.setImageDrawable(ContextCompat.getDrawable(PlayListActivity.this, R.drawable.ic_repeat_on));
                } else if (o instanceof EventRepeatOff) {
                    icRepeat.setImageDrawable(ContextCompat.getDrawable(PlayListActivity.this, R.drawable.ic_repeat_off));
                }

            }
        });




        lastSongPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        behavior = BottomSheetBehavior.from(bottomSheetPlayList);
        songItemPresenter = new SongItemPresenterImpl(this);
        setupAdapter();

        //request permission
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_CODE);
        } else {
            songItemPresenter.loadData();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private List<Item> randomShuffle(List<Item> currentPlayList) {
        List<Item> newList = new ArrayList<>();
        int index;
        Item tempItem;
        Random random = new Random();
        for (int i = currentPlayList.size() - 1; i > 0; i--) {
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

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        SongPlaybackManager.getSongPlaybackManagerInstance().saveLastPlayedSong();
        SharedPreferences.Editor countEditor = lastSongPreferences.edit();
        countEditor.putBoolean(FIRST_TIME_INSTALL, false);
        countEditor.apply();
    }

    @Override
    protected void onRestart() {
        Log.i("WTF", PREFIX + ": onRestart");
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendBroadcast(new Intent(ACTION_SAVE_CURRENT_PLAYLIST));
        disposable.dispose();
        stopService(new Intent(PlayListActivity.this, SongPlaybackService.class));
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

        List<SongItem> playList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            Item currentItem = itemList.get(i);
            if (!(currentItem instanceof SongItem)) {
                continue;
            }

            SongItem currentSongItem = (SongItem) currentItem;
            playList.add(currentSongItem);

        }

        //store the original list to sharedpreferences
        Gson gson = new Gson();
        String originalPlayListInString = gson.toJson(playList);
        SharedPreferences.Editor editor = lastSongPreferences.edit();
        editor.putString(ORIGINAL_PLAYLIST, originalPlayListInString);
        editor.apply();

        //update UI of the playlist recyclerview
        songItemAdapter.setItemList(itemList);
        //set originalList
        SongPlaybackManager.getSongPlaybackManagerInstance().setOriginalList(playList);
        Log.i("WTF", "---------- displaySongItemList: manager = " + SongPlaybackManager.getSongPlaybackManagerInstance());
    }

    @Override
    public void showProgressBar() {
        retrieveProgressbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        retrieveProgressbar.setVisibility(View.GONE);
    }


    private void updatePlayerView(SongItem currentSongItem, int currentPlaybackPosition) {
        if (currentSongItem.getSong() != null) {
            String songCover = null;
            Cursor coverCursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                    MediaStore.Audio.Albums._ID + " = ?",
                    new String[]{String.valueOf(currentSongItem.getSong().getAlbumId())},
                    null);

            if (coverCursor.moveToFirst()) {
                songCover = coverCursor.getString(coverCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
            }

            if (songCover != null) {
                Glide.with(PlayListActivity.this).load(songCover).apply(new RequestOptions().centerCrop()).into(songBackground);
            } else {
                Glide.with(PlayListActivity.this).load(R.drawable.default_song_image).apply(new RequestOptions().centerCrop()).into(songBackground);
            }

            int minute = currentPlaybackPosition / 60000;
            int second = currentPlaybackPosition % 60000;
            String displayedSongMinute = "";
            String displayedSongSecond = "";

            if (minute < 10) {
                displayedSongMinute = "0" + minute;
            } else {
                displayedSongMinute = "" + minute;
            }

            if (second < 10000) {
                displayedSongSecond = "0" + (second / 1000);
            } else if ((second >= 10000) && (second < 60000)) {
                displayedSongSecond = "" + (second / 1000);
            }



            tvCurrentPosition.setText(displayedSongMinute + ":" + displayedSongSecond);
            songTimeBar.setOnSeekBarChangeListener(this);
            songTimeBar.setMax(((int)currentSongItem.getSong().getDuration()));
            songTimeBar.setProgress(currentPlaybackPosition);
            tvDuration.setText(SongItemRepository.convertDuration(currentSongItem.getSong().getDuration()));
            songName.setText(currentSongItem.getSong().getSongTitle());
            songName.setSelected(true);
            artistName.setText(currentSongItem.getSong().getArtistName());
            artistName.setSelected(true);
        } else {
            songName.setText("???");
            artistName.setText("???");
            Glide.with(PlayListActivity.this).load(R.drawable.default_song_image).apply(new RequestOptions().centerCrop()).into(songBackground);
        }
    }


    /**USER SEEK TIMEBAR**/
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (fromUser) {
            currentSongPosition = seekBar.getProgress();

        }
        minute = currentSongPosition / 60000;
        second = currentSongPosition % 60000;

        if (minute < 10) {
            minuteInString = "0" + minute;
        } else {
            minuteInString = "" + minute;
        }

        if (second < 10000) {
            secondInString = "0" + (second / 1000);
        } else if ((second >= 10000) && (second < 60000)) {
            secondInString = "" + (second / 1000);
        }


        tvCurrentPosition.setText(minuteInString + ":" + secondInString);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        stopSeek();
        SongPlaybackManager.getSongPlaybackManagerInstance().seek(seekBar.getProgress());

    }


    private void doSeekToPosition() {
        counter = new Timer();
        counter.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentSongPosition = SongPlaybackManager.getSongPlaybackManagerInstance().getCurrentPlaybackPosition();
                        songTimeBar.setProgress(currentSongPosition);


                    }
                });
            }
        }, 0, 1000);
    }

    private void stopSeek() {
        counter.cancel();
    }
}
