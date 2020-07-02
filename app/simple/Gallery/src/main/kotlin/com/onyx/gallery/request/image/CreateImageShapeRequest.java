package com.onyx.gallery.request.image;

import android.graphics.Matrix;
import android.graphics.Rect;

import com.onyx.android.sdk.data.Size;
import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.bean.ResourceType;
import com.onyx.android.sdk.scribble.data.bean.ShapeResource;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.gallery.common.BaseRequest;
import com.onyx.gallery.handler.DrawHandler;
import com.onyx.gallery.utils.ExpandShapeFactory;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/3/13 16:25
 *     desc   :
 * </pre>
 */
public class CreateImageShapeRequest extends BaseRequest {

    private String imageFilePath;
    private Shape imageShape;
    private Rect scribbleRect;

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
        Size imageSize = new Size();
        BitmapUtils.decodeBitmapSize(imageFilePath, imageSize);

        float scaleFactor = calculatesSaleFactor(imageSize);
        updateImageSize(imageSize, scaleFactor);

        float dx = scribbleRect.width() / 2 - imageSize.width / 2;
        float dy = scribbleRect.height() / 2 - imageSize.height / 2;
        TouchPoint downPoint = new TouchPoint(dx, dy);
        imageShape = createImageShape(downPoint, drawHandler.getRenderContext(), imageSize);
        drawHandler.renderToBitmap(imageShape);
        drawHandler.addShape(imageShape);
        Rect rect = new Rect((int) dx, (int) dy, ((int) dx + imageSize.width), ((int) dy + imageSize.height));
        drawHandler.setOrgLimitRect(rect);
        getGlobalEditBundle().setInitDx(dx);
        getGlobalEditBundle().setInitDy(dy);
        getGlobalEditBundle().setInitScaleFactor(scaleFactor);
        drawHandler.updateLimitRect();
        setRenderToScreen(true);
    }

    private float calculatesSaleFactor(Size imageSize) {
        return getGlobalEditBundle().scaleToContainer(imageSize);
    }

    private void updateImageSize(Size imageSize, float scaleFactor) {
        imageSize.width = (int) (imageSize.width * scaleFactor);
        imageSize.height = (int) (imageSize.height * scaleFactor);
    }

    private Shape createImageShape(TouchPoint downPoint, RenderContext renderContext, Size imageSize) {
        Matrix normalizedMatrix = new Matrix();
        renderContext.matrix.invert(normalizedMatrix);
        TouchPoint normalizedDownPoint = ShapeUtils.matrixTouchPoint(downPoint, normalizedMatrix);
        Shape shape = ExpandShapeFactory.createShape(ExpandShapeFactory.IMAGE_SHAPE_EXPAND);
        shape.onDown(normalizedDownPoint, normalizedDownPoint);
        TouchPoint up = new TouchPoint(downPoint);
        up.x = downPoint.x + imageSize.width;
        up.y = downPoint.y + imageSize.height;
        TouchPoint normalizedUpPoint = ShapeUtils.matrixTouchPoint(up, normalizedMatrix);
        shape.onUp(normalizedUpPoint, normalizedUpPoint);
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

    public Shape getImageShape() {
        return imageShape;
    }

}
