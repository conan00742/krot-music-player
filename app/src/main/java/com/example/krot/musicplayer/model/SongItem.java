package com.example.krot.musicplayer.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Krot on 2/6/18.
 */

public class SongItem implements Item, Parcelable{

    @Nullable
    private Song mSong;

    public SongItem(Song mSong) {
        this.mSong = mSong;
    }

    protected SongItem(Parcel in) {
        mSong = in.readParcelable(Song.class.getClassLoader());
    }

    public static final Creator<SongItem> CREATOR = new Creator<SongItem>() {
        @Override
        public SongItem createFromParcel(Parcel in) {
            return new SongItem(in);
        }

        @Override
        public SongItem[] newArray(int size) {
            return new SongItem[size];
        }
    };

    @Nullable
    public Song getSong() {
        return mSong;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SongItem) {
            SongItem mCurrentSongItem = (SongItem) obj;
            return (this.getSong().getSongId().equals(mCurrentSongItem.getSong().getSongId()));
        } else {
            return false;
        }
    }

    @Override
    public boolean sameContent(Item currentItem) {
        SongItem mCurrentSongItem = (SongItem) currentItem;
        return (    this.getSong().getAlbumId().equals(mCurrentSongItem.getSong().getAlbumId())
                &&  this.getSong().getSongUri().equals(mCurrentSongItem.getSong().getSongUri())
                &&  this.getSong().getSongTitle().equals(mCurrentSongItem.getSong().getSongId())
                &&  this.getSong().getArtistName().equals(mCurrentSongItem.getSong().getArtistName())
                &&  this.getSong().getDuration() == mCurrentSongItem.getSong().getDuration());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mSong, flags);
    }
}
