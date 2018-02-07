package com.example.krot.musicplayer.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Krot on 2/6/18.
 */

public class Song {

    @NonNull
    private final String mSongId;

    @NonNull
    private final String mSongUri;

    @NonNull
    private final String mSongTitle;

    @Nullable
    private final String mArtistName;

    public Song(@NonNull String mSongId, @NonNull String mSongUri, @NonNull String mSongTitle, String mArtistName) {
        this.mSongId = mSongId;
        this.mSongUri = mSongUri;
        this.mSongTitle = mSongTitle;
        this.mArtistName = mArtistName;
    }

    @NonNull
    public String getSongId() {
        return mSongId;
    }

    @NonNull
    public String getSongUri() {
        return mSongUri;
    }

    @NonNull
    public String getSongTitle() {
        return mSongTitle;
    }

    @Nullable
    public String getArtistName() {
        return mArtistName;
    }
}
