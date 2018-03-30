package com.example.krot.musicplayer.playlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityOptionsCompat;
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
import com.example.krot.musicplayer.Helper;
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
import com.example.krot.musicplayer.home.HomeActivity;
import com.example.krot.musicplayer.model.Item;
import com.example.krot.musicplayer.model.SongItem;
import com.example.krot.musicplayer.presenter.PickSongContract;
import com.example.krot.musicplayer.presenter.song.PickSongPresenterImpl;
import com.example.krot.musicplayer.queue.QueuePlayListActivity;
import com.example.krot.musicplayer.receiver.PlaybackReceiver;
import com.example.krot.musicplayer.repository.SongItemRepository;
import com.example.krot.musicplayer.service.SongPlaybackService;

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

public class PlayListActivity extends AppCompatActivity implements PickSongContract.PickSongView, SeekBar.OnSeekBarChangeListener {

    private static final String PREFIX = PlayListActivity.class.getSimpleName();

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
    @BindView(R.id.icon_minimize)
    ImageView icMinimize;

    @OnClick(R.id.icon_minimize)
    public void backToHome() {
        SongPlaybackManager.getSongPlaybackManagerInstance().saveLastPlayedSong();
        Intent showCurrentPlayingSongIntent = new Intent(PlayListActivity.this, HomeActivity.class);
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(PlayListActivity.this, songBackground, getResources().getString(R.string.shared_song_cover));
        startActivity(showCurrentPlayingSongIntent, optionsCompat.toBundle());
    }


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
    private int previousPlaybackSongPosition;
    private int selectionCount = 0;
    private PlaybackReceiver playbackReceiver;
    private SongItemAdapter songItemAdapter;
    private PickSongContract.PickSongPresenter pickSongPresenter;

