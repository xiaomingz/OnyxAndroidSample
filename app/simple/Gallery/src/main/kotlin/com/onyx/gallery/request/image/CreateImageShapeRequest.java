package com.onyx.gallery.request.image;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.onyx.android.sdk.data.Size;
import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.bean.ResourceType;
import com.onyx.android.sdk.scribble.data.bean.ShapeResource;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.gallery.bundle.EditBundle;
import com.onyx.gallery.common.BaseRequest;
import com.onyx.gallery.handler.DrawHandler;
import com.onyx.gallery.utils.ExpandShapeFactory;
import com.onyx.gallery.views.shape.ImageShapeExpand;

import org.jetbrains.annotations.NotNull;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/3/13 16:25
 *     desc   :
 * </pre>
 */
public class CreateImageShapeRequest extends BaseRequest {

    private String imageFilePath;
    private ImageShapeExpand imageShape;
    private Rect scribbleRect;

    public CreateImageShapeRequest(@NotNull EditBundle editBundle) {
        super(editBundle);
    }

    public CreateImageShapeRequest setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
        return this;
    }

    public CreateImageShapeRequest setScribbleRect(Rect scribbleRect) {
        this.scribbleRect = scribbleRect;
        return this;
    }

    @Override
    public void execute(DrawHandler drawHandler) {
        EditBundle editBundle = getEditBundle();
        Size imageSize = editBundle.getOrgImageSize();
        Size renderImageSize = editBundle.getRenderImageSize();

        float scaleFactor = editBundle.scaleToContainer(renderImageSize);


        float dx = (scribbleRect.width() - renderImageSize.width) / 2;
        float dy = (scribbleRect.height() - renderImageSize.height) / 2;

        TouchPoint downPoint = new TouchPoint(0, 0);
        imageShape = createImageShape(downPoint, drawHandler.getRenderContext(), imageSize);

        Matrix initMatrix = new Matrix();
        initMatrix.postScale(scaleFactor, scaleFactor);
        RectF viewPortRect = drawHandler.getRenderContext().getViewPortRect();
        float[] floatArrayOf = new float[]{imageSize.width, imageSize.height};
        initMatrix.mapPoints(floatArrayOf);

        float offsetX = Math.abs(viewPortRect.width() - floatArrayOf[0]) / 2;
        float offsetY = Math.abs(viewPortRect.height() - floatArrayOf[1]) / 2;
        initMatrix.postTranslate(offsetX, offsetY);

        editBundle.setOffsetX(offsetX);
        editBundle.setOffsetY(offsetY);
        editBundle.setInitScaleFactor(scaleFactor);

        Rect rect = new Rect((int) dx, (int) dy, ((int) dx + renderImageSize.width), ((int) dy + renderImageSize.height));
        drawHandler.setOrgLimitRect(new Rect(rect));
        drawHandler.setCurrLimitRect(new Rect(rect));
        drawHandler.updateLimitRect(true);

        drawHandler.setRenderContextMatrix(initMatrix);

        drawHandler.renderToBitmap(imageShape);
        drawHandler.addShape(imageShape);

        setRenderToScreen(true);
    }

    @Override
    public void afterExecute(@NotNull DrawHandler drawHandler) {
        super.afterExecute(drawHandler);
        drawHandler.afterCreateImageShape();
        drawHandler.makeCropSnapshot(imageFilePath, (ImageShapeExpand) imageShape);
    }

    private ImageShapeExpand createImageShape(TouchPoint downPoint, RenderContext renderContext, Size imageSize) {
        ImageShapeExpand shape = (ImageShapeExpand) ExpandShapeFactory.createShape(ExpandShapeFactory.IMAGE_SHAPE_EXPAND);
        shape.onDown(downPoint, downPoint);
        TouchPoint up = new TouchPoint(downPoint);
        up.x = downPoint.x + imageSize.width;
        up.y = downPoint.y + imageSize.height;
        shape.onUp(up, up);
        shape.ensureShapeUniqueId();
        shape.updateShapeRect();
        shape.setResource(createImageResource(imageFilePath));
        return shape;
    }

    private ShapeResource createImageResource(String localPath) {
        ShapeResource shapeResource = new ShapeResource();
        shapeResource.localPath = localPath;
        shapeResource.type = ResourceType.IMAGE;
        return shapeResource;
    }

}
