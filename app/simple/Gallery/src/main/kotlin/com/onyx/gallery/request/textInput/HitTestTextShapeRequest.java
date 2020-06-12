package com.onyx.gallery.request.textInput;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.text.StaticLayout;

import androidx.annotation.NonNull;

import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.ShapeTextStyle;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.utils.TextLayoutUtils;
import com.onyx.android.sdk.utils.DimenUtils;
import com.onyx.android.sdk.utils.RectUtils;
import com.onyx.android.sdk.utils.ResManager;
import com.onyx.gallery.R;
import com.onyx.gallery.common.BaseRequest;
import com.onyx.gallery.handler.DrawHandler;
import com.onyx.gallery.helpers.DrawArgs;
import com.onyx.gallery.helpers.InsertTextConfig;
import com.onyx.gallery.utils.NoteUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : lxw
 *     time   : 2018/8/21 16:08
 *     desc   :
 * </pre>
 */
public class HitTestTextShapeRequest extends BaseRequest {

    private TouchPoint touchPoint;
    private List<Shape> selectedShapes;
    private InsertTextConfig insertTextConfig;
    private DrawArgs drawingArgs;

    public HitTestTextShapeRequest(@NonNull DrawHandler drawHandler, TouchPoint touchPoint) {
        this.touchPoint = touchPoint;
        setPauseRawDraw(false);
    }

    public HitTestTextShapeRequest setInsertTextConfig(InsertTextConfig insertTextConfig) {
        this.insertTextConfig = insertTextConfig;
        return this;
    }

    public HitTestTextShapeRequest setDrawingArgs(DrawArgs drawingArgs) {
        this.drawingArgs = drawingArgs;
        return this;
    }

    @Override
    public void execute(DrawHandler drawHandler) throws Exception {
        if (touchPoint == null) {
            return;
        }
        List<Shape> shapes = drawHandler.getCacheShapeList();
        if (shapes == null || shapes.isEmpty()) {
            return;
        }
        selectedShapes = new ArrayList<>();
        for (Shape shape : shapes) {
            if (shape.getType() != ShapeFactory.SHAPE_EDIT_TEXT_SHAPE) {
                continue;
            }
            RectF rectF = shape.getBoundingRect();
            if (rectF.contains(touchPoint.x, touchPoint.y)) {
                selectedShapes.add(shape);
                break;
            }
        }
    }

    public Shape getHitTextShape() {
        Shape hitTextShape;
        if (hasSelectedShapes()) {
            hitTextShape = selectedShapes.get(0);
        } else {
            hitTextShape = createTextShape(touchPoint);
        }
        return hitTextShape;
    }

    private Shape createTextShape(TouchPoint point) {
        Shape shape = NoteUtils.createShape(ShapeFactory.SHAPE_EDIT_TEXT_SHAPE, ShapeFactory.LayoutType.FREE.ordinal());
        ShapeTextStyle textStyle = new ShapeTextStyle();
        InsertTextConfig insertTextConfig = getInsertTextConfig();
        textStyle.setTextSize(DimenUtils.pt2px(ResManager.getAppContext(), insertTextConfig.getTextSize()))
                .setTextSpacing(insertTextConfig.getTextSpacing())
                .setTextBold(insertTextConfig.getBold())
                .setTextItalic(insertTextConfig.getItalic());
//                .setFontFace(textInsertConfig.getFontFace());
        TouchPoint newPoint = point.clone();
        newPoint.applyMatrix(getDrawHandler().getRenderContext().matrix);
        RectF container = new RectF(getRenderContext().getViewPortRect());
        textStyle.setTextWidth((int) (container.right - newPoint.x - ResManager.getDimens(R.dimen.note_text_input_shape_left_right_margin)));
        textStyle.setPointScale(drawingArgs.getNormalizeScale());
        shape.setTextStyle(textStyle);
        shape.setText("");
        shape.setColor(drawingArgs.getStrokeColor());
        RectF shapeRect = locationShapeRect(shape, point);
        point.x = shapeRect.left;
        point.y = shapeRect.top;
        TouchPoint up = new TouchPoint();
        up.x = shapeRect.right;
        up.y = shapeRect.bottom;
        shape.onDown(point, point);
        shape.onUp(up, up);
        shape.ensureShapeUniqueId();
        shape.updateShapeRect();
        return shape;
    }

    private RectF locationShapeRect(Shape shape, TouchPoint point) {
        RectF container = new RectF(getRenderContext().getViewPortRect());
        container.right = getRenderContext().getMatrixInvertValue(container.right);
        container.bottom = getRenderContext().getMatrixInvertValue(container.bottom);
        StaticLayout layout = TextLayoutUtils.createTextLayout(shape);
        final float width = getRenderContext().getMatrixInvertValue(layout.getWidth());
        final float height = getRenderContext()
                .getMatrixInvertValue(layout.getHeight()) * ResManager.getInteger(R.integer.min_edit_text_shape_row);
        RectF shapeRect = new RectF(point.x, point.y, container.right, point.y + height);
        if (!RectUtils.contains(container, shapeRect)) {
            Matrix matrix = new Matrix();
            matrix.setTranslate(container.right - shapeRect.right < 0 ? -width : 0,
                    container.bottom - shapeRect.bottom < 0 ? -height : 0);
            matrix.mapRect(shapeRect);
        }
        return shapeRect;
    }

    private InsertTextConfig getInsertTextConfig() {
        return insertTextConfig;
    }

    private RenderContext getRenderContext() {
        return getDrawHandler().getRenderContext();
    }

    public List<Shape> getSelectedShapes() {
        return selectedShapes;
    }

    public boolean hasSelectedShapes() {
        return selectedShapes != null && !selectedShapes.isEmpty();
    }

}
