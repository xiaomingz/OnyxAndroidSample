package com.onyx.android.sdk.data;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * created by weiyang on 18-12-19
 */
public class ReaderNavigation {
    private PointF centerPoint = new PointF();
    private String orderHint = "-1";
    private float radius = 10;
    private final static float DEFAULT_RADIUS = 30;
    private final static float DEFAULT_OFFSET = 10;
    private RectF rectF = new RectF();

    public ReaderNavigation() {
    }

    public void setCenterPoint(float x, float y) {
        this.centerPoint.x = x;
        this.centerPoint.y = y;
    }

    public void setContentRect(float left, float top, float right, float bottom) {
        rectF.set(left, top, right, bottom);
    }

    public void setOrderHint(String orderHint) {
        this.orderHint = orderHint;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public PointF getCenterPoint() {
        centerPoint.x = rectF.left + DEFAULT_RADIUS + DEFAULT_OFFSET;
        centerPoint.y = rectF.top + DEFAULT_RADIUS + DEFAULT_OFFSET;
        return centerPoint;
    }

    public String getOrderHint() {
        return orderHint;
    }

    public float getRadius() {
        return DEFAULT_RADIUS;
    }
}
