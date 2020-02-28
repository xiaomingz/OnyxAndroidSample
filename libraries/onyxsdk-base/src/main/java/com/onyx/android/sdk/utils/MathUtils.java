package com.onyx.android.sdk.utils;

import android.graphics.PointF;

/**
 * Created by Joy on 2016/6/2.
 */
public class MathUtils {
    public static double distance(int x1, int y1, int x2, int y2) {
        return Math.hypot(Math.abs(x1 - x2), Math.abs(y1 - y2));
    }

    /**
     * p1 means the fix center point, p2 is the extend point from center point ,p3 is the touch point
     *
     * @return
     */
    public static float calculateAngle(PointF center, PointF current, PointF previous) {
        float v1x = current.x - center.x;
        float v1y = current.y - center.y;

        //need to normalize:
        double l1 = Math.sqrt(v1x * v1x + v1y * v1y);
        v1x /= l1;
        v1y /= l1;

        float v2x = previous.x - center.x;
        float v2y = previous.y - center.y;

        //need to normalize:
        double l2 = Math.sqrt(v2x * v2x + v2y * v2y);
        v2x /= l2;
        v2y /= l2;

        double rad = Math.atan2(v2y, v2x) - Math.atan2(v1y, v1x);
        float degrees = (float) Math.toDegrees(rad);
        return degrees > 0 ? degrees : 360f + degrees;
    }

    public static PointF calculateMiddlePointFromTwoPoint(double p1X, double p1Y, double p2X, double p2Y) {
        return new PointF(((float) ((p1X + p2X) / 2)), ((float) (p1Y + p2Y) / 2));
    }

    public static PointF calculateTriangleCentroidPoint(PointF p1,PointF p2,PointF p3){
        return new PointF((p1.x + p2.x + p3.x) / 3, (p1.y + p2.y + p3.y) / 3);
    }

    public static long parseLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public static int parseInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return 0;
        }
    }

    public static float parseFloat(final String string) {
        float value = 0.0f;
        try {
            value = Float.valueOf(string);
        } catch (Exception e) {

        } finally {
            return value;
        }
    }

    /**
     * number is [start, end);
     */
    public static boolean withinRange(int number, int start, int end) {
        int tmpStart = Math.min(start, end);
        int tmpEnd = Math.max(start, end);
        return number >= tmpStart && number < tmpEnd;
    }

    public static float clampFloat(float target, float min, float max) {
        return Math.max(min, Math.min(max, target));
    }
}
