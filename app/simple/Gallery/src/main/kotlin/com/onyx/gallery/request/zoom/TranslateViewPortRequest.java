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
import com.onyx.gallery.helpers.NoteManager;

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
        return getNoteManager().getRenderContext().getZoomRect();
    }

    private RectF getViewPortRect() {
        return getNoteManager().getRenderContext().getViewPortRect();
    }

    @Override
    public void execute(NoteManager noteManager) throws Exception{
        normalizeViewport();
        if (!canTranslate()) {
            return;
        }
        translateBitmap();
        calculateTranslateRect();
        buildTranslateShapes();
        translateRenderMatrix(noteManager);
        renderToBitmap(noteManager, hRenderShapes, translateHRect);
        renderToBitmap(noteManager, vRenderShapes, translateVRect);
    }

    private void renderToBitmap(NoteManager noteManager, @NonNull List<Shape> shapes, RectF clipRect) {
        if (shapes.isEmpty()) {
            return;
        }
        Canvas renderCanvas = noteManager.getRenderContext().canvas;
        renderCanvas.save();
        renderCanvas.clipRect(clipRect);
        getNoteManager().renderToBitmap(shapes);
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

    private void translateRenderMatrix(NoteManager noteManager) {
        RenderContext renderContext = noteManager.getRenderContext();
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
        List<Shape> shapeList = getNoteManager().getShapeCacheList();
        for (Shape shape : shapeList) {
            RectF shapeRect = new RectF(shape.getBoundingRect());
            getNoteManager().getRenderContext().matrix.mapRect(shapeRect);
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
        Bitmap newBitmap = Bitmap.createBitmap((int) getViewPortRect().width(), (int) getViewPortRect().height(), Bitmap.Config.ARGB_8888);
        newBitmap.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(newBitmap);
        canvas.translate(dx, dy);
        canvas.drawBitmap(getNoteManager().getRenderContext().bitmap, 0, 0, null);
        getNoteManager().getRenderContext().updateBitmap(newBitmap);
    }
}
