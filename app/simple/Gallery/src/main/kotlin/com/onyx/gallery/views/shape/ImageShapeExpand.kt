package com.onyx.gallery.views.shape

import android.graphics.Bitmap
import android.graphics.Matrix
import com.onyx.android.sdk.scribble.shape.BaseShape
import com.onyx.android.sdk.scribble.shape.RenderContext
import com.onyx.android.sdk.scribble.utils.ShapeUtils
import com.onyx.android.sdk.utils.BitmapUtils
import com.onyx.gallery.utils.ExpandShapeFactory

/**
 * Created by Leung on 2020/6/24
 */
class ImageShapeExpand : BaseShape() {

    fun getImageBitmap(): Bitmap = resourceBitmap

    fun setResourceBitmap(imageBitmap: Bitmap) {
        resourceBitmap = imageBitmap
    }

    override fun getType(): Int = ExpandShapeFactory.IMAGE_SHAPE_EXPAND

    override fun render(renderContext: RenderContext) {
        if (resource == null || !resource.isImageResource
                || !resource.isLocalExist) {
            return
        }
        val width = currentPoint.x
        val height = currentPoint.y
        if (!BitmapUtils.isValid(resourceBitmap)) {
            resourceBitmap = BitmapUtils.decodeBitmap(resource.localPath, width.toInt(), height.toInt())
        }
        if (!BitmapUtils.isValid(resourceBitmap)) {
            return
        }
        applyStrokeStyle(renderContext)
        val matrix = Matrix(renderContext.normalizeMatrix)
        matrix.postConcat(getRenderMatrix(renderContext))
        renderContext.canvas.drawBitmap(resourceBitmap, matrix, renderContext.paint)
    }

    override fun hitTest(x: Float, y: Float, radius: Float): Boolean {
        val matrix = Matrix()
        if (getMatrix() != null) {
            getMatrix().invert(matrix)
        }
        val pts = floatArrayOf(x, y)
        matrix.mapPoints(pts)
        val originRect = getOriginRect() ?: return false
        return ShapeUtils.circleRectHitTest(originRect, pts[0], pts[1], radius)
    }

}