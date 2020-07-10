package com.onyx.gallery.utils;

import android.graphics.Matrix;
import android.graphics.RectF;

import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.SelectionRect;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.RectUtils;
import com.onyx.gallery.handler.DrawHandler;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;

public class RenderHandlerUtils {

    public static float getMatrixNormalValue(RenderContext renderContext, float value) {
        Matrix matrix = renderContext.matrix;
        if (matrix == null) {
            return value;
        }
        float[] pts = new float[]{0, value};
        matrix.mapPoints(pts);
        return pts[1] - pts[0];
    }

    public static void renderSelectionRect(@NonNull DrawHandler drawHandler, Shape textShape, Shape cursorShape) {
        textShape.applyTransformMatrix();
        RenderContext renderContext = drawHandler.getRenderContext();
        renderContext.clearSelectionRect();
        List<Shape> shapes = new ArrayList<>();
        shapes.add(textShape);
        float normalizeScale = drawHandler.getDrawingArgs().getNormalizeScale();
        RectF limitRect = new RectF(drawHandler.getCurrLimitRect());
        RectUtils.scale(limitRect, normalizeScale, normalizeScale);
        SelectionRect selectionRect = SelectionRect.buildSelectionRect(shapes, renderContext, limitRect);
        renderContext.setSelectionRect(selectionRect);

        if (cursorShape != null) {
            shapes.add(cursorShape);
        }
        drawHandler.renderVarietyShapesToScreen(shapes);
        drawHandler.postSelectionBundle();
    }

    @NotNull
    public static TouchPoint onTranslate(@NotNull SelectionRect selectionRect, float dx, float dy) {
        TouchPoint translatePoint = new TouchPoint(dx, dy);
        selectionRect.getMatrix().postTranslate(dx, dy);
        RectF limitRect = selectionRect.limitRect;
        Matrix matrix = selectionRect.matrix;
        RectF originRect = selectionRect.getOriginRect();
        if (limitRect == null || limitRect.isEmpty()) {
            return translatePoint;
        }
        // if origin Rect is out of limit rect, can translate
        if (!limitRect.contains(originRect)) {
            return translatePoint;
        }
        RectF rectF = new RectF(originRect);
        matrix.mapRect(rectF);
        if (!limitRect.contains(rectF)) {
            if (isHorizontalOutOfLimit(limitRect, rectF)) {
                translatePoint.x = 0;
                matrix.postTranslate(-dx, 0);
            }
            if (isVerticalOutOfLimit(limitRect, rectF)) {
                translatePoint.y = 0;
                matrix.postTranslate(0, -dy);
            }
        }
        return translatePoint;
    }

    private static boolean isHorizontalOutOfLimit(RectF limitRect, RectF rect) {
        return !limitRect.contains(rect.left, rect.centerY())
                || !limitRect.contains(rect.right, rect.centerY());
    }

    private static boolean isVerticalOutOfLimit(RectF limitRect, RectF rect) {
        return !limitRect.contains(rect.centerX(), rect.top)
                || !limitRect.contains(rect.centerX(), rect.bottom);
    }


}
