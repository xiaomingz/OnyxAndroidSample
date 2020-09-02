package com.onyx.gallery.request.shape;

import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.gallery.bundle.EditBundle;
import com.onyx.gallery.common.BaseRequest;
import com.onyx.gallery.handler.DrawHandler;

import java.util.List;

/**
 * Created by lxm on 2018/3/1.
 */

public class AddShapesRequest extends BaseRequest {

    private volatile List<Shape> shapes;

    public AddShapesRequest(EditBundle editBundle, List<Shape> shapes) {
        super(editBundle);
        this.shapes = shapes;
    }

    @Override
    public void execute(DrawHandler drawHandler) throws Exception {
        if (shapes == null) {
            return;
        }
        drawHandler.addShapes(shapes);
        drawHandler.renderToBitmap(shapes);
        setRenderToScreen(true);
    }
}
