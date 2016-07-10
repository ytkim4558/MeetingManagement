package com.nagnek.android.sharedString;

import android.app.Activity;

import com.nagnek.android.externalIntent.Phone;
import com.nagnek.android.meetingmanagement.Member;
import com.nagnek.android.nagneAndroidUtil.NagneSharedPreferenceUtil;

/**
 * Created by yongtakpc on 2016. 7. 9..
 */
public class Storage {
    static public final String SAVE_MEMBER_INFO_FILE = "SAVE_MEMBER_INFO";

    // 멤버 정보를 SharedPreference에서 저장될 때 키를 멤버의 전화번호로 분류
    public static void saveMemberInfoToSharedPreference(Activity activity, Member member) {
        saveMemberInfoToSharedPreference(activity, Storage.SAVE_MEMBER_INFO_FILE, member);
    }

    // 멤버 정보를 SharedPreference에서 저장될 때 키를 멤버의 전화번호로 분류
    private static void saveMemberInfoToSharedPreference(Activity activity, String fileName, Member member) {
        saveMemberInfoToSharedPreference(activity, fileName, member, 0);
    }

    // 멤버 정보를 SharedPreference에서 저장할 때 키를 멤버의 전화번호로 분류
    private static void saveMemberInfoToSharedPreference(Activity activity, String fileName, Member member, int FileMode) {
        member.phone_number = Phone.getFormatPhoneNumberFormat(member.phone_number);
        NagneSharedPreferenceUtil.removeKey(activity, fileName, member.phone_number);
        NagneSharedPreferenceUtil.appendValue(activity, fileName, member.phone_number, member.name);
        NagneSharedPreferenceUtil.appendValue(activity, fileName, member.phone_number, member.phone_number);
        if (member.imageUri != null) {
            NagneSharedPreferenceUtil.appendValue(activity, fileName, member.phone_number, member.imageUri.toString());
        } else {
            NagneSharedPreferenceUtil.appendValue(activity, fileName, member.phone_number, "");
        }
    }

    public static String[] loadMemberInfoFromSharedPreference(Activity activity, Member member) {
        return loadMemberInfoFromSharedPreference(activity, Storage.SAVE_MEMBER_INFO_FILE, member);
    }

    private static String[] loadMemberInfoFromSharedPreference(Activity activity, String fileName, Member member) {
        member.phone_number = Phone.getFormatPhoneNumberFormat(member.phone_number);
        String[] valueList = NagneSharedPreferenceUtil.getValueList(activity, fileName, member.phone_number);
        return valueList;
    }
}
