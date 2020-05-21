package com.onyx.gallery.request.zoom;

import android.graphics.Matrix;

import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.gallery.common.BaseRequest;
import com.onyx.gallery.helpers.NoteManager;

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

    public ZoomingRequest(float scale, TouchPoint scalePoint) {
        this.scale = scale;
        this.scalePoint = scalePoint;
    }

    @Override
    public void execute(NoteManager noteManager) throws Exception {
        RenderContext renderContext = noteManager.getRenderContext();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale, scalePoint.x, scalePoint.y);
        renderContext.setScalingMatrix(matrix);
    }

    public float getScale() {
        return scale;
    }
}
