package com.example.krot.musicplayer;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Krot on 3/27/18.
 */

public class Helper {

    //check if service is running or not
    public static boolean isServiceStarted(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) MusicPlayerApp.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    //check if application is running or not
    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null)
        {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
