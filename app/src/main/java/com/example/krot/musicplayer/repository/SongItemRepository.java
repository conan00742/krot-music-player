package com.example.krot.musicplayer.repository;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
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
                    String songId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    String songUri = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    String albumId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                    SongItem mCurrentSongItem = new SongItem(new Song(songId, albumId, songUri, songName, artistName, duration));
                    itemList.add(mCurrentSongItem);

                } while (cursor.moveToNext());
            }

            cursor.close();
        }


        this.itemList = itemList;
        return itemList;
    }



    public static String convertDuration(long duration) {
        String out = null;
        long hours = 0;
        try {
            hours = (duration / 3600000);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return out;
        }
        long remaining_minutes = (duration - (hours * 3600000)) / 60000;
        String minutes = String.valueOf(remaining_minutes);
        if (minutes.equals(0)) {
            minutes = "00";
        }
        long remaining_seconds = (duration - (hours * 3600000) - (remaining_minutes * 60000));
        String seconds = String.valueOf(remaining_seconds);
        if (seconds.length() < 2) {
            seconds = "00";
        } else {
            seconds = seconds.substring(0, 2);
        }

        if (hours > 0) {
            out = hours + ":" + minutes + ":" + seconds;
        } else {
            out = minutes + ":" + seconds;
        }

        return out;

    }


}
