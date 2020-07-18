package com.onyx.gallery.request.crop

import android.graphics.*
import com.onyx.android.sdk.data.Size
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.scribble.data.bean.ResourceType
import com.onyx.android.sdk.scribble.data.bean.ShapeResource
import com.onyx.android.sdk.utils.DateTimeUtil
import com.onyx.android.sdk.utils.FileUtils
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler
import com.onyx.gallery.handler.MirrorModel
import com.onyx.gallery.utils.BitmapUtils
import com.onyx.gallery.utils.ExpandShapeFactory
import com.onyx.gallery.utils.ScribbleUtils
import com.onyx.gallery.views.shape.ImageShapeExpand
import java.io.File

/**
 * Created by Leung on 2020/6/29
 */
class SaveCropTransformRequest : BaseRequest() {

    override fun execute(drawHandler: DrawHandler) {
        drawHandler.getImageShape() ?: return
        val cropRect = RectF(cropHandler.cropBoxRect)
        if (cropRect.isEmpty) {
            return
        }
        drawHandler.saveHandwritingDataToCropSnapshot()

        val filePath = globalEditBundle.filePath
        val cropBitmap = cropImage(filePath, cropRect)

        val imageSize = Size(cropBitmap.width, cropBitmap.height)
        globalEditBundle.initScaleFactor = globalEditBundle.scaleToContainer(imageSize)

        val newPath = File(FileUtils.getParent(globalEditBundle.filePath), "crop_${DateTimeUtil.getCurrentTime()}.png").absolutePath
        val newImageShape = createImageShape(newPath, imageSize, cropBitmap)
        drawHandler.updateImageShape(newImageShape)
        updateLimitRect(imageSize, newImageShape.downPoint)

        BitmapUtils.saveBitmapToFile(context, newPath, cropBitmap)
        drawHandler.makeCropSnapshot(newPath, newImageShape)
        globalEditBundle.filePath = newPath

        cropBitmap.recycle()
        cropHandler.resetCropState()
        drawHandler.clearHandwritingData()
        drawHandler.setRawDrawingRenderEnabled(false)
    }

    private fun cropImage(filePath: String, orgCropRect: RectF): Bitmap {
        var imageBitmap = ScribbleUtils.drawScribbleToImage(drawHandler, filePath, globalEditBundle.getNormalizedMatrix())
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
                matrix.postTranslate(-dx, 0f)
                matrix.postScale(-1f, 1f)
            }
            MirrorModel.BOTTOM -> {
                val dy = imageBitmap.height.toFloat()
                matrix.postTranslate(0f, -dy)
                matrix.postScale(1f, -1f)
            }
        }
        canvas.drawBitmap(imageBitmap, matrix, Paint())
        imageBitmap.recycle()
        return newBitmap
    }

    private fun updateLimitRect(imageSize: Size, downPoint: TouchPoint) {
        val newLimitRect = Rect(downPoint.x.toInt(), downPoint.y.toInt(),
                (downPoint.x + imageSize.width).toInt(),
                (downPoint.y + imageSize.height).toInt())
        drawHandler.updateLimitRect(newLimitRect)
    }

    private fun createImageShape(path: String, imageSize: Size, cropBitmap: Bitmap): ImageShapeExpand {
        val newBitmap = Bitmap.createScaledBitmap(cropBitmap, imageSize.width, imageSize.height, true)
        val imageShape = ExpandShapeFactory.createShape(ExpandShapeFactory.IMAGE_SHAPE_EXPAND) as ImageShapeExpand
        imageShape.setResourceBitmap(newBitmap)
        imageShape.setResource(createImageResource(path))
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

        return imageShape
    }

    private fun createImageResource(localPath: String): ShapeResource {
        val shapeResource = ShapeResource()
        shapeResource.localPath = localPath
        shapeResource.type = ResourceType.IMAGE
        return shapeResource
    }

}