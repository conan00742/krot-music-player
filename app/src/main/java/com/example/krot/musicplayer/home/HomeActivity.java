package com.example.krot.musicplayer.home;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.krot.musicplayer.R;
import com.example.krot.musicplayer.SongPlaybackManager;
import com.example.krot.musicplayer.event_bus.EventIsPaused;
import com.example.krot.musicplayer.event_bus.EventIsPlaying;
import com.example.krot.musicplayer.event_bus.EventUpdateMiniPlaybackUI;
import com.example.krot.musicplayer.event_bus.RxBus;
import com.example.krot.musicplayer.model.Item;
import com.example.krot.musicplayer.model.SongItem;
import com.example.krot.musicplayer.playlist.PlayListActivity;
import com.example.krot.musicplayer.presenter.SongItemContract;
import com.example.krot.musicplayer.presenter.song.SongItemPresenterImpl;
import com.example.krot.musicplayer.service.ServiceUtils;
import com.example.krot.musicplayer.service.SongPlaybackService;
import com.google.gson.Gson;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

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

public class HomeActivity extends AppCompatActivity implements SongItemContract.SongItemView {

    @BindView(R.id.toolbar_home)
    Toolbar toolbarHome;

    @BindView(R.id.loading_progress)
    ProgressBar loadingProgress;

    @BindView(R.id.mini_song_cover)
    ImageView miniSongCover;

    @BindView(R.id.mini_song_title)
    TextView miniSongTitle;

    @BindView(R.id.mini_song_artist)
    TextView miniSongArtist;

    @BindView(R.id.ic_mini_previous)
    ImageView icMiniPrevious;

    @OnClick(R.id.ic_mini_previous)
    public void playPreviousSong() {
        SongPlaybackManager.getSongPlaybackManagerInstance().previous();
    }

    @BindView(R.id.ic_mini_playback)
    ImageView icMiniPlayback;

    @OnClick(R.id.ic_mini_playback)
    public void doSongPlayback() {
        sendBroadcast(new Intent(ACTION_PLAYBACK));
    }

    @BindView(R.id.ic_mini_next)
    ImageView icMiniNext;

    @OnClick(R.id.ic_mini_next)
    public void playNextSong() {
        SongPlaybackManager.getSongPlaybackManagerInstance().next();
    }

    private SongItemContract.SongItemPresenter songItemPresenter;
    private SharedPreferences defaultPreferences;
    private Disposable disposable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        defaultPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        setSupportActionBar(toolbarHome);
        getSupportActionBar().setTitle("Music");
        setUpDrawer();
        songItemPresenter = new SongItemPresenterImpl(this);


