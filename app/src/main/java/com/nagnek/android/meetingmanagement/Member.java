package com.nagnek.android.meetingmanagement;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.nagnek.android.debugLog.Dlog;

/**
 * Created by yongtakpc on 2016. 7. 3..
 */

// Member의 객체를 바꿀 때 saveMemberInfoToSharedPreference에서 저장하는 필드나 순서등을 변경해야 함.
public class Member implements Parcelable {
    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel in) {
            return new Member(in);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };
    public Uri imageUri;    // 이미지 주소 (sd카드등의 장소)
    public String name;    // 이름
    public String phone_number;  // 폰 번호

    Member() {
        imageUri = null;
        name = null;
        phone_number = null;
    }

    private Member(Parcel in) {
        String uriString = in.readString();
        if (uriString != null) {
            imageUri = Uri.parse(uriString);
        } else {
            imageUri = null;
        }
        name = in.readString();
        phone_number = in.readString();
    }

    public void copy(Member member) {
        if (member != null) {
            imageUri = member.imageUri;
            name = member.name;
            phone_number = member.phone_number;
        } else {
            Dlog.i("member 객체가 널입니다");
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (imageUri != null) {
            dest.writeString(imageUri.toString());
        } else {
            dest.writeString(null);
        }
        dest.writeString(name);
        dest.writeString(phone_number);
    }
}
