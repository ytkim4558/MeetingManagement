package com.nagnek.android.meetingmanagement;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by yongtakpc on 2016. 7. 7..
 */
public class Group implements Parcelable {
    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
    public Uri imageUri;
    public String name;
    public ArrayList<Member> memberList;

    Group() {
        this.imageUri = null;
        this.name = null;
        this.memberList = null;
    }

    Group(String name) {
        this.name = name;
    }

    Group(String name, Uri imageUri) {
        this.name = name;
        this.imageUri = imageUri;
    }

    protected Group(Parcel in) {
        String uriString = in.readString();
        if(uriString != null) {
            imageUri = Uri.parse(uriString);
        } else {
            imageUri = null;
        }
        name = in.readString();
        memberList = in.readArrayList(Member.class.getClassLoader());
    }

    void copy(Group group) {
        this.imageUri = group.getImageUri();
        this.name = group.getName();
        this.memberList = group.memberList;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    Uri getImageUri() {
        return imageUri;
    }

    void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    ArrayList<Member> getMemberList() { return memberList;}

    void setMemberList(ArrayList<Member> memberList) {
        this.memberList = memberList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if(imageUri != null) {
            dest.writeString(imageUri.toString());
        } else {
            dest.writeString(null);
        }
        dest.writeString(name);
        dest.writeList(memberList);
    }
}
