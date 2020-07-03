package com.onyx.gallery.handler

import android.graphics.*
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.event.ui.StartRotateEvent
import com.onyx.gallery.event.ui.UpdateCropRectEvent
import com.onyx.gallery.request.transform.MirrorRequest
import com.onyx.gallery.request.transform.RotateRequest
import com.onyx.gallery.views.crop.CropImageView

/**
 * Created by Leung on 2020/6/29
 */
class CropHandler(val globalEditBundle: GlobalEditBundle) : CropImageView.OnCropRectChange {

    companion object {
        const val SINGLE_ROTATE_ANGLE = 90f
        const val ROTATE_CYCLE = 360f
    }

    var cropBoxRect = RectF()
    var currAngle = 0f
    var xAxisMirror = MirrorModel.LEFT
    var yAxisMirror = MirrorModel.TOP
    var currMirrot: MirrorModel? = null

    override fun onCropRectChange(newCropRect: RectF) {
        cropBoxRect.set(newCropRect)
    }

    fun onCropChangeToCustomize() {
        val surfaceRect = globalEditBundle.drawHandler.surfaceRect
        val imageBitmap = getImageBitmap() ?: return
        val width: Int = surfaceRect.width()
        val height: Int = surfaceRect.height()
        val centerPoint = Point(width / 2, height / 2)
        val offset = Math.min(imageBitmap.width / 4, imageBitmap.height / 4).toFloat()
        cropBoxRect.set(centerPoint.x - offset, centerPoint.y - offset, centerPoint.x + offset, centerPoint.y + offset)
        postEvent(UpdateCropRectEvent(cropBoxRect))
    }

    fun onCropChangeTo_1_1() {
        val currLimitRect = globalEditBundle.drawHandler.currLimitRect
        cropBoxRect.set(currLimitRect)
        postEvent(UpdateCropRectEvent(cropBoxRect))
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
        cropBoxRect.set(currLimitRect.left.toFloat(), currLimitRect.top.toFloat(),
                currLimitRect.left + cropWidth.toFloat(),
                currLimitRect.top + cropHeight.toFloat())
        postEvent(UpdateCropRectEvent(cropBoxRect))
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
        cropBoxRect.set(currLimitRect.left.toFloat(), currLimitRect.top.toFloat(),
                currLimitRect.left + cropWidth.toFloat(),
                currLimitRect.top + cropHeight.toFloat())
        postEvent(UpdateCropRectEvent(cropBoxRect))
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
        cropBoxRect.set(currLimitRect.left.toFloat(), currLimitRect.top.toFloat(),
                currLimitRect.left + cropWidth.toFloat(),
                currLimitRect.top + cropHeight.toFloat())
        postEvent(UpdateCropRectEvent(cropBoxRect))
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
        cropBoxRect.set(currLimitRect.left.toFloat(), currLimitRect.top.toFloat(),
                currLimitRect.left + cropWidth.toFloat(),
                currLimitRect.top + cropHeight.toFloat())
        postEvent(UpdateCropRectEvent(cropBoxRect))
    }

    private fun updateWidthRange(cropWidth: Int, width: Int, cropHeight: Int): Pair<Int, Int> {
        var newCropWidth = cropWidth
        var newCropHeight = cropHeight
        val offsetWidth = newCropWidth - width
        val offsetHeight = newCropHeight * offsetWidth / newCropWidth
        newCropWidth -= offsetWidth
        newCropHeight -= offsetHeight
        return Pair(newCropHeight, newCropWidth)
    }

    private fun updateHeightRange(cropHeight: Int, orgHeight: Int, cropWidth: Int): Pair<Int, Int> {
        var newCropHeight = cropHeight
        var newCropWidth = cropWidth
        val offsetHeight = newCropHeight - orgHeight
        val offsetWidth = newCropWidth * offsetHeight / newCropHeight
        newCropWidth -= offsetWidth
        newCropHeight -= offsetHeight
        return Pair(newCropHeight, newCropWidth)
    }

    private fun getImageBitmap(): Bitmap? = globalEditBundle.drawHandler.getImageBitmap()

    fun onRotateToLeft() {
        currAngle -= SINGLE_ROTATE_ANGLE
        postEvent(StartRotateEvent(currAngle, getRotatedCropBox()))
        globalEditBundle.enqueue(RotateRequest(-SINGLE_ROTATE_ANGLE), null)
    }

    fun onRotateToRight() {
        currAngle += SINGLE_ROTATE_ANGLE
        postEvent(StartRotateEvent(currAngle, getRotatedCropBox()))
        globalEditBundle.enqueue(RotateRequest(SINGLE_ROTATE_ANGLE), null)
    }

    private fun getRotatedCropBox(): RectF {
        return RectF(cropBoxRect).apply {
            getRotateMatrix().mapRect(this)
        }
    }

    fun getImageRect(): Rect {
        val rectF = RectF(globalEditBundle.drawHandler.currLimitRect).apply {
            getRotateMatrix().mapRect(this)
        }
        return Rect().apply {
            rectF.round(this)
        }
    }

    private fun getRotateMatrix(): Matrix {
        val centerPoint = globalEditBundle.getContainerCenterPoint()
        return Matrix().apply {
            postRotate(currAngle, centerPoint.x, centerPoint.y)
        }
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
        currMirrot = mirrorModel
        globalEditBundle.enqueue(MirrorRequest(mirrorModel), null)
    }

    fun hasRotateChange(): Boolean {
        return currAngle % ROTATE_CYCLE != 0f
    }

    fun hasMirrorChange(): Boolean {
        currMirrot ?: return false
        return currMirrot == MirrorModel.RIGHT || currMirrot == MirrorModel.BOTTOM
    }

    private fun postEvent(event: Any) {
        globalEditBundle.postEvent(event)
    }

    fun release() {
        resetCropState()
    }

    fun resetCropState() {
        currAngle = 0f
        cropBoxRect.setEmpty()
    }

}