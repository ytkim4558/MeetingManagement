package com.nagnek.android.meetingmanagement;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * Created by yongtakpc on 2016. 7. 1..
 */
public class BaseApplication extends Application {
    public static boolean DEBUG = false;
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        this.DEBUG = isDebuggable(this);
        this.context = getApplicationContext();
    }
    private void getContext() {
        context = getApplicationContext();
    }

    /**
     * get Debug Mode
     *
     * @param context
     * @return
     */
    private boolean isDebuggable(Context context) {
        boolean debuggable = false;

        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo appinfo = pm.getApplicationInfo(context.getPackageName(), 0);
            debuggable = (0 != (appinfo.flags & ApplicationInfo.FLAG_DEBUGGABLE));
        } catch (PackageManager.NameNotFoundException e) {
            /* debuggable variable will remain false */
        }

        return debuggable;
    }

}