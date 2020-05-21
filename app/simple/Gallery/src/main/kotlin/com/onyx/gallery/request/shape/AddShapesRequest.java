package com.onyx.gallery.request.shape;

import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.gallery.common.BaseRequest;
import com.onyx.gallery.helpers.NoteManager;

import java.util.List;

/**
 * Created by lxm on 2018/3/1.
 */

public class AddShapesRequest extends BaseRequest {

    private volatile List<Shape> shapes;

    public AddShapesRequest(List<Shape> shapes) {
        this.shapes = shapes;
    }

    @Override
    public void execute(NoteManager noteManager)throws Exception {
        if (shapes == null) {
            return;
        }
        noteManager.cacheShape(shapes);
        noteManager.renderToBitmap(shapes);
        setRenderShapesToBitmap(true);
        setRenderToScreen(true);
    }
}