        //request permission
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasPermissions(this, permissions)) {
            Log.i("WTF", ">>>>>>>>>>>>>BEFORE: NOT GRANTED");
            displayDefaultUI();
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_CODE);
        } else {
            Log.i("WTF", ">>>>>>>>>>>>>BEFORE: GRANTED");
            songItemPresenter.loadData();
        }

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.i("WTF", ">>>>>>>>>>>>>AFTER: GRANTED");
                        songItemPresenter.loadData();
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        boolean shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE);
                        if (shouldShowRationale) {
                            //show the reason why user must grant STORAGE permission
                            //show dialog
                            new AlertDialog.Builder(this).setTitle("Permission Denied").setMessage(R.string.permission_rationale).setPositiveButton("RE-TRY", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
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


    @Override
    protected void onStart() {
        super.onStart();
        disposable = RxBus.getInstance().toObserverable().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (o instanceof EventUpdateMiniPlaybackUI) {
                    EventUpdateMiniPlaybackUI eventUpdateMiniPlaybackUI = (EventUpdateMiniPlaybackUI) o;
                    updateMiniPlaybackControlUI(eventUpdateMiniPlaybackUI.getCurrentSongItem());
                }

                else if (o instanceof EventIsPlaying) {
                    icMiniPlayback.setImageDrawable(ContextCompat.getDrawable(HomeActivity.this, R.drawable.ic_mini_pause));
                    if (!ServiceUtils.isServiceStarted(SongPlaybackService.class)) {
                        Intent playSongServiceIntent = new Intent(HomeActivity.this, SongPlaybackService.class);
                        playSongServiceIntent.setAction(ACTION_CREATE_NOTIFICATION);
                        startService(playSongServiceIntent);
                    }
                }

                else if (o instanceof EventIsPaused) {
                    icMiniPlayback.setImageDrawable(ContextCompat.getDrawable(HomeActivity.this, R.drawable.ic_mini_play));
                }
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor countEditor = defaultPreferences.edit();
        countEditor.putBoolean(FIRST_TIME_INSTALL, false);
        countEditor.apply();
        sendBroadcast(new Intent(ACTION_SAVE_CURRENT_PLAYLIST));
        disposable.dispose();

    }

    private void setUpDrawer() {
        SecondaryDrawerItem itemHome = new SecondaryDrawerItem().withIdentifier(1).withName(R.string.drawer_item_home).withIcon(R.drawable.ic_home);

        SecondaryDrawerItem itemPlaylistQueue = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.drawer_item_play_queue).withIcon(R.drawable.ic_queue);

        SecondaryDrawerItem itemPlaylists = new SecondaryDrawerItem().withIdentifier(3).withName(R.string.drawer_item_playlists).withIcon(R.drawable.ic_playlist);

        SecondaryDrawerItem itemArtists = new SecondaryDrawerItem().withIdentifier(4).withName(R.string.drawer_item_artists).withIcon(R.drawable.ic_artist);

        SecondaryDrawerItem itemAlbums = new SecondaryDrawerItem().withIdentifier(5).withName(R.string.drawer_item_albums).withIcon(R.drawable.ic_album);

        SecondaryDrawerItem itemSongs = new SecondaryDrawerItem().withIdentifier(6).withName(R.string.drawer_item_songs).withIcon(R.drawable.ic_song);

        Drawer mainDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbarHome)
                .addDrawerItems(itemHome,
                        itemPlaylistQueue,
                        itemPlaylists,
                        itemArtists,
                        itemAlbums,
                        itemSongs)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        return true;
                    }
                })
                .build();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mainDrawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home_drawer, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.ic_menu_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        return true;
    }


    private void displayDefaultUI() {
        miniSongTitle.setText("???");
        miniSongArtist.setText("???");
        Glide.with(HomeActivity.this).load(R.drawable.default_song_image).apply(new RequestOptions().centerCrop()).into(miniSongCover);
        icMiniPlayback.setImageDrawable(ContextCompat.getDrawable(HomeActivity.this, R.drawable.ic_mini_play));
    }


    private void updateMiniPlaybackControlUI(SongItem currentSongItem) {
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
                Glide.with(HomeActivity.this).load(songCover).apply(new RequestOptions().centerCrop()).into(miniSongCover);
            } else {
                Glide.with(HomeActivity.this).load(R.drawable.default_song_image).apply(new RequestOptions().centerCrop()).into(miniSongCover);
            }

            miniSongTitle.setText(currentSongItem.getSong().getSongTitle());
            miniSongTitle.setSelected(true);
            miniSongArtist.setText(currentSongItem.getSong().getArtistName());
            miniSongArtist.setSelected(true);
        } else {
            miniSongTitle.setText("???");
            miniSongArtist.setText("???");
            Glide.with(HomeActivity.this).load(R.drawable.default_song_image).apply(new RequestOptions().centerCrop()).into(miniSongCover);
        }
    }



    @Override
    public void showProgressBar() {
        loadingProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        loadingProgress.setVisibility(View.GONE);
    }

    @Override
    public void setOriginalList(List<Item> originalList) {
        List<SongItem> originalSongItemList = new ArrayList<>();
        for (int i = 0; i < originalList.size(); i++) {
            Item currentItem = originalList.get(i);
            if (currentItem instanceof SongItem) {
                SongItem currentSongItem = (SongItem) currentItem;
                originalSongItemList.add(currentSongItem);
            }
        }

        //store the original list to sharedpreferences
        Gson gson = new Gson();
        String originalPlayListInString = gson.toJson(originalSongItemList);
        SharedPreferences.Editor editor = defaultPreferences.edit();
        editor.putString(ORIGINAL_PLAYLIST, originalPlayListInString);
        editor.apply();

        SongPlaybackManager.getSongPlaybackManagerInstance().setOriginalList(originalSongItemList);
    }

}
