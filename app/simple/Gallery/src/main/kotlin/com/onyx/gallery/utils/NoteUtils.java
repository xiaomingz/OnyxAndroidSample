package com.onyx.gallery.utils;

import android.graphics.Matrix;
import android.graphics.RectF;

import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.utils.NumberUtils;
import com.onyx.android.sdk.utils.RectUtils;
import com.onyx.gallery.helpers.ConstantsKt;

/**
 * Created by lxm on 2018/3/6.
 */

public class NoteUtils {

    public static Shape createShape(int shapeType, int layoutType) {
        Shape shape = ShapeFactory.createShape(shapeType);
        shape.setLayoutType(layoutType);
        return shape;
    }

    public static void resetZoom(RenderContext renderContext, Matrix initMatrix) {
        float noteWidth = renderContext.getViewPortRect().width();
        float noteHeight = renderContext.getViewPortRect().height();
        renderContext.setViewPortScale(NumberUtils.FLOAT_ONE);
        renderContext.setViewPortRect(new RectF(0, 0, noteWidth, noteHeight));
        renderContext.setZoomRect(new RectF(0, 0, noteWidth, noteHeight));
        renderContext.setMatrix(initMatrix);
    }

    public static void updateDrawRectWhenScale(RectF drawRect, float scale, TouchPoint scalePoint) {
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale, scalePoint.x + drawRect.left, scalePoint.y + drawRect.top);
        float[] pts = new float[]{0, 0};
        matrix.mapPoints(pts);
        RectUtils.translate(drawRect, -pts[0], -pts[1]);
    }


}
