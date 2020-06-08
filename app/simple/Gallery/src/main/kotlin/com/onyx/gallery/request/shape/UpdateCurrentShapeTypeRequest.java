package com.onyx.gallery.request.shape;

import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.gallery.common.BaseRequest;
import com.onyx.gallery.handler.DrawHandler;
import com.onyx.gallery.handler.touch.TouchHandlerType;

import org.jetbrains.annotations.NotNull;

/**
 * <pre>
 *     author : lxw
 *     time   : 2018/10/11 17:34
 *     desc   :
 * </pre>
 */
public class UpdateCurrentShapeTypeRequest extends BaseRequest {
    private int newShape;

    public UpdateCurrentShapeTypeRequest(int newShape) {
        this.newShape = newShape;
    }

    @Override
    public void execute(@NotNull DrawHandler drawHandler) throws Exception {
        drawHandler.updateCurrShapeType(newShape);
        TouchHandlerType touchHandlerType;
        switch (newShape) {
            case ShapeFactory.SHAPE_CIRCLE:
            case ShapeFactory.SHAPE_RECTANGLE:
            case ShapeFactory.SHAPE_TRIANGLE:
            case ShapeFactory.SHAPE_LINE:
                drawHandler.setRawDrawingRenderEnabled(false);
                touchHandlerType = TouchHandlerType.GRAPHICS;
                break;
            default:
                drawHandler.setRawDrawingRenderEnabled(true);
                touchHandlerType = TouchHandlerType.SCRIBBLE;
                break;
        }

        getGlobalEditBundle().getTouchHandlerManager().activateHandler(touchHandlerType);
    }
}
