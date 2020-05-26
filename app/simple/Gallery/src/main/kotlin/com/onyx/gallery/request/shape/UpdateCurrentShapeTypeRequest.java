package com.onyx.gallery.request.shape;

import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.gallery.common.BaseRequest;
import com.onyx.gallery.event.eventhandler.EventHandlerManager;
import com.onyx.gallery.helpers.NoteManager;

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
    public void execute(@NotNull NoteManager noteManager) throws Exception {
        noteManager.updateCurrShapeType(newShape);
        EventHandlerManager.EventHandlerType eventHandlerType;
        switch (newShape) {
            case ShapeFactory.SHAPE_CIRCLE:
            case ShapeFactory.SHAPE_RECTANGLE:
            case ShapeFactory.SHAPE_TRIANGLE:
            case ShapeFactory.SHAPE_LINE:
                noteManager.setRawDrawingRenderEnabled(false);
                eventHandlerType = EventHandlerManager.EventHandlerType.NORMAL_SHAPE_EVENT;
                break;
            default:
                noteManager.setRawDrawingRenderEnabled(true);
                eventHandlerType = EventHandlerManager.EventHandlerType.PEN_EVENT;
                break;
        }

        getGlobalEditBundle().getEventHandlerManager().activateHandler(eventHandlerType);
    }
}
