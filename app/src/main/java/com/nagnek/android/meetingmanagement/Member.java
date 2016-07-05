package com.nagnek.android.meetingmanagement;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yongtakpc on 2016. 7. 3..
 */
public class Member implements Parcelable {
    Uri imageUri;    // 이미지 주소 (sd카드등의 장소)
    String name;    // 이름
    String phone_number;  // 폰 번호

    Member() {
        imageUri = null;
        name = null;
        phone_number = null;
    }

    public void copy(Member member) {
        if(member != null) {
            imageUri = member.imageUri;
            name = member.name;
            phone_number = member.phone_number;
        } else {
            Dlog.i("member 객체가 널입니다");
        }
    }

    private Member(Parcel in) {
        Uri.Builder builder = new Uri.Builder();

        imageUri = builder.path(in.readString()).build();
        name = in.readString();
        phone_number = in.readString();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(imageUri, flags);
        dest.writeString(name);
        dest.writeString(phone_number);
    }
}
