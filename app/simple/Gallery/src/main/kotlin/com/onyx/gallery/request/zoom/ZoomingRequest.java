package com.onyx.gallery.request.zoom;

import android.graphics.Matrix;

import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.gallery.bundle.EditBundle;
import com.onyx.gallery.common.BaseRequest;
import com.onyx.gallery.handler.DrawHandler;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/8/15 18:30
 *     desc   :
 * </pre>
 */
public class ZoomingRequest extends BaseRequest {

    private float scale;
    private TouchPoint scalePoint;

    public ZoomingRequest(EditBundle editBundle, float scale, TouchPoint scalePoint) {
        super(editBundle);
        this.scale = scale;
        this.scalePoint = scalePoint;
    }

    @Override
    public void execute(DrawHandler drawHandler) throws Exception {
        RenderContext renderContext = drawHandler.getRenderContext();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale, scalePoint.x, scalePoint.y);
        renderContext.setScalingMatrix(matrix);
        setRenderToScreen(true);
    }

    public float getScale() {
        return scale;
    }
}
