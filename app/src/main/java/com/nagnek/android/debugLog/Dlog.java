package com.nagnek.android.debugLog;

import android.util.Log;

/**
 * Created by yongtakpc on 2016. 7. 1..
 */
public class Dlog {
    public static final boolean showToast = false;
    static final String TAG = "meetingmanagement";

    /**
     * Log Level Error
     **/
    public static final void e(String message) {
        if (BaseApplication.DEBUG) Log.e(TAG, buildLogMsg(message));
    }

    /**
     * Log Level Warning
     **/
    public static final void w(String message) {
        if (BaseApplication.DEBUG) Log.w(TAG, buildLogMsg(message));
    }

    /**
     * Log Level Information
     **/
    public static final void i(String message) {
        if (BaseApplication.DEBUG) Log.i(TAG, buildLogMsg(message));
    }

    /**
     * Log Level Debug
     **/
    public static final void d(String message) {
        if (BaseApplication.DEBUG) Log.d(TAG, buildLogMsg(message));
    }

    /**
     * Log Level Verbose
     **/
    public static final void v(String message) {
        if (BaseApplication.DEBUG) Log.v(TAG, buildLogMsg(message));
    }

    public static String s(String input) {
        if (BaseApplication.DEBUG) {
            String string = buildLogMsg(input);
            return string;
        }
        return null;
    }


    public static String buildLogMsg(String message) {

        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];

        StringBuilder sb = new StringBuilder();

        sb.append("[");
        sb.append(ste.getFileName().replace(".java", ""));
        sb.append("::");
        sb.append(ste.getMethodName());
        sb.append("]");
        sb.append(message);
        return sb.toString();
    }
}
