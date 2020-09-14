package com.onyx.gallery.request.textInput;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.text.StaticLayout;

import androidx.annotation.NonNull;

import com.onyx.android.sdk.data.Size;
import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.ShapeTextStyle;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.utils.DimenUtils;
import com.onyx.android.sdk.utils.RectUtils;
import com.onyx.android.sdk.utils.ResManager;
import com.onyx.gallery.R;
import com.onyx.gallery.bundle.EditBundle;
import com.onyx.gallery.common.BaseRequest;
import com.onyx.gallery.handler.DrawHandler;
import com.onyx.gallery.helpers.DrawArgs;
import com.onyx.gallery.helpers.InsertTextConfig;
import com.onyx.gallery.utils.ExpandShapeFactory;
import com.onyx.gallery.utils.StaticLayoutUtils;
import com.onyx.gallery.views.shape.EditTextShapeExpand;

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

    public HitTestTextShapeRequest(@NonNull EditBundle editBundle, @NonNull DrawHandler drawHandler, TouchPoint touchPoint) {
        super(editBundle);
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
        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);
            if (shape.getType() != ShapeFactory.SHAPE_EDIT_TEXT_SHAPE && shape.getType() != ExpandShapeFactory.EDIT_TEXT_SHAPE_EXPAND) {
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
        Size orgImageSize = getEditBundle().getOrgImageSize();
        RectF container = new RectF(0, 0, orgImageSize.width, orgImageSize.height);

        EditTextShapeExpand shape = (EditTextShapeExpand) ExpandShapeFactory.createShape(ExpandShapeFactory.EDIT_TEXT_SHAPE_EXPAND);
        ShapeTextStyle textStyle = new ShapeTextStyle();
        InsertTextConfig insertTextConfig = getInsertTextConfig();
        shape.setIndentation(insertTextConfig.isIndentation());
        shape.setTraditional(insertTextConfig.isTraditional());
        float textSize = DimenUtils.pt2px(ResManager.getAppContext(), insertTextConfig.getTextSize());
        textSize *= getDrawHandler().getNormalizedScale();
        textStyle.setTextSize(textSize)
                .setTextSpacing(insertTextConfig.getTextSpacing())
                .setTextBold(insertTextConfig.getBold())
                .setTextItalic(insertTextConfig.getItalic())
                .setFontFace(insertTextConfig.getFontFace());
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
        EditBundle editBundle = getEditBundle();
        Size orgImageSize = editBundle.getOrgImageSize();
        RectF container = new RectF(0, 0, orgImageSize.width, orgImageSize.height);
        StaticLayout layout = StaticLayoutUtils.createTextLayout((EditTextShapeExpand) shape);
        final float height = layout.getHeight() * ResManager.getInteger(R.integer.min_edit_text_shape_row);
        RectF shapeRect = new RectF(point.x, point.y, point.x + textWidth, point.y + height);
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