    @OnClick(R.id.bottom_sheet_playlist)
    public void showPlaylist() {
        //scroll to position with offset 0
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) musicRecyclerView.getLayoutManager();
        linearLayoutManager.scrollToPositionWithOffset(previousPlaybackSongPosition, 0);
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback_song);
        ButterKnife.bind(this);

        songTimeBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.orange)));
        lastSongPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        behavior = BottomSheetBehavior.from(bottomSheetPlayList);
        pickSongPresenter = new PickSongPresenterImpl(this);
        setupAdapter();


    }

    @Override
    protected void onStart() {
        super.onStart();
        disposable = RxBus.getInstance().toObserverable().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                //Khi user click vào 1 bài nhạc trong list
                if (o instanceof EventPlaySong) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    //Kiểm tra xem có shuffle hay ko
                    EventPlaySong eventPlaySong = (EventPlaySong) o;
                    int currentSongIndex = eventPlaySong.getPosition() - 1;

                    //EVENT_PICK_A_SONG
                    if (SongPlaybackManager.getSongPlaybackManagerInstance().isShuffleOn()) {
                        List<Item> defaultPlayList = SongPlaybackManager.getSongPlaybackManagerInstance().getOriginalList();
                        SongPlaybackManager.getSongPlaybackManagerInstance().setCurrentList(defaultPlayList);
                        SongPlaybackManager.getSongPlaybackManagerInstance().setPlayerShuffleOff();
                    }


                    SongPlaybackManager.getSongPlaybackManagerInstance().setLastPlayedSongIndex(currentSongIndex);

                    if (SongPlaybackManager.getSongPlaybackManagerInstance().isPlaying()) {
                        SongPlaybackManager.getSongPlaybackManagerInstance().pause();
                        SongPlaybackManager.getSongPlaybackManagerInstance().prepareSource(currentSongIndex);
                    } else {
                        SongPlaybackManager.getSongPlaybackManagerInstance().prepareSource(currentSongIndex);
                    }

                    SongPlaybackManager.getSongPlaybackManagerInstance().play();

                }

                //khi user chọn shuffle all
                else if (o instanceof EventShuffleAllSongs) {
                    //EVENT_SHUFFLE_ALL_SONGS
                    // 1. Re-shuffle the original list
                    List<Item> originalList = SongPlaybackManager.getSongPlaybackManagerInstance().getOriginalList();

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
                    selectionCount += 1;
                    updatePlayerView(eventUpdatePlayerUI.getCurrentSongItem(), eventUpdatePlayerUI.getCurrentPlaybackPosition(), eventUpdatePlayerUI.getCurrentSongIndex());
                } else if (o instanceof EventIsPlaying) {
                    icPlayback.setImageDrawable(ContextCompat.getDrawable(PlayListActivity.this, R.drawable.ic_pause));
                    doSeekToPosition();
                    //start service
                    if (!Helper.isServiceStarted(SongPlaybackService.class)) {
                        Intent playSongServiceIntent = new Intent(PlayListActivity.this, SongPlaybackService.class);
                        playSongServiceIntent.setAction(ACTION_CREATE_NOTIFICATION);
                        startService(playSongServiceIntent);
                    }
                } else if (o instanceof EventIsPaused) {
                    icPlayback.setImageDrawable(ContextCompat.getDrawable(PlayListActivity.this, R.drawable.ic_play));
                    if (counter != null) {
                        stopSeek();
                    }
                } else if (o instanceof EventEndSong) {
                    stopSeek();
                } else if (o instanceof EventShuffleOn) {
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

        SongPlaybackManager.getSongPlaybackManagerInstance().initUI();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposable.dispose();
        SongPlaybackManager.getSongPlaybackManagerInstance().saveLastPlayedSong();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }

    }


    private void setupAdapter() {
        songItemAdapter = new SongItemAdapter(this);
        songItemAdapter.setItemList(SongPlaybackManager.getSongPlaybackManagerInstance().getOriginalList());

        musicRecyclerView.setAdapter(songItemAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        musicRecyclerView.setLayoutManager(manager);
        musicRecyclerView.setItemAnimator(new DefaultItemAnimator());
        Drawable musicDividerDecoration = ContextCompat.getDrawable(this, R.drawable.music_divider_decoration);
        musicRecyclerView.addItemDecoration(new DividerItemDecoration(musicDividerDecoration));
    }



    @Override
    public void updatePlayListUI(List<Item> newList, int index) {

        if (selectionCount > 0) {
            if (previousPlaybackSongPosition == 0) {
                previousPlaybackSongPosition += 1;
            }
            SongItem previousItem = (SongItem) songItemAdapter.getItemAt(previousPlaybackSongPosition);
            previousItem.setSelected(false);

            newList.remove(previousPlaybackSongPosition);
            newList.add(previousItem);
        }

        previousPlaybackSongPosition = index + 1;

        SongItem newItem = (SongItem) songItemAdapter.getItemAt(previousPlaybackSongPosition);
        newItem.setSelected(true);
        newList.remove(previousPlaybackSongPosition);
        newList.add(newItem);

        songItemAdapter.updateListItem(newList);


    }


    //TODO: chỗ này gây lag
    private void updatePlayerView(SongItem currentSongItem, int currentPlaybackPosition, int index) {
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


            songTimeBar.setMax(((int) currentSongItem.getSong().getDuration()));
            songTimeBar.setProgress(currentPlaybackPosition);
            tvDuration.setText(SongItemRepository.convertDuration(currentSongItem.getSong().getDuration()));
            songName.setText(currentSongItem.getSong().getSongTitle());
            songName.setSelected(true);
            artistName.setText(currentSongItem.getSong().getArtistName());
            artistName.setSelected(true);



            //TODO: lag quá
            List<Item> oldList = songItemAdapter.getCurrentItemList();
            Log.i("QUEENOFPAIN", "oldList = " + oldList + " - index = " + index);
            pickSongPresenter.updatePlayList(oldList, index);

        } else {
            songName.setText("???");
            artistName.setText("???");
            Glide.with(PlayListActivity.this).load(R.drawable.default_song_image).apply(new RequestOptions().centerCrop()).into(songBackground);
        }
    }


    /**
     * USER SEEK TIMEBAR
     **/
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
        }, 0, 500);
    }


    private void stopSeek() {
        counter.cancel();
    }


}
