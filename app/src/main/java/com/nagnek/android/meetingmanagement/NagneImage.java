package com.nagnek.android.meetingmanagement;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.content.Intent;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by yongtakpc on 2016. 7. 1..
 */

// 참조 : https://docs.google.com/document/d/1fmOqkm4K2yVQp153NVyUtZKz9n9iosqlZDB3CGi0yyE/edit?usp=sharing

public class NagneImage {
    // 최초 호출. 이미지를 가져온다.
    public static Intent getImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }

    protected static String getfilePath(Context context, Uri uri) {
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
        String fName = null;

        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            fName = cursor.getString(columnIndex);
        }
        if (cursor != null) {
            cursor.close();
        }

        return fName;
    }
}
