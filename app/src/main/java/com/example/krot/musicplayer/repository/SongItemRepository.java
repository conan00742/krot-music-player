package com.example.krot.musicplayer.repository;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.krot.musicplayer.model.Item;
import com.example.krot.musicplayer.model.Song;
import com.example.krot.musicplayer.model.SongItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Krot on 2/7/18.
 */

public class SongItemRepository {

    @NonNull
    private Context mContext;

    @Nullable
    private List<Item> itemList = new ArrayList<>();

    public SongItemRepository(@NonNull Context mContext) {
        this.mContext = mContext;
    }

    @Nullable
    public List<Item> getItemList() {
        return itemList;
    }

    public List<Item> retrieveSongListFromExternalStorage() {
        List<Item> itemList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selectionQuery = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        Cursor cursor = mContext.getContentResolver().query(uri, null, selectionQuery, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String songId = UUID.randomUUID().toString();
                    String songUri = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
//                    Log.i("WTF", "" + songId + " - " + songUri + " - " + songName + " - " + artistName);

                    SongItem mCurrentSongItem = new SongItem(new Song(songId, songUri, songName, artistName));
                    itemList.add(mCurrentSongItem);
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

//        for (int i = 0; i < itemList.size(); i++) {
//            Item mCurrentItem = itemList.get(i);
//            if (mCurrentItem instanceof SongItem) {
//                SongItem mCurrentSongItem = (SongItem) mCurrentItem;
//                Log.i("WTF", "songName = " + mCurrentSongItem.getSong().getSongTitle());
//            }
//        }
        this.itemList = itemList;
        return itemList;
    }


}
