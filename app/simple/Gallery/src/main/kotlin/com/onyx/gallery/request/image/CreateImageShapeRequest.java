package com.onyx.gallery.request.image;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.SurfaceView;

import com.onyx.android.sdk.data.Size;
import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.bean.ResourceType;
import com.onyx.android.sdk.scribble.data.bean.ShapeResource;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.gallery.common.BaseRequest;
import com.onyx.gallery.helpers.NoteManager;
import com.onyx.gallery.utils.NoteUtils;

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
    public void execute(NoteManager noteManager) {
        Size imageSize = new Size();
        BitmapUtils.decodeBitmapSize(imageFilePath, imageSize);

        float scaleFactor = calculateScaleFactor(noteManager.getNoteView(), imageSize);
        updateImageSize(imageSize, scaleFactor);

        float dx = scribbleRect.width() / 2 - imageSize.width / 2;
        float dy = scribbleRect.height() / 2 - imageSize.height / 2;
        TouchPoint downPoint = new TouchPoint(dx, dy);
        imageShape = createImageShape(downPoint, noteManager.getRenderContext(), imageSize);
        noteManager.renderToBitmap(imageShape);
        noteManager.cacheShape(imageShape);

        getGlobalEditBundle().setInitDx(dx);
        getGlobalEditBundle().setInitDy(dy);
        getGlobalEditBundle().setInitScaleFactor(scaleFactor);
    }

    private void updateImageSize(Size imageSize, float scaleFactor) {
        imageSize.width = (int) (imageSize.width * scaleFactor);
        imageSize.height = (int) (imageSize.height * scaleFactor);
    }

    private float calculateScaleFactor(SurfaceView surfaceView, Size imageSize) {
        float containerWidth = surfaceView.getWidth();
        float containerHeight = surfaceView.getHeight();

        float scaleFactor = 1.0f;
        if (imageSize.width >= imageSize.height) {
            scaleFactor = containerWidth / imageSize.width;
        } else {
            scaleFactor = containerHeight / imageSize.height;
        }
        return scaleFactor;
    }

    private Shape createImageShape(TouchPoint downPoint, RenderContext renderContext, Size imageSize) {
        Matrix normalizedMatrix = new Matrix();
        renderContext.matrix.invert(normalizedMatrix);
        TouchPoint normalizedDownPoint = ShapeUtils.matrixTouchPoint(downPoint, normalizedMatrix);
        Shape shape = NoteUtils.createShape(ShapeFactory.SHAPE_IMAGE, ShapeFactory.LayoutType.FREE.ordinal());
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
