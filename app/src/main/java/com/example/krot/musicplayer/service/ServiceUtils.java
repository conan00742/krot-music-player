package com.example.krot.musicplayer.service;

import android.app.ActivityManager;
import android.content.Context;

import com.example.krot.musicplayer.MusicPlayerApp;

import java.util.List;

/**
 * Created by Krot on 3/14/18.
 */

public class ServiceUtils {


    public static boolean isServiceStarted(Class<?> serviceClass) {
//        final ActivityManager activityManager = (ActivityManager) MusicPlayerApp.getAppContext()
//                .getSystemService(Context.ACTIVITY_SERVICE);
//
//        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
//
//        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
//            if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
//                return true;
//            }
//        }
//        return false;

        ActivityManager manager = (ActivityManager) MusicPlayerApp.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
