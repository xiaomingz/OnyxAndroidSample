package com.onyx.android.sdk.data;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;

import com.onyx.android.sdk.api.ReaderImage;
import com.onyx.android.sdk.utils.BitmapUtils;

/**
 * Created by zhuzeng on 10/4/15.
 */
public class ReaderImageImpl implements ReaderImage {

    private RectF rectangle;
    private int pos;
    private Bitmap bitmap;
    private float gammaCorrection;

    public static ReaderImageImpl create(int width, int height, Bitmap.Config config) {
        ReaderImageImpl readerBitmap = new ReaderImageImpl(width, height, config);
        return readerBitmap;
    }

    public ReaderImageImpl() {
        super();
    }

    public ReaderImageImpl(int width, int height, Bitmap.Config config) {
        super();
        bitmap = Bitmap.createBitmap(width, height, config);
    }

    public void clear() {
        if (bitmap != null) {
            bitmap.eraseColor(Color.WHITE);
        }
    }

    public void recycleBitmap() {
        if (bitmap != null) {
            bitmap.recycle();
        }
        bitmap = null;
    }

    @Override
    public RectF getRectangle() {
        return rectangle;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public int getPos() {
        return pos;
    }

    public void setGammaCorrection(float correction) {
        this.gammaCorrection = correction;
    }

    public float gammaCorrection() {
        return gammaCorrection;
    }

    public void update(int width, int height, Bitmap.Config config) {
        if (bitmap == null || bitmap.getWidth() != width || bitmap.getHeight() != height) {
            recycleBitmap();
            bitmap = Bitmap.createBitmap(width, height, config);
        }
    }

    public boolean copyFrom(final Bitmap src) {
        recycleBitmap();
        bitmap = src.copy(src.getConfig(), true);
        return BitmapUtils.isValid(bitmap);
    }

    public void attach(final Bitmap src) {
        recycleBitmap();
        bitmap = src;
    }

    public ReaderImageImpl setRectangle(RectF rectangle) {
        this.rectangle = rectangle;
        return this;
    }

    public ReaderImageImpl setPos(int pos) {
        this.pos = pos;
        return this;
    }
}
