package com.onyx.gallery.request.shape;

import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.gallery.bundle.EditBundle;
import com.onyx.gallery.common.BaseRequest;
import com.onyx.gallery.handler.DrawHandler;
import com.onyx.gallery.handler.touch.TouchHandler;
import com.onyx.gallery.handler.touch.TouchHandlerManager;
import com.onyx.gallery.handler.touch.TouchHandlerType;
import com.onyx.gallery.helpers.DrawArgs;
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

    public UpdateCurrentShapeTypeRequest(EditBundle editBundle, int newShape) {
        super(editBundle);
        this.newShape = newShape;
    }

    @Override
    public void execute(@NotNull DrawHandler drawHandler) throws Exception {
        TouchHandlerManager touchHandlerManager = getEditBundle().getTouchHandlerManager();
        drawHandler.updateCurrShapeType(newShape);
        TouchHandlerType touchHandlerType;
        switch (newShape) {
            case ShapeFactory.SHAPE_CIRCLE:
            case ShapeFactory.SHAPE_RECTANGLE:
            case ShapeFactory.SHAPE_TRIANGLE:
            case ShapeFactory.SHAPE_LINE:
            case ExpandShapeFactory.SHAPE_DASH_LINE:
            case ExpandShapeFactory.SHAPE_WAVE_LINE:
            case ExpandShapeFactory.SHAPE_ARROW_LINE:
                touchHandlerType = TouchHandlerType.NORMAL_SHAPE;
                break;
            case ShapeFactory.SHAPE_EDIT_TEXT_SHAPE:
                touchHandlerType = TouchHandlerType.TEXT_INSERTION;
                break;
            case ExpandShapeFactory.SHAPE_MOSAIC:
                touchHandlerType = TouchHandlerType.MOSAIC;
                break;
            case ExpandShapeFactory.CROP:
                touchHandlerType = TouchHandlerType.CROP;
                break;
            case ExpandShapeFactory.ERASE:
                touchHandlerType = TouchHandlerType.ERASE;
                break;
            default:
                drawHandler.setStrokeStyle(DrawArgs.defaultStrokeType);
                drawHandler.updateLimitRect(drawHandler.getCurrLimitRect(), touchHandlerManager.canRawDrawingRenderEnabled());
                DrawArgs drawingArgs = drawHandler.getDrawingArgs();
                drawHandler.setStrokeColor(drawingArgs.getStrokeColor());
                drawHandler.setStrokeWidth(drawingArgs.getStrokeWidth());
                touchHandlerType = TouchHandlerType.EPD_SHAPE;
                break;
        }
        touchHandlerManager.activateHandler(touchHandlerType);
        TouchHandler activateHandler = touchHandlerManager.getActivateHandler();
        if (activateHandler != null) {
            drawHandler.setRawInputReaderEnable(activateHandler.canRawInputReaderEnable());
            drawHandler.setRawDrawingRenderEnabled(activateHandler.canRawDrawingRenderEnabled());
        }
        setRenderToScreen(true);
    }
}
