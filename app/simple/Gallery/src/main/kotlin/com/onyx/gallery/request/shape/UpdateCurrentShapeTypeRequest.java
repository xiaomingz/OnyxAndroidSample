package com.onyx.gallery.request.shape;

import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.gallery.common.BaseRequest;
import com.onyx.gallery.handler.DrawHandler;
import com.onyx.gallery.handler.touch.TouchHandlerType;
import com.onyx.gallery.utils.ExpandShapeFactory;

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
            case ExpandShapeFactory.SHAP_DASH_LINE:
            case ExpandShapeFactory.SHAP_WAVE_LINE:
            case ExpandShapeFactory.SHAP_ARROW_LINE:
                drawHandler.setRawDrawingRenderEnabled(false);
                touchHandlerType = TouchHandlerType.NORMAL_SHAPE;
                break;
            case ShapeFactory.SHAPE_EDIT_TEXT_SHAPE:
                drawHandler.setRawDrawingRenderEnabled(false);
                touchHandlerType = TouchHandlerType.TEXT_INSERTION;
                break;
            case ExpandShapeFactory.SHAP_MOSAIC:
                drawHandler.setRawDrawingRenderEnabled(false);
                touchHandlerType = TouchHandlerType.MOSAIC;
                break;
            default:
                drawHandler.setRawDrawingRenderEnabled(true);
                touchHandlerType = TouchHandlerType.EPD_SHAPE;
                break;
        }
        getGlobalEditBundle().getTouchHandlerManager().activateHandler(touchHandlerType);
        setRenderToScreen(true);
    }
}
