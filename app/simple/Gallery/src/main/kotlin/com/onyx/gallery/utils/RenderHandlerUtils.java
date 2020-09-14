package com.onyx.gallery.utils;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import com.onyx.android.sdk.scribble.data.SelectionRect;
import com.onyx.android.sdk.scribble.data.ShapeTextStyle;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.gallery.handler.DrawHandler;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;

public class RenderHandlerUtils {

    public static void renderSelectionRect(@NonNull DrawHandler drawHandler, Shape textShape, Shape cursorShape) {
        try {
            RenderContext renderContext = drawHandler.getRenderContext();
            RectF viewPortRect = renderContext.getViewPortRect();
            renderContext.clearSelectionRect();

            List<Shape> shapes = new ArrayList<>();
            shapes.add(textShape);
            SelectionRect selectionRect = SelectionRect.buildSelectionRect(shapes, RenderContext.create(new Paint(), new Matrix()), viewPortRect);
            renderContext.setSelectionRect(selectionRect);

            List<Shape> renderShapes = new ArrayList<>();
            Shape shapeClone = ExpandShapeFactory.INSTANCE.ShapeClone(textShape);
            Matrix initMatrix = drawHandler.getInitMatrix();

            renderShapes.add(shapeClone);
            if (cursorShape != null) {
                Shape cursorShapeClone = ExpandShapeFactory.INSTANCE.ShapeClone(cursorShape);
                renderShapes.add(cursorShapeClone);
            }
            drawHandler.renderVarietyShapesToScreen(renderShapes,initMatrix);
            drawHandler.postSelectionBundle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
