package com.onyx.gallery.action.textInput;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.StaticLayout;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.rx.RxRequest;
import com.onyx.android.sdk.scribble.data.SelectionRect;
import com.onyx.android.sdk.scribble.data.ShapeTextStyle;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.scribble.utils.TextLayoutUtils;
import com.onyx.android.sdk.utils.RectUtils;
import com.onyx.gallery.bundle.EditBundle;
import com.onyx.gallery.common.BaseEditAction;
import com.onyx.gallery.utils.StaticLayoutUtils;
import com.onyx.gallery.views.shape.EditTextShapeExpand;

import org.jetbrains.annotations.NotNull;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/6/13 16:34
 *     desc   :
 * </pre>
 */
public class CreateCursorShapeByOffsetAction extends BaseEditAction<RxRequest> {

    private Shape textShape;
    private SelectionRect selectionRect;
    private float normalizeScale;
    private int cursorOffset;

    private Shape cursorShape;

    public CreateCursorShapeByOffsetAction(@NotNull EditBundle editBundle) {
        super(editBundle);
    }

    public CreateCursorShapeByOffsetAction setTextShape(Shape textShape) {
        this.textShape = textShape;
        return this;
    }

    public CreateCursorShapeByOffsetAction setSelectionRect(SelectionRect selectionRect) {
        this.selectionRect = selectionRect;
        return this;
    }

    public CreateCursorShapeByOffsetAction setCursorOffset(int cursorOffset) {
        this.cursorOffset = cursorOffset;
        return this;
    }

    public CreateCursorShapeByOffsetAction setNormalizeScale(float normalizeScale) {
        this.normalizeScale = normalizeScale;
        return this;
    }

    @Override
    public void execute(RxCallback callback) {
        if (textShape == null || textShape.getTextStyle() == null || selectionRect == null) {
            return;
        }
        PointF pointF = selectionRect.getRenderMatrixPoint(selectionRect.getOriginRect().left, selectionRect.getOriginRect().top);
        StaticLayout layout = StaticLayoutUtils.createTextLayout((EditTextShapeExpand) textShape);
        int line = layout.getLineForOffset(cursorOffset);
        int start = layout.getLineStart(line);
        if (cursorOffset == start && line > 0) {
            line--;
        }
        start = layout.getLineStart(line);
        int end = layout.getLineEnd(line);

        Rect lineRect = new Rect();
        layout.getLineBounds(line, lineRect);

        ShapeTextStyle cloneTextStyle = textShape.getTextStyle().clone();
        if (cloneTextStyle == null || textShape.getText() == null) {
            return;
        }

        String content = textShape.getText().substring(start, end);
        StaticLayout lineLayout = TextLayoutUtils.createTextLayout(content, cloneTextStyle);

        int offset = (int) lineLayout.getPrimaryHorizontal(cursorOffset - start);
        if (textShape instanceof EditTextShapeExpand) {
            EditTextShapeExpand editTextShapeExpand = (EditTextShapeExpand) textShape;
            if (line == 0 && editTextShapeExpand.isIndentation()) {
                offset += editTextShapeExpand.getIndentationOffset();
            }
        }

        RectF cursorRect = new RectF();
        cursorRect.set(offset + pointF.x,
                lineRect.top + pointF.y,
                offset + pointF.x,
                lineRect.bottom + pointF.y);
        RectUtils.scale(cursorRect, normalizeScale, normalizeScale);
        cursorShape = ShapeUtils.createCursorShape(cursorRect);
    }

    public Shape getCursorShape() {
        return cursorShape;
    }

}
