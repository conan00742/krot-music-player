package com.example.krot.musicplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;


import com.example.krot.musicplayer.SongPlaybackManager;
import com.example.krot.musicplayer.service.SongPlaybackService;

import static com.example.krot.musicplayer.AppConstantTag.ACTION_PLAYBACK;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_PLAY_NEXT_SONG;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_PLAY_PREVIOUS_SONG;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_SAVE_CURRENT_PLAYLIST;
import static com.example.krot.musicplayer.AppConstantTag.ACTION_UPDATE_UI;

/**
 * Created by Krot on 3/19/18.
 */

public class PlaybackReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String playerAction = intent.getAction();
            if (playerAction != null) {
                //ACTION_PLAYBACK
                if (TextUtils.equals(ACTION_PLAYBACK, playerAction)) {
                    Log.i("KROTKROTKROT", "ACTION_PLAYBACK: isPlaying = " + SongPlaybackManager.getSongPlaybackManagerInstance().isPlaying());
                    if (SongPlaybackManager.getSongPlaybackManagerInstance().isPlaying()) {
                        SongPlaybackManager.getSongPlaybackManagerInstance().pause();
                    } else {
                        SongPlaybackManager.getSongPlaybackManagerInstance().play();
                    }
                }

                //ACTION_NEXT
                else if (TextUtils.equals(ACTION_PLAY_NEXT_SONG, playerAction)) {
                    SongPlaybackManager.getSongPlaybackManagerInstance().next();
                }

                //ACTION_PREVIOUS
                else if (TextUtils.equals(ACTION_PLAY_PREVIOUS_SONG, playerAction)) {
                    SongPlaybackManager.getSongPlaybackManagerInstance().previous();
                }

                else if (TextUtils.equals(ACTION_SAVE_CURRENT_PLAYLIST, playerAction)) {
                    Log.i("WTF", "ACTION_SAVE_CURRENT_PLAYLIST");
                    SongPlaybackManager.getSongPlaybackManagerInstance().saveLastPlayedSong();
                }

            }
        }
    }


}
