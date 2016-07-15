package com.nagnek.android.nagneImage;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;


/**
 * Created by yongtakpc on 2016. 7. 15..
 */
class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
    Activity activity;
    private final WeakReference<ImageView> imageViewReference;
    private int data = 0;

    public BitmapWorkerTask(Activity activity, ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        this.activity = activity;
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Integer... params) {
        data = params[0];
        return NagneImage.decodeSampledBitmapFromResource(activity.getResources(), data, 100, 100);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
