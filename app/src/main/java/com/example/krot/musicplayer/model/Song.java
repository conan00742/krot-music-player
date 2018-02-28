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
    private final String mAlbumId;

    @NonNull
    private final String mSongUri;

    @NonNull
    private final String mSongTitle;

    @Nullable
    private final String mArtistName;


    private final long mDuration;

    public Song(@NonNull String mSongId, @NonNull String mAlbumId , @NonNull String mSongUri, @NonNull String mSongTitle, @Nullable String mArtistName, long mDuration) {
        this.mSongId = mSongId;
        this.mAlbumId = mAlbumId;
        this.mSongUri = mSongUri;
        this.mSongTitle = mSongTitle;
        this.mArtistName = mArtistName;
        this.mDuration = mDuration;
    }

    @NonNull
    public String getSongId() {
        return mSongId;
    }

    @NonNull
    public String getAlbumId() {
        return mAlbumId;
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


    public long getDuration() {
        return mDuration;
    }
}
