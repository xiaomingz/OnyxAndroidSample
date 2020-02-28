package com.onyx.android.sdk.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/8/9 11:07
 *     desc   :
 * </pre>
 */
public class ColorUtils {

    private static final int GRAY_BRUSH_BITMAP_SIZE = 2;
    private static final int GRAY_INCREMENTAL_INTERVAL = 64;
    private static final int GRAY_BRUSH_DRAW_POINT_SIZE = 1;

    public static int getRedAisle(int color) {
        return (color & 0xff0000) >> 16;
    }

    public static int getGreenAisle(int color) {
        return (color & 0x00ff00) >> 8;
    }

    public static int getBlueAisle(int color) {
        return (color & 0x0000ff);
    }

    public static boolean isNeutralColor(int color) {
        return getRedAisle(color) == getGreenAisle(color)
                && getGreenAisle(color) == getBlueAisle(color);
    }

    public static boolean isGrayColor(int color) {
        return isNeutralColor(color) && getRedAisle(color) > 0 && getRedAisle(color) < 256;
    }

    public static Bitmap grayBrush(int color, float strokeWidth) {
        int whitePointCount = ColorUtils.getBlueAisle(color) / GRAY_INCREMENTAL_INTERVAL;
        Bitmap bitmap = Bitmap.createBitmap(GRAY_BRUSH_BITMAP_SIZE, GRAY_BRUSH_BITMAP_SIZE, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.BLACK);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(strokeWidth);
        int drawCount = 0;
        for (int left = 0; left < GRAY_BRUSH_BITMAP_SIZE; left++) {
            if (drawCount >= whitePointCount) {
                break;
            }
            for (int top = 0; top < GRAY_BRUSH_BITMAP_SIZE; top++) {
                canvas.drawRect(new RectF(left,
                        top, left + GRAY_BRUSH_DRAW_POINT_SIZE,
                        top + GRAY_BRUSH_DRAW_POINT_SIZE), paint);
                drawCount++;
                if (drawCount >= whitePointCount) {
                    break;
                }
            }
        }
        return bitmap;
    }
}
