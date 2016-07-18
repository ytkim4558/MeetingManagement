package com.nagnek.android.nagneImage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.nagnek.android.debugLog.Dlog;

import java.lang.ref.WeakReference;

import static com.nagnek.android.nagneImage.NagneImage.decodeSampledBitmapFromResource;
import static com.nagnek.android.nagneImage.NagneImage.decodeSampledBitmapFromUri;

/**
 * Created by yongtakpc on 2016. 7. 16..
 */
public class BitmapWorkerTask extends AsyncTask<BitmapWorkerOptions, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;
    public BitmapWorkerOptions data = null;
    private float reqWidth = 0;
    private float reqHeight = 0;
    private Context context;

    public BitmapWorkerTask(Context context, ImageView imageView, final float reqWidth, final float reqHeight) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);

        // 컨텍스트의 경우 getApplicationContext()로 인자값을 넘겨준다.
        this.context = context.getApplicationContext();
        this.reqWidth = reqWidth;
        this.reqHeight = reqHeight;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(BitmapWorkerOptions... params) {
        return retrieveBitmap(params[0]);
    }

    protected Bitmap retrieveBitmap(BitmapWorkerOptions workerOptions) {
        data = workerOptions;
        if (workerOptions.isFromResource()) {
            return decodeSampledBitmapFromResource(context.getResources(), workerOptions.getResourceId(), reqWidth, reqHeight);
        } else if (workerOptions.isFromImageUri()) {
            return decodeSampledBitmapFromUri(context, workerOptions.getImageUri(), reqWidth, reqHeight);
        } else {
            Dlog.e("Error loading bitmap - no source!");
        }
        return null;
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

            if (this == bitmapWorkerTask && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }

        context = null;
    }

    public static boolean cancelPotentialWork(BitmapWorkerOptions newData, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final BitmapWorkerOptions prevData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (prevData == null || prevData != newData) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerDrawableTask();
            }
        }
        return null;
    }
}
