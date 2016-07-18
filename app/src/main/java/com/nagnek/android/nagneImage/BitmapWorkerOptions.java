package com.nagnek.android.nagneImage;

import android.content.Context;
import android.net.Uri;

import java.util.IllegalFormatCodePointException;

/**
 * Created by yongtakpc on 2016. 7. 18..
 */

public class BitmapWorkerOptions {
    private Context mContext;

    private Uri mImageUri;
    private Integer mResourceId;
    private int mWidth;
    private int mHeight;
    private BitmapShape mShape;

    public static class Builder {
        private Context mContext;

        private Uri mImageUri;
        private Integer mResourceId;
        private int mWidth;
        private int mHeight;
        private BitmapShape mShape;

        public Builder(Context context) {
            mContext = context.getApplicationContext();
            mWidth = 0;
            mHeight = 0;
            mShape = BitmapShape.Rect;
        }

        public BitmapWorkerOptions build() {
            BitmapWorkerOptions options = new BitmapWorkerOptions();

            options.mContext = mContext;
            options.mImageUri = mImageUri;
            options.mResourceId = mResourceId;
            options.mWidth = mWidth;
            options.mHeight = mHeight;
            options.mShape = mShape;

            return options;
        }

        public Builder resource(Uri resourceUri) {
            mImageUri = resourceUri;
            return this;
        }

        public Builder resource(int resourceId) {
            mResourceId = new Integer(resourceId);
            return this;
        }

        public Builder resource(Integer resourceId) {
            mResourceId = resourceId;
            return this;
        }

        public Builder width(int width) {
            if (width > 0) {
                mWidth = width;
            } else {
                throw new IllegalArgumentException("Can't set width to " + width);
            }
            return this;
        }

        public Builder shape(BitmapShape shape) {
            mShape = shape;
            return this;
        }

        public Builder height(int height) {
            if (height > 0) {
                mHeight = height;
            } else {
                throw new IllegalArgumentException("Can't set height to " + height);
            }
            return this;
        }
    }

    /**
     * Private constructor.
     * <p>
     * Use a {@link Builder} to create.
     */
    private BitmapWorkerOptions() {
    }

    public Uri getImageUri() {
        return mImageUri;
    }

    public Integer getResourceId() {
        return mResourceId;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public Context getContext() {
        return mContext;
    }

    public BitmapShape getShape() {
        return mShape;
    }

    public boolean isFromResource() {
        return getResourceId() != null;
    }

    public boolean isFromImageUri() {
        return getImageUri() != null;
    }

    public boolean isRequireCircleBitmap() {    // circle 형태의 비트맵을 요구하는지
        return getShape() == BitmapShape.Circle;
    }
}
