package com.onyx.gallery.handler

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.PointF
import android.graphics.RectF
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.event.ui.UpdateCropRectEvent
import com.onyx.gallery.request.transform.MirrorRequest
import com.onyx.gallery.request.transform.RotateRequest
import com.onyx.gallery.views.crop.CropImageView

/**
 * Created by Leung on 2020/6/29
 */
class CropHandler(val globalEditBundle: GlobalEditBundle) : CropImageView.OnCropRectChange {

    var cropRect = RectF()
    var rotateAngle = 90f
    var xAxisMirror = MirrorModel.LEFT
    var yAxisMirror = MirrorModel.TOP

    override fun onCropRectChange(cropRect: RectF) {
        cropRect.set(cropRect)
    }

    fun onCropChangeToCustomize() {
        val surfaceView = globalEditBundle.drawHandler.surfaceView ?: return
        val imageBitmap = globalEditBundle.drawHandler.getImageBitmap() ?: return
        val width: Int = surfaceView.width
        val height: Int = surfaceView.height
        val centerPoint = Point(width / 2, height / 2)
        val offset = Math.min(imageBitmap.width / 4, imageBitmap.height / 4).toFloat()
        cropRect.set(centerPoint.x - offset, centerPoint.y - offset, centerPoint.x + offset, centerPoint.y + offset)
        postEvent(UpdateCropRectEvent(cropRect))
    }

    fun onCropChangeTo_1_1() {
        val currLimitRect = globalEditBundle.drawHandler.currLimitRect
        cropRect.set(currLimitRect)
        postEvent(UpdateCropRectEvent(cropRect))
    }

    fun onCropChangeTo_4_3() {
        val currLimitRect = globalEditBundle.drawHandler.currLimitRect
        val width = currLimitRect.width()
        val height = currLimitRect.height()
        var cropWidth = width
        var cropHeight = 3 * cropWidth / 4

        if (cropHeight > height) {
            val pair = updateHeightRange(cropHeight, height, cropWidth)
            cropHeight = pair.first
            cropWidth = pair.second
        }
        cropRect.set(currLimitRect.left.toFloat(), currLimitRect.top.toFloat(),
                currLimitRect.left + cropWidth.toFloat(),
                currLimitRect.top + cropHeight.toFloat())
        postEvent(UpdateCropRectEvent(cropRect))
    }

    fun onCropChangeTo_3_4() {
        val currLimitRect = globalEditBundle.drawHandler.currLimitRect
        val width = currLimitRect.width()
        val height = currLimitRect.height()
        var cropHeight = height
        var cropWidth = 3 * cropHeight / 4

        if (cropWidth > width) {
            val pair = updateWidthRange(cropWidth, width, cropHeight)
            cropHeight = pair.first
            cropWidth = pair.second
        }
        cropRect.set(currLimitRect.left.toFloat(), currLimitRect.top.toFloat(),
                currLimitRect.left + cropWidth.toFloat(),
                currLimitRect.top + cropHeight.toFloat())
        postEvent(UpdateCropRectEvent(cropRect))
    }

    fun onCropChangeToHorizontal_16_9() {
        val currLimitRect = globalEditBundle.drawHandler.currLimitRect
        val width = currLimitRect.width()
        val height = currLimitRect.height()
        var cropWidth = width
        var cropHeight = 9 * width / 16

        if (cropHeight > height) {
            val pair = updateHeightRange(cropHeight, height, cropWidth)
            cropHeight = pair.first
            cropWidth = pair.second
        }
        cropRect.set(currLimitRect.left.toFloat(), currLimitRect.top.toFloat(),
                currLimitRect.left + cropWidth.toFloat(),
                currLimitRect.top + cropHeight.toFloat())
        postEvent(UpdateCropRectEvent(cropRect))
    }

    fun onCropChangeToVertical_16_9() {
        val currLimitRect = globalEditBundle.drawHandler.currLimitRect
        val width = currLimitRect.width()
        val height = currLimitRect.height()
        var cropHeight = height
        var cropWidth = 9 * height / 16

        if (cropWidth > width) {
            val pair = updateWidthRange(cropWidth, width, cropHeight)
            cropHeight = pair.first
            cropWidth = pair.second
        }
        cropRect.set(currLimitRect.left.toFloat(), currLimitRect.top.toFloat(),
                currLimitRect.left + cropWidth.toFloat(),
                currLimitRect.top + cropHeight.toFloat())
        postEvent(UpdateCropRectEvent(cropRect))
    }

    private fun updateWidthRange(cropWidth: Int, width: Int, cropHeight: Int): Pair<Int, Int> {
        var cropWidth1 = cropWidth
        var cropHeight1 = cropHeight
        val offsetWidth = cropWidth1 - width
        val offsetHeight = cropHeight1 * offsetWidth / cropWidth1
        cropWidth1 -= offsetWidth
        cropHeight1 -= offsetHeight
        return Pair(cropHeight1, cropWidth1)
    }

    private fun updateHeightRange(cropHeight: Int, height: Int, cropWidth: Int): Pair<Int, Int> {
        var cropHeight1 = cropHeight
        var cropWidth1 = cropWidth
        val offsetHeight = cropHeight1 - height
        val offsetWidth = cropWidth1 * offsetHeight / cropHeight1
        cropWidth1 -= offsetWidth
        cropHeight1 -= offsetHeight
        return Pair(cropHeight1, cropWidth1)
    }

    private fun getImageBitmap(): Bitmap = globalEditBundle.drawHandler.getImageBitmap()!!

    fun onRotateToLeft() {
        val centerPoint = getCenterPoint()
        globalEditBundle.enqueue(RotateRequest(-rotateAngle, centerPoint), null)
    }

    fun onRotateToRight() {
        val centerPoint = getCenterPoint()
        globalEditBundle.enqueue(RotateRequest(rotateAngle, centerPoint), null)
    }

    fun onXAxisChange(): MirrorModel {
        xAxisMirror = if (xAxisMirror == MirrorModel.LEFT) {
            MirrorModel.RIGHT
        } else {
            MirrorModel.LEFT
        }.apply { onMirror(this) }
        return xAxisMirror
    }

    fun onYAxisChange(): MirrorModel {
        yAxisMirror = if (yAxisMirror == MirrorModel.TOP) {
            MirrorModel.BOTTOM
        } else {
            MirrorModel.TOP
        }.apply { onMirror(this) }
        return yAxisMirror
    }

    private fun onMirror(mirrorModel: MirrorModel) {
        globalEditBundle.enqueue(MirrorRequest(mirrorModel), null)
    }

    private fun getCenterPoint(): PointF {
        val surfaceView = globalEditBundle.drawHandler.surfaceView
        surfaceView ?: return PointF()
        return PointF((surfaceView!!.width / 2).toFloat(), (surfaceView.height / 2).toFloat())
    }

    private fun postEvent(event: Any) {
        globalEditBundle.postEvent(event)
    }

    fun release() {
        cropRect.set(0f, 0f, 0f, 0f)
    }

}