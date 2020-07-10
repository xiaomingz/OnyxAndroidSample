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
import com.onyx.gallery.utils.ExpandShapeFactory;
import com.onyx.gallery.views.EditTextShapeExpand;

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
        List<Shape> shapes = drawHandler.getAllShapes();
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
        DrawHandler drawHandler = getDrawHandler();
        EditTextShapeExpand shape = (EditTextShapeExpand) ExpandShapeFactory.createShape(ExpandShapeFactory.EDIT_TEXT_SHAPE_EXPAND);
        ShapeTextStyle textStyle = new ShapeTextStyle();
        InsertTextConfig insertTextConfig = getInsertTextConfig();
        shape.setIndentation(insertTextConfig.isIndentation());
        shape.setTraditional(insertTextConfig.isTraditional());
        textStyle.setTextSize(DimenUtils.pt2px(ResManager.getAppContext(), insertTextConfig.getTextSize()))
                .setTextSpacing(insertTextConfig.getTextSpacing())
                .setTextBold(insertTextConfig.getBold())
                .setTextItalic(insertTextConfig.getItalic())
                .setFontFace(insertTextConfig.getFontFace());
        TouchPoint newPoint = point.clone();
        newPoint.applyMatrix(drawHandler.getRenderContext().matrix);
        RectF container = new RectF(drawHandler.getCurrLimitRect());
        int textWidth = (int) (container.width() / 2);
        textStyle.setTextWidth(textWidth);
        textStyle.setPointScale(drawingArgs.getNormalizeScale());
        shape.setTextStyle(textStyle);
        shape.setText("");
        shape.setColor(insertTextConfig.getTextColor());
        RectF shapeRect = locationShapeRect(shape, point, textWidth);
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

    private RectF locationShapeRect(Shape shape, TouchPoint point, int textWidth) {
        RenderContext renderContext = getRenderContext();
        RectF container = new RectF(getDrawHandler().getCurrLimitRect());
        StaticLayout layout = TextLayoutUtils.createTextLayout(shape);
        final float height = renderContext.getMatrixInvertValue(layout.getHeight()) * ResManager.getInteger(R.integer.min_edit_text_shape_row);
        RectF shapeRect = new RectF(point.x - textWidth / 2, point.y - height / 2, point.x + textWidth / 2, point.y + height);
        if (!RectUtils.contains(container, shapeRect)) {
            Matrix matrix = new Matrix();
            float dx = 0, dy = 0;
            if (shapeRect.left < container.left) {
                dx = container.left - shapeRect.left;
            } else if (shapeRect.right > container.right) {
                dx = container.right - shapeRect.right;
            }
            if (shapeRect.top < container.top) {
                dy = container.top - shapeRect.top;
            } else if (shapeRect.bottom > container.bottom) {
                dy = container.bottom - shapeRect.bottom;
            }
            matrix.setTranslate(dx, dy);
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
