package com.nagnek.android.nagneImage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by yongtakpc on 2016. 7. 1..
 */
public class NagneCropImage extends NagneImage {
    // 갤러리에서 이미지가 돌아오면 호출. 이미지를 자른다.
    public static Intent getCropImageIntent(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
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
}
