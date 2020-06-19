package com.onyx.gallery.action.textInput;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.StaticLayout;

import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.rx.RxRequest;
import com.onyx.android.sdk.scribble.data.SelectionRect;
import com.onyx.android.sdk.scribble.data.ShapeTextStyle;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.scribble.utils.TextLayoutUtils;
import com.onyx.android.sdk.utils.RectUtils;
import com.onyx.gallery.common.BaseEditAction;
import com.onyx.gallery.utils.StaticLayoutUtils;
import com.onyx.gallery.views.EditTextShapeExpand;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/6/13 16:34
 *     desc   :
 * </pre>
 */
public class CreateCursorShapeByTouchPointAction extends BaseEditAction<RxRequest> {

    private Shape textShape;
    private SelectionRect selectionRect;
    private TouchPoint touchPoint;
    private float normalizeScale;

    private int cursorOffset;
    private Shape cursorShape;

    public CreateCursorShapeByTouchPointAction setTextShape(Shape textShape) {
        this.textShape = textShape;
        return this;
    }

    public CreateCursorShapeByTouchPointAction setSelectionRect(SelectionRect selectionRect) {
        this.selectionRect = selectionRect;
        return this;
    }

    public CreateCursorShapeByTouchPointAction setTouchPoint(TouchPoint touchPoint) {
        this.touchPoint = touchPoint;
        return this;
    }

    public CreateCursorShapeByTouchPointAction setNormalizeScale(float normalizeScale) {
        this.normalizeScale = normalizeScale;
        return this;
    }

    @Override
    public void execute(RxCallback callback) {
        if (textShape == null || textShape.getTextStyle() == null) {
            return;
        }
        ShapeTextStyle cloneTextStyle = textShape.getTextStyle().clone();
        if (cloneTextStyle == null) {
            return;
        }
        PointF pointF = selectionRect.getRenderMatrixPoint(selectionRect.getOriginRect().left, selectionRect.getOriginRect().top);
        touchPoint.x = touchPoint.x - pointF.x;
        touchPoint.y = touchPoint.y - pointF.y;

        RectF cursorRect = new RectF();
        StaticLayout layout = StaticLayoutUtils.createTextLayout((EditTextShapeExpand) textShape);
        int lineCount = layout.getLineCount();

        for (int i = 0; i < lineCount; i++) {
            Rect lineRect = new Rect();
            layout.getLineBounds(i, lineRect);

            if (lineRect.contains((int) touchPoint.x, (int) touchPoint.y)) {
                int start = layout.getLineStart(i);
                int end = layout.getLineEnd(i);

                if (textShape.getText() == null) {
                    return;
                }
                String content = textShape.getText().substring(start, end);
                StaticLayout lineLayout = createTextLayout(i, content, cloneTextStyle);

                int offset = lineLayout.getOffsetForHorizontal(0, touchPoint.x);
                cursorOffset = offset + start;

                offset = (int) lineLayout.getPrimaryHorizontal(offset);

                cursorRect.set(offset + pointF.x,
                        lineRect.top + pointF.y,
                        offset + pointF.x,
                        lineRect.bottom + pointF.y);
                RectUtils.scale(cursorRect, normalizeScale, normalizeScale);
                cursorShape = ShapeUtils.createCursorShape(cursorRect);
                return;
            }
        }
    }

    private StaticLayout createTextLayout(int line, String content, ShapeTextStyle cloneTextStyle) {
        if (line == 0 && ((EditTextShapeExpand) textShape).isIndentation()) {
            return StaticLayoutUtils.createTextLayout(content, cloneTextStyle, ((EditTextShapeExpand) textShape).isIndentation());
        }
        return TextLayoutUtils.createTextLayout(content, cloneTextStyle);
    }

    public int getCursorOffset() {
        return cursorOffset;
    }

    public Shape getCursorShape() {
        return cursorShape;
    }

}
