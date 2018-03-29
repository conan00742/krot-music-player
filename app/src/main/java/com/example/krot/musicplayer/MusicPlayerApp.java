package com.example.krot.musicplayer;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Krot on 3/14/18.
 */

public class MusicPlayerApp extends Application implements Application.ActivityLifecycleCallbacks{

    private static MusicPlayerApp appContext;

    public static MusicPlayerApp getAppContext() {
        return appContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.i("LAZY", "activity = " + activity + " - onActivityCreated - isFinishing = " + activity.isFinishing());
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.i("LAZY", "activity = " + activity + " - onActivityStarted - isFinishing = " + activity.isFinishing());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.i("LAZY", "activity = " + activity + " - onActivityResumed - isFinishing = " + activity.isFinishing());
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.i("LAZY", "activity = " + activity + " - onActivityPaused - isFinishing = " + activity.isFinishing());
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.i("LAZY", "activity = " + activity + " - onActivityStopped - isFinishing = " + activity.isFinishing());
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.i("LAZY", "activity = " + activity + " - onActivitySaveInstanceState - isFinishing = " + activity.isFinishing());
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.i("LAZY", "activity = " + activity + " - onActivityDestroyed - isFinishing = " + activity.isFinishing());
    }
}
