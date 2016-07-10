package com.nagnek.android.externalIntent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;

/**
 * Created by yongtakpc on 2016. 7. 3..
 */
public class Phone {
    static public String getFormatPhoneNumberFormat(String unFormattedPhoneNumber) {
        return PhoneNumberUtils.formatNumber(unFormattedPhoneNumber);
    }

    // 전화걸기
    public void call(Context context, String number) {
        if (number != null) {
            context.startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + number)));
        }
    }
}
