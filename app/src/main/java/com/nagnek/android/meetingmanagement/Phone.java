package com.nagnek.android.meetingmanagement;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by yongtakpc on 2016. 7. 3..
 */
public class Phone {
    // 전화걸기
    public void call(Context context, String number) {
        if (number != null) {
            context.startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + number)));
        }
    }
}
