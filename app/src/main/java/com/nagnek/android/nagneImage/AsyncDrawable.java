package com.nagnek.android.nagneImage;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.WeakReference;

/**
 * Created by yongtakpc on 2016. 7. 17..
 */
public class AsyncDrawable extends BitmapDrawable{
    private final WeakReference<NagneBitmapWorkerTask> bitmapWorkeTaskReference;

    public AsyncDrawable(Resources res, Bitmap bitmap,
                         NagneBitmapWorkerTask nagneBitmapWorkerTask) {
        super(res, bitmap);
        bitmapWorkeTaskReference =
                new WeakReference<NagneBitmapWorkerTask>(nagneBitmapWorkerTask);
    }

    public NagneBitmapWorkerTask getBitmapWorkerDrawableTask() {
        return bitmapWorkeTaskReference.get();
    }
}
