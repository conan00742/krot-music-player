package com.example.krot.musicplayer.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Krot on 2/6/18.
 */

public class Song implements Parcelable{

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

    protected Song(Parcel in) {
        mSongId = in.readString();
        mAlbumId = in.readString();
        mSongUri = in.readString();
        mSongTitle = in.readString();
        mArtistName = in.readString();
        mDuration = in.readLong();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mSongId);
        dest.writeString(this.mAlbumId);
        dest.writeString(this.mSongUri);
        dest.writeString(this.mSongTitle);
        dest.writeString(this.mArtistName);
        dest.writeLong(this.mDuration);
    }
}
