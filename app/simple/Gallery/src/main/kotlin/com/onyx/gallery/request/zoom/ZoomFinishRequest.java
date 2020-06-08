package com.onyx.gallery.request.zoom;

import android.graphics.Color;
import android.graphics.RectF;

import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.utils.NumberUtils;
import com.onyx.android.sdk.utils.RectUtils;
import com.onyx.gallery.common.BaseRequest;
import com.onyx.gallery.handler.DrawHandler;
import com.onyx.gallery.utils.NoteUtils;

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

    public ZoomFinishRequest(float scale, TouchPoint scalePoint) {
        this.scale = scale;
        this.scalePoint = scalePoint;
    }

    @Override
    public void execute(DrawHandler drawHandler) throws Exception {
        RenderContext renderContext = drawHandler.getRenderContext();
        if ((renderContext.getViewPortScale() * scale) <= NumberUtils.FLOAT_ONE) {
            NoteUtils.resetZoom(renderContext);
        } else {
            renderContext.matrix.postScale(scale, scale, scalePoint.x, scalePoint.y);
            RectUtils.scale(renderContext.getZoomRect(), scale, scale);
            renderContext.setViewPortScale(scale * renderContext.getViewPortScale());
            NoteUtils.updateDrawRectWhenScale(getDrawRect(), scale, scalePoint);
        }
        renderContext.setScalingMatrix(null);
        renderContext.canvas.drawColor(Color.WHITE);
        setRenderShapesToBitmap(true);
        isViewScaling = renderContext.isViewScaling();
        drawHandler.setRawDrawingEnabled(true);
        drawHandler.updateLimitRect();
    }

    private RectF getDrawRect() {
        return getDrawHandler().getRenderContext().getViewPortRect();
    }

    public boolean isViewScaling() {
        return isViewScaling;
    }
}
