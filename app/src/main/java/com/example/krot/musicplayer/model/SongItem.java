package com.example.krot.musicplayer.model;

import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Krot on 2/6/18.
 */

public class SongItem implements Item {

    @Nullable
    private Song mSong;

    public SongItem() {
    }

    public SongItem(Song mSong) {
        this.mSong = mSong;
    }

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
}
