package com.onyx.gallery.request

import android.graphics.*
import android.view.SurfaceView
import com.onyx.android.sdk.data.Size
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler
import com.onyx.gallery.handler.MirrorModel
import com.onyx.gallery.utils.BitmapUtils
import com.onyx.gallery.views.ImageShapeExpand

/**
 * Created by Leung on 2020/6/29
 */
class SaveCropTransformRequest : BaseRequest() {

    override fun execute(drawHandler: DrawHandler) {
        val imageShape = drawHandler.getImageShape() ?: return
        val cropRect = RectF(cropHandler.cropRect)
        if (cropRect.isEmpty) {
            return
        }
        val cropBitmap = cropImage(cropRect)

        val imageSize = Size(cropBitmap.width.toInt(), cropBitmap.height.toInt())
        val scaleFactor: Float = zoomInToContainer(drawHandler.surfaceView, imageSize).apply {
            globalEditBundle.initScaleFactor = this
        }
        updateImageSize(imageSize, scaleFactor)
        updateImageShape(imageShape, imageSize, cropBitmap)
        updateLimitRect(imageSize, imageShape.downPoint)
        BitmapUtils.saveBitmapToFile(context, globalEditBundle.filePath, cropBitmap)
        renderShapesToBitmap = true
        renderToScreen = true
    }

    private fun cropImage(orgRropRect: RectF): Bitmap {
        val filePath = globalEditBundle.filePath
        var imageBitmap = BitmapFactory.decodeFile(filePath, BitmapFactory.Options())
        val cropRect = RectF(orgRropRect)

        if (cropHandler.hasMirrorChange()) {
            cropHandler.currMirrot?.let { imageBitmap = imageMirrorChange(imageBitmap, it) }
        }
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

    private fun zoomInToContainer(containerView: SurfaceView, imageSize: Size): Float {
        val containerWidth = containerView.width.toFloat()
        val containerHeight = containerView.height.toFloat()
        var scaleFactor = 1.0f
        scaleFactor = if (containerWidth <= containerHeight) {
            containerWidth / imageSize.width
        } else {
            containerHeight / imageSize.height
        }
        return scaleFactor
    }

    private fun updateImageSize(imageSize: Size, scaleFactor: Float) {
        imageSize.width = (imageSize.width * scaleFactor).toInt()
        imageSize.height = (imageSize.height * scaleFactor).toInt()
    }

    private fun updateImageShape(imageShape: ImageShapeExpand, imageSize: Size, cropBitmap: Bitmap) {
        val newBitmap = Bitmap.createScaledBitmap(cropBitmap, imageSize.width, imageSize.height, true)
        imageShape.setResourceBitmap(newBitmap)

        val surfaceView = drawHandler.surfaceView
        val dx: Float = surfaceView.width / 2 - imageSize.width / 2.toFloat()
        val dy: Float = surfaceView.height / 2 - imageSize.height / 2.toFloat()
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