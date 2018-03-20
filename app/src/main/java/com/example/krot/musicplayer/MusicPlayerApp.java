package com.example.krot.musicplayer;

import android.app.Application;

/**
 * Created by Krot on 3/14/18.
 */

public class MusicPlayerApp extends Application {

    private static MusicPlayerApp appContext;

    public static MusicPlayerApp getAppContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
    }
}
