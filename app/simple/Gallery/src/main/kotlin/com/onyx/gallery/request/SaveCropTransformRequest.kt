package com.onyx.gallery.request

import android.graphics.*
import com.onyx.android.sdk.data.Size
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler
import com.onyx.gallery.handler.MirrorModel
import com.onyx.gallery.utils.BitmapUtils
import com.onyx.gallery.utils.ScribbleUtils
import com.onyx.gallery.views.ImageShapeExpand

/**
 * Created by Leung on 2020/6/29
 */
class SaveCropTransformRequest : BaseRequest() {

    override fun execute(drawHandler: DrawHandler) {
        val imageShape = drawHandler.getImageShape() ?: return
        val cropRect = RectF(cropHandler.cropBoxRect)
        if (cropRect.isEmpty) {
            return
        }
        val filePath = globalEditBundle.filePath
        val cropBitmap = cropImage(filePath, cropRect)

        val imageSize = Size(cropBitmap.width, cropBitmap.height)
        val scaleFactor: Float = globalEditBundle.scaleToContainer(imageSize).apply {
            globalEditBundle.initScaleFactor = this
        }
        updateImageShape(imageShape, imageSize, cropBitmap)
        updateLimitRect(imageSize, imageShape.downPoint)
        BitmapUtils.saveBitmapToFile(context, globalEditBundle.filePath, cropBitmap)
        cropBitmap.recycle()
        cropHandler.resetCropState()
    }

    private fun cropImage(filePath: String, orgCropRect: RectF): Bitmap {
        var imageBitmap = ScribbleUtils.drawScribbleToImgae(drawHandler, filePath, globalEditBundle.getNormalizedMatrix())
        val cropRect = RectF(orgCropRect)
        if (cropHandler.hasRotateChange()) {
            imageBitmap = imageRotateChange(imageBitmap)
        }
        if (cropHandler.hasMirrorChange()) {
            cropHandler.currMirrot?.let { imageBitmap = imageMirrorChange(imageBitmap, it) }
        }
        globalEditBundle.getNormalizedMatrix().mapRect(cropRect)
        return Bitmap.createBitmap(
                imageBitmap,
                cropRect.left.toInt(),
                cropRect.top.toInt(),
                cropRect.width().toInt(),
                cropRect.height().toInt()
        )
    }

    private fun imageRotateChange(imageBitmap: Bitmap): Bitmap {
        val width = imageBitmap.width
        val height = imageBitmap.height
        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        val matrix = Matrix()
        matrix.postRotate(cropHandler.currAngle, (width / 2).toFloat(), (height / 2).toFloat())
        canvas.drawBitmap(imageBitmap, matrix, Paint())
        imageBitmap.recycle()
        return newBitmap
    }

    private fun imageMirrorChange(imageBitmap: Bitmap, mirrorModel: MirrorModel): Bitmap {
        val newBitmap = Bitmap.createBitmap(imageBitmap.width, imageBitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        val matrix = Matrix()
        when (mirrorModel) {
            MirrorModel.RIGHT -> {
                val dx = imageBitmap.width.toFloat()
                matrix.postTranslate(-dx, 0f);
                matrix.postScale(-1f, 1f)
            }
            MirrorModel.BOTTOM -> {
                val dy = imageBitmap.height.toFloat()
                matrix.postTranslate(0f, -dy);
                matrix.postScale(1f, -1f)
            }
        }
        canvas.drawBitmap(imageBitmap, matrix, Paint())
        imageBitmap.recycle()
        return newBitmap
    }

    private fun updateImageShape(imageShape: ImageShapeExpand, imageSize: Size, cropBitmap: Bitmap) {
        val newBitmap = Bitmap.createScaledBitmap(cropBitmap, imageSize.width, imageSize.height, true)
        imageShape.setResourceBitmap(newBitmap)

        val surfaceRect = drawHandler.surfaceRect
        val dx: Float = surfaceRect.width() / 2 - imageSize.width / 2.toFloat()
        val dy: Float = surfaceRect.height() / 2 - imageSize.height / 2.toFloat()
        val downPoint = TouchPoint(dx, dy)
        imageShape.onDown(downPoint, downPoint)

        val up = TouchPoint(downPoint)
        up.x = downPoint.x + imageSize.width
        up.y = downPoint.y + imageSize.height
        imageShape.onUp(up, up)

        imageShape.ensureShapeUniqueId()
        imageShape.updateShapeRect()

        val rect = Rect(dx.toInt(), dy.toInt(), (dx + imageSize.width).toInt(), (dy + imageSize.height).toInt())
        drawHandler.orgLimitRect = rect
        globalEditBundle.initDx = dx
        globalEditBundle.initDy = dy
    }

    private fun updateLimitRect(imageSize: Size, downPoint: TouchPoint) {
        val newLimitRect = Rect(downPoint.x.toInt(), downPoint.y.toInt(),
                (downPoint.x + imageSize.width).toInt(),
                (downPoint.y + imageSize.height).toInt())
        drawHandler.updateLimitRect(newLimitRect)
        drawHandler.setRawDrawingRenderEnabled(false)
    }


}