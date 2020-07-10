package com.onyx.gallery.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceView;

import com.onyx.android.sdk.scribble.shape.RenderContext;

/**
 * Created by lxm on 2018/2/7.
 */

public class RendererUtils {

    public static RenderContext createRenderContext() {
        return RenderContext.create(new Paint(), new Matrix());
    }

    public static Rect checkSurfaceView(SurfaceView surfaceView) {
        if (surfaceView == null || !surfaceView.getHolder().getSurface().isValid()) {
            return null;
        }
        return new Rect(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
    }

    public static void clearBackground(final Canvas canvas, final Paint paint, final Rect rect) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(rect, paint);
    }

    public static void clearBitmap(final Bitmap bitmap, final RectF clearRect) {
        if (clearRect == null) {
            return;
        }
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.TRANSPARENT);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawRect(clearRect, paint);
    }

    public static void renderBackground(Context context,
                                        Canvas canvas,
                                        RenderContext renderContext,
                                        Rect viewRect) {
        RendererUtils.clearBackground(canvas, new Paint(), viewRect);
        Matrix matrix = new Matrix(renderContext.getViewPortMatrix());
        if (renderContext.scalingMatrix != null) {
            matrix.postConcat(renderContext.scalingMatrix);
        }
        renderContext.drawBackGround(context, canvas, viewRect, matrix);
    }

    public static Paint createRectPaint() {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

}
