package com.onyx.gallery.request

import android.graphics.*
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler
import com.onyx.gallery.utils.BitmapUtils
import com.onyx.gallery.views.ImageShapeExpand

/**
 * Created by Leung on 2020/6/29
 */
class SaveCropTransformRequest : BaseRequest() {

    override fun execute(drawHandler: DrawHandler) {
        val imageShape = drawHandler.getImageShape() ?: return
        val cropHandler = globalEditBundle.cropHandler
        val cropRect = RectF(cropHandler.cropRect)
        if (cropRect.isEmpty) {
            return
        }
        val cropBitmap = cropImage(cropRect)
        updateImageShape(imageShape, cropRect, cropBitmap)
        updateLimitRect(cropRect, imageShape.downPoint)
        BitmapUtils.saveBitmapToFile(context, globalEditBundle.filePath, cropBitmap)
        renderShapesToBitmap = true
        renderToScreen = true
    }

    private fun cropImage(orgRropRect: RectF): Bitmap {
        val filePath = globalEditBundle.filePath
        val imageBitmap = BitmapFactory.decodeFile(filePath, BitmapFactory.Options())
        val cropRect = RectF(orgRropRect)

        val matrix = Matrix()
        val normalizedMatrix = Matrix()
        matrix.postScale(globalEditBundle.initScaleFactor, globalEditBundle.initScaleFactor)
        matrix.postTranslate(globalEditBundle.initDx, globalEditBundle.initDy)
        matrix.invert(normalizedMatrix)
        normalizedMatrix.mapRect(cropRect)

        return Bitmap.createBitmap(
                imageBitmap,
                cropRect.left.toInt(),
                cropRect.top.toInt(),
                cropRect.width().toInt(),
                cropRect.height().toInt()
        )
    }

    private fun updateImageShape(imageShape: ImageShapeExpand, orgCropRect: RectF, cropBitmap: Bitmap) {
        val newBitmap = Bitmap.createScaledBitmap(cropBitmap, orgCropRect.width().toInt(), orgCropRect.height().toInt(), true)
        imageShape.setResourceBitmap(newBitmap)

        val surfaceView = drawHandler.surfaceView!!
        val dx: Float = surfaceView.width / 2 - orgCropRect.width() / 2.toFloat()
        val dy: Float = surfaceView.height / 2 - orgCropRect.height() / 2.toFloat()
        val downPoint = TouchPoint(dx, dy)
        imageShape.onDown(downPoint, downPoint)

        val up = TouchPoint(downPoint)
        up.x = downPoint.x + orgCropRect.width()
        up.y = downPoint.y + orgCropRect.height()
        imageShape.onUp(up, up)

        imageShape.ensureShapeUniqueId()
        imageShape.updateShapeRect()
    }

    private fun updateLimitRect(orgCropRect: RectF, downPoint: TouchPoint) {
        val newLimitRect = Rect(downPoint.x.toInt(), downPoint.y.toInt(),
                (downPoint.x + orgCropRect.width()).toInt(),
                (downPoint.y + orgCropRect.height()).toInt())
        drawHandler.updateLimitRect(newLimitRect)
        drawHandler.setRawDrawingRenderEnabled(false)
    }


}