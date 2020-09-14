package com.onyx.gallery.request.textInput;

import android.graphics.Matrix;
import android.text.StaticLayout;

import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.ResManager;
import com.onyx.gallery.R;
import com.onyx.gallery.bundle.EditBundle;
import com.onyx.gallery.common.BaseRequest;
import com.onyx.gallery.handler.DrawHandler;
import com.onyx.gallery.utils.RenderHandlerUtils;
import com.onyx.gallery.utils.StaticLayoutUtils;
import com.onyx.gallery.views.shape.EditTextShapeExpand;

/**
 * <pre>
 *     author : lxw
 *     time   : 2018/8/15 17:49
 *     desc   :
 * </pre>
 */
public class RenderInputTextShapeRequest extends BaseRequest {

    private Shape textShape;
    private boolean isPlaceHWR;
    private Shape cursorShape;

    public RenderInputTextShapeRequest(EditBundle editBundle, Shape textShape) {
        super(editBundle);
        this.textShape = textShape;
        setPauseRawDraw(false);
    }

    public RenderInputTextShapeRequest setCursorShape(Shape cursorShape) {
        this.cursorShape = cursorShape;
        return this;
    }

    public RenderInputTextShapeRequest setPlaceHWR(boolean placeHWR) {
        isPlaceHWR = placeHWR;
        return this;
    }

    @Override
    public void execute(DrawHandler drawHandler) throws Exception {
        if (textShape == null) {
            return;
        }
        StaticLayout layout = StaticLayoutUtils.createTextLayout((EditTextShapeExpand) textShape);
        TouchPoint down = textShape.getPoints().get(0);
        TouchPoint up = textShape.getPoints().get(1);
        float deltaX = layout.getWidth();
        float deltaY = layout.getHeight();
        int lineCount = layout.getLineCount();
        int minLineCount = ResManager.getInteger(R.integer.min_edit_text_shape_row);
        if (lineCount > 0 && lineCount < minLineCount && !isPlaceHWR) {
            deltaY = deltaY / lineCount * minLineCount;
        }
        
        float[] pointList = new float[]{deltaX, deltaY};
        Matrix initMatrix = drawHandler.getInitMatrix();
        initMatrix.mapPoints(pointList);

        up.x = down.x + deltaX;
        up.y = down.y + deltaY;
        textShape.updatePoints();
        RenderHandlerUtils.renderSelectionRect(drawHandler, textShape, cursorShape);
    }
}
