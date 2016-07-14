package com.nagnek.android.nagneImage;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.net.Uri;

/**
 * Created by yongtakpc on 2016. 7. 1..
 * http://stackoverflow.com/questions/11932805/cropping-circular-area-from-bitmap-in-android
 */
public class NagneCircleImage extends NagneImage {
    // 서클 이미지로 잘라낸다
    public static Bitmap getCircleBitmap(Bitmap bm) {

        int sice = Math.min((bm.getWidth()), (bm.getHeight()));

        Bitmap bitmap = ThumbnailUtils.extractThumbnail(bm, sice, sice);

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final int color = 0xffff0000;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) 4);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap getCircleBitmap(Context context, Uri imageUri) {
        Bitmap bitmap = getBitmap(context, imageUri);
        Bitmap circleBitmap = getCircleBitmap(bitmap);
        bitmap = null;
        return circleBitmap;
    }

    public static Bitmap getCircleBitmap(Resources res, int resId,
                                         float reqWidth, float reqHeight) {
        Bitmap bitmap = decodeSampledBitmapFromResource(res, resId, reqWidth, reqHeight);
        Bitmap circleBitmap = getCircleBitmap(bitmap);
        bitmap = null;
        return circleBitmap;
    }

    public static Bitmap getCircleBitmap(Context context, Uri imageUri,
                                         float reqWidth, float reqHeight) {
        Bitmap bitmap = decodeSampledBitmapFromUri(context, imageUri, reqWidth, reqHeight);
        Bitmap circleBitmap = getCircleBitmap(bitmap);
        bitmap = null;
        return circleBitmap;
    }
}
