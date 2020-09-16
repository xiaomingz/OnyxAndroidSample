package com.onyx.gallery.request.crop

import android.graphics.*
import com.onyx.android.sdk.data.Size
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.scribble.data.bean.ResourceType
import com.onyx.android.sdk.scribble.data.bean.ShapeResource
import com.onyx.android.sdk.utils.DateTimeUtil
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler
import com.onyx.gallery.handler.MirrorModel
import com.onyx.gallery.utils.BitmapUtils
import com.onyx.gallery.utils.ExpandShapeFactory
import com.onyx.gallery.utils.ExpandShapeFactory.createShape
import com.onyx.gallery.utils.ScribbleUtils
import com.onyx.gallery.views.shape.ImageShapeExpand
import java.io.File

/**
 * Created by Leung on 2020/6/29
 */
class SaveCropTransformRequest(editBundle: EditBundle) : BaseRequest(editBundle) {

    override fun execute(drawHandler: DrawHandler) {
        drawHandler.getImageShape() ?: return
        val cropRect = RectF(cropHandler.cropBoxRect)
        if (cropRect.isEmpty) {
            return
        }
        drawHandler.saveHandwritingDataToCropSnapshot()

        val filePath = editBundle.imagePath
        val cropBitmap = cropImage(filePath, cropRect)
        val imageSize = Size(cropBitmap.width, cropBitmap.height)

        updateRenderMatrix(imageSize)

        val newPath = File(context.cacheDir, "crop_${DateTimeUtil.getCurrentTime()}.png").absolutePath
        val newImageShape = createImageShape(newPath, imageSize)
        drawHandler.updateImageShape(newImageShape)
        updateLimitRect(editBundle.renderImageSize, drawHandler)

        BitmapUtils.saveBitmapToFile(context, newPath, cropBitmap)
        drawHandler.makeCropSnapshot(newPath, newImageShape)
        editBundle.imagePath = newPath

        cropBitmap.recycle()
        cropHandler.resetCropState()
        drawHandler.clearHandwritingData()
        drawHandler.setRawDrawingRenderEnabled(false)
        drawHandler.renderToBitmap(newImageShape)
        drawHandler.afterCreateImageShape()
    }

    private fun cropImage(filePath: String, orgCropRect: RectF): Bitmap {
        var imageBitmap = ScribbleUtils.drawScribbleToImage(drawHandler, filePath, editBundle.getNormalizedMatrix())
        val cropRect = RectF(orgCropRect)

        val normalizedMatrix = drawHandler.getNormalizedMatrix()
        if (cropHandler.hasRotateChange()) {
            imageBitmap = imageRotateChange(imageBitmap, normalizedMatrix)
        }
        if (cropHandler.hasMirrorChange()) {
            cropHandler.currMirrot?.let { imageBitmap = imageMirrorChange(imageBitmap, it) }
        }
        normalizedMatrix.mapRect(cropRect)
        return Bitmap.createBitmap(
                imageBitmap,
                cropRect.left.toInt(),
                cropRect.top.toInt(),
                cropRect.width().toInt(),
                cropRect.height().toInt()
        )
    }

    private fun imageRotateChange(imageBitmap: Bitmap, normalizedMatrix: Matrix): Bitmap {
        val width = imageBitmap.width
        val height = imageBitmap.height
        val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        val matrix = Matrix()
        matrix.postRotate(cropHandler.currAngle, (width / 2).toFloat(), (height / 2).toFloat())
        canvas.drawBitmap(imageBitmap, matrix, Paint())
        imageBitmap.recycle()
        normalizedMatrix.postConcat(matrix)
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

    private fun updateRenderMatrix(imageSize: Size) {
        editBundle.orgImageSize = Size(imageSize.width, imageSize.height)
        editBundle.renderImageSize = Size(imageSize.width, imageSize.height)
        val renderImageSize = editBundle.renderImageSize
        val initScaleFactor = editBundle.scaleToContainer(renderImageSize)

        val initMatrix = Matrix()
        initMatrix.postScale(initScaleFactor, initScaleFactor)
        val viewPortRect = drawHandler.renderContext.viewPortRect
        val floatArrayOf = floatArrayOf(imageSize.width.toFloat(), imageSize.height.toFloat())
        initMatrix.mapPoints(floatArrayOf)

        val offsetX = Math.abs(viewPortRect.width() - floatArrayOf[0]) / 2
        val offsetY = Math.abs(viewPortRect.height() - floatArrayOf[1]) / 2
        initMatrix.postTranslate(offsetX, offsetY)

        editBundle.offsetX = offsetX
        editBundle.offsetY = offsetY
        editBundle.initScaleFactor = initScaleFactor

        drawHandler.setRenderContextMatrix(initMatrix)
    }

    private fun updateLimitRect(renderImageSize: Size, drawHandler: DrawHandler) {
        val surfaceRect = drawHandler.surfaceRect
        val dx: Float = (surfaceRect.width() - renderImageSize.width) / 2.toFloat()
        val dy: Float = (surfaceRect.height() - renderImageSize.height) / 2.toFloat()

        val newLimitRect = Rect(dx.toInt(), dy.toInt(), dx.toInt() + renderImageSize.width, dy.toInt() + renderImageSize.height)
        this.drawHandler.updateLimitRectDisableRawDrawing(newLimitRect)
    }

    private fun createImageShape(imageFilePath: String, imageSize: Size): ImageShapeExpand {
        val shape = createShape(ExpandShapeFactory.IMAGE_SHAPE_EXPAND) as ImageShapeExpand
        val downPoint = TouchPoint(0f, 0f)
        shape.onDown(downPoint, downPoint)
        val up = TouchPoint(downPoint)
        up.x = downPoint.x + imageSize.width
        up.y = downPoint.y + imageSize.height
        shape.onUp(up, up)
        shape.ensureShapeUniqueId()
        shape.updateShapeRect()
        shape.resource = createImageResource(imageFilePath)
        return shape
    }

    private fun createImageResource(localPath: String): ShapeResource {
        val shapeResource = ShapeResource()
        shapeResource.localPath = localPath
        shapeResource.type = ResourceType.IMAGE
        return shapeResource
    }

}