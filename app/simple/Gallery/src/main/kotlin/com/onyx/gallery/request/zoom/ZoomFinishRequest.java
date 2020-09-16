package com.onyx.gallery.request.zoom;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.utils.RectUtils;
import com.onyx.gallery.bundle.EditBundle;
import com.onyx.gallery.common.BaseRequest;
import com.onyx.gallery.handler.DrawHandler;
import com.onyx.gallery.utils.NoteUtils;

import org.jetbrains.annotations.NotNull;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/8/15 18:30
 *     desc   :
 * </pre>
 */
public class ZoomFinishRequest extends BaseRequest {

    private float scale;
    private TouchPoint scalePoint;
    private boolean isViewScaling;

    public ZoomFinishRequest(EditBundle editBundle, float scale, TouchPoint scalePoint) {
        super(editBundle);
        this.scale = scale;
        this.scalePoint = scalePoint;
    }

    @Override
    public void execute(DrawHandler drawHandler) throws Exception {
        float initScale = getInitScale();
        RenderContext renderContext = drawHandler.getRenderContext();
        boolean canRawDrawingRenderEnabled = getEditBundle().getTouchHandlerManager().canRawDrawingRenderEnabled();

        Matrix initMatrix = drawHandler.getInitMatrix();
        RectF limitRectF = new RectF(drawHandler.getLastZoomLimitRect());
        if ((drawHandler.getRenderContextScale() * scale) <= initScale) {
            NoteUtils.resetZoom(renderContext, initMatrix);
            if (canRawDrawingRenderEnabled) {
                drawHandler.updateLimitRectEnableRawDrawing(drawHandler.getOrgLimitRect());
            } else {
                drawHandler.updateLimitRectDisableRawDrawing(drawHandler.getOrgLimitRect());
            }
        } else {
            renderContext.matrix.postScale(scale, scale, scalePoint.x, scalePoint.y);
            RectUtils.scale(renderContext.getZoomRect(), scale, scale);
            renderContext.setViewPortScale(scale * renderContext.getViewPortScale());
            NoteUtils.updateDrawRectWhenScale(getDrawRect(), scale, scalePoint);

            renderContext.scalingMatrix.mapRect(limitRectF);
            Rect newLimitRect = new Rect();
            limitRectF.round(newLimitRect);
            drawHandler.zoomLimitRect(newLimitRect);
        }
        renderContext.canvas.drawColor(Color.WHITE);
        isViewScaling = renderContext.isViewScaling();

        renderContext.setScalingMatrix(null);
        setRenderShapesToBitmap(true);
        setRenderToScreen(true);
    }

    @Override
    public void afterExecute(@NotNull DrawHandler drawHandler) {
        super.afterExecute(drawHandler);
        drawHandler.setRawInputReaderEnable(true);
        drawHandler.setRawDrawingRenderEnabled(canRawDrawingRenderEnabled());
    }

    private RectF getDrawRect() {
        return getDrawHandler().getRenderContext().getViewPortRect();
    }

    public boolean isViewScaling() {
        return isViewScaling;
    }

    private float getInitScale() {
        return getEditBundle().getInitScaleFactor();
    }

}
