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
public class GalleryImage {
    // 최초 호출. 이미지를 가져온다.
    public static Intent getImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }

    // 갤러리에서 이미지가 돌아오면 호출. 이미지를 자른다.
    public static Intent getCropImageIntent(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("noFaceDetection", true);
        return intent;
    }

    // intent에서 CROP된 이미지를 가져온다.
    public static Bitmap getImageFromIntent(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            String fPath = getfilePath(context, uri);

            context.getContentResolver().delete(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    MediaStore.MediaColumns.DATA + "='" + fPath + "'",
                    null);

            return bmp;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private static String getfilePath(Context context, Uri uri) {
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
