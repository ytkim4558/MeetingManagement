package com.nagnek.android.externalIntent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by yongtakpc on 2016. 7. 8..
 */
public class NagneSMS {
    public void sendSMS(Context context, String phoneNumber, String message) {
        Uri uri = Uri.parse("smsto:" + phoneNumber);
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", message);
        context.startActivity(it);
    }
}
