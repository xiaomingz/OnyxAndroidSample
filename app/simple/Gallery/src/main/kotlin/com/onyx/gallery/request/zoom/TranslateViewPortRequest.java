package com.onyx.gallery.request.zoom;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.RectUtils;
import com.onyx.gallery.common.BaseRequest;
import com.onyx.gallery.handler.DrawHandler;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/8/21 17:28
 *     desc   :
 * </pre>
 */
public class TranslateViewPortRequest extends BaseRequest {

    private float dx;
    private float dy;
    private RectF translateHRect;
    private RectF translateVRect;
    private List<Shape> hRenderShapes = new ArrayList<>();
    private List<Shape> vRenderShapes = new ArrayList<>();

    public TranslateViewPortRequest(float dx, float dy) {
        this.dx = dx;
        this.dy = dy;
    }

    private RectF getZoomRect() {
        return getDrawHandler().getRenderContext().getZoomRect();
    }

    private RectF getViewPortRect() {
        return getDrawHandler().getRenderContext().getViewPortRect();
    }

    @Override
    public void execute(DrawHandler drawHandler) throws Exception {
        normalizeViewport();
        if (!canTranslate()) {
            return;
        }
        translateBitmap();
        calculateTranslateRect();
        buildTranslateShapes();
        translateRenderMatrix(drawHandler);
        renderToBitmap(drawHandler, hRenderShapes, translateHRect);
        renderToBitmap(drawHandler, vRenderShapes, translateVRect);
        drawHandler.updateLimitRect(false);
        setRenderToScreen(true);
    }

    @Override
    public void afterExecute(@NotNull DrawHandler drawHandler) {
        super.afterExecute(drawHandler);
        drawHandler.setRawInputReaderEnable(true);
        drawHandler.setRawDrawingRenderEnabled(canRawDrawingRenderEnabled());
    }

    private void renderToBitmap(DrawHandler drawHandler, @NonNull List<Shape> shapes, RectF clipRect) {
        if (shapes.isEmpty()) {
            return;
        }
        Canvas renderCanvas = drawHandler.getRenderContext().canvas;
        renderCanvas.save();
        renderCanvas.clipRect(clipRect);
        getDrawHandler().renderToBitmap(shapes);
        renderCanvas.restore();
    }

    private void calculateTranslateRect() {
        float noteWidth = getViewPortRect().width();
        float noteHeight = getViewPortRect().height();
        if (dx > 0) {
            translateHRect = new RectF(0, 0, dx, noteHeight);
        } else {
            translateHRect = new RectF(noteWidth + dx, 0, noteWidth, noteHeight);
        }
        if (dy > 0) {
            translateVRect = new RectF(0, 0, noteWidth, dy);
        } else {
            translateVRect = new RectF(0, noteHeight + dy, noteWidth, noteHeight);
        }
    }

    private void translateRenderMatrix(DrawHandler drawHandler) {
        RenderContext renderContext = drawHandler.getRenderContext();
        renderContext.matrix.postTranslate(dx, dy);
    }

    private boolean canTranslate() {
        if (dx == 0 && dy == 0) {
            return false;
        }
        RectUtils.translate(getViewPortRect(), -dx, -dy);
        if (!getZoomRect().contains(getViewPortRect())) {
            RectUtils.translate(getViewPortRect(), dx, dy);
            return false;
        }
        return true;
    }

    private void normalizeViewport() {
        if (getViewPortRect().left < getZoomRect().left) {
            dx -= (getZoomRect().left - getViewPortRect().left);
        }
        if (getViewPortRect().right > getZoomRect().right) {
            dx += (getViewPortRect().right - getZoomRect().right);
        }
        if (getViewPortRect().bottom > getZoomRect().bottom) {
            dy += (getViewPortRect().bottom - getZoomRect().bottom);
        }
        if (getViewPortRect().top < getZoomRect().top) {
            dy -= (getZoomRect().top - getViewPortRect().top);
        }
    }

    private void buildTranslateShapes() {
        DrawHandler drawHandler = getDrawHandler();
        List<Shape> shapeList = getDrawHandler().getAllShapes();
        for (Shape shape : shapeList) {
            RectF shapeRect = new RectF(shape.getBoundingRect());
            drawHandler.getRenderContext().matrix.mapRect(shapeRect);
            RectUtils.translate(shapeRect, dx, dy);
            if (shapeRect.intersect(translateHRect)) {
                hRenderShapes.add(shape);
            }
            if (shapeRect.intersect(translateVRect)) {
                vRenderShapes.add(shape);
            }
        }
    }

    private void translateBitmap() {
        DrawHandler drawHandler = getDrawHandler();
        Bitmap newBitmap = Bitmap.createBitmap((int) getViewPortRect().width(), (int) getViewPortRect().height(), Bitmap.Config.ARGB_8888);
        newBitmap.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(newBitmap);
        canvas.translate(dx, dy);
        canvas.drawBitmap(drawHandler.getRenderContext().bitmap, 0, 0, null);
        drawHandler.getRenderContext().updateBitmap(newBitmap);
    }
}
