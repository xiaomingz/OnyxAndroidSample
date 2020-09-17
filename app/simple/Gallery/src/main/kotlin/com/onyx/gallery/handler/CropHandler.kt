package com.onyx.gallery.handler

import android.graphics.Point
import android.graphics.PointF
import android.graphics.RectF
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.action.RotateAction
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.event.ui.ResetCropBoxEvent
import com.onyx.gallery.event.ui.UpdateCropRectEvent
import com.onyx.gallery.models.MenuAction
import com.onyx.gallery.request.transform.MirrorRequest
import com.onyx.gallery.views.crop.CropImageView

/**
 * Created by Leung on 2020/6/29
 */
class CropHandler(val editBundle: EditBundle) : CropImageView.OnCropRectChange {

    companion object {
        const val SINGLE_ROTATE_ANGLE = 90f
        const val ROTATE_CYCLE = 360f
    }

    var cropBoxRect = RectF()
    var currAngle = 0f
    var xAxisMirror = MirrorModel.LEFT
    var yAxisMirror = MirrorModel.TOP
    var currMirrot: MirrorModel? = null
    var cropRectType = MenuAction.CROP_CUSTOMIZE

    override fun onCropRectChange(newCropRect: RectF) {
        cropBoxRect.set(newCropRect)
    }

    fun onCropChangeToCustomize() {
        val drawHandler = editBundle.drawHandler
        val surfaceRect = drawHandler.surfaceRect
        val limitRect = drawHandler.currLimitRect
        val width = limitRect.width()
        val height = limitRect.height()
        val centerPoint = Point(surfaceRect.centerX(), surfaceRect.centerY())
        val offset = Math.min(width / 4, height / 4).toFloat()
        cropBoxRect.set(centerPoint.x - offset, centerPoint.y - offset, centerPoint.x + offset, centerPoint.y + offset)
        postEvent(UpdateCropRectEvent(cropBoxRect, false))
        cropRectType = MenuAction.CROP_CUSTOMIZE
    }

    fun onCropChangeTo_1_1() {
        val surfaceRect = editBundle.drawHandler.surfaceRect
        val currLimitRect = editBundle.drawHandler.currLimitRect
        val width = surfaceRect.width()
        val height = surfaceRect.height()
        var size = currLimitRect.width()
        if (currLimitRect.height() < currLimitRect.width()) {
            size = currLimitRect.height()
        }
        val radius = size / 2f
        val centerPoint = PointF(width / 2f, height / 2f)
        cropBoxRect.set(centerPoint.x - radius, centerPoint.y - radius, centerPoint.x + radius, centerPoint.y + radius)
        postEvent(UpdateCropRectEvent(cropBoxRect, true))
        cropRectType = MenuAction.CROP_1_1
    }

    fun onCropChangeTo_4_3() {
        val currLimitRect = editBundle.drawHandler.currLimitRect
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
        postEvent(UpdateCropRectEvent(cropBoxRect, true))
        cropRectType = MenuAction.CROP_4_3
    }

    fun onCropChangeTo_3_4() {
        val currLimitRect = editBundle.drawHandler.currLimitRect
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
        postEvent(UpdateCropRectEvent(cropBoxRect, true))
        cropRectType = MenuAction.CROP_3_4
    }

    fun onCropChangeToHorizontal_16_9() {
        val currLimitRect = editBundle.drawHandler.currLimitRect
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
        postEvent(UpdateCropRectEvent(cropBoxRect, true))
        cropRectType = MenuAction.CROP_16_9_HORIZONTAL
    }

    fun onCropChangeToVertical_16_9() {
        val currLimitRect = editBundle.drawHandler.currLimitRect
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
        postEvent(UpdateCropRectEvent(cropBoxRect, true))
        cropRectType = MenuAction.CROP_16_9_VERTICAL
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

    fun onRotateToLeft() {
        currAngle -= SINGLE_ROTATE_ANGLE
        RotateAction(editBundle, currAngle, -SINGLE_ROTATE_ANGLE).execute(object : RxCallback<RxRequest>() {
            override fun onNext(rxRequest: RxRequest) {
                resetCropBox()
            }
        })
    }

    fun onRotateToRight() {
        currAngle += SINGLE_ROTATE_ANGLE
        RotateAction(editBundle, currAngle, SINGLE_ROTATE_ANGLE).execute(object : RxCallback<RxRequest>() {
            override fun onNext(rxRequest: RxRequest) {
                resetCropBox()
            }
        })
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
        editBundle.enqueue(MirrorRequest(editBundle, mirrorModel), object : RxCallback<RxRequest>() {
            override fun onNext(rxRequest: RxRequest) {
                resetCropBox()
            }
        })
    }

    fun hasRotateChange(): Boolean {
        return currAngle % ROTATE_CYCLE != 0f
    }

    fun hasMirrorChange(): Boolean {
        currMirrot ?: return false
        return currMirrot == MirrorModel.RIGHT || currMirrot == MirrorModel.BOTTOM
    }

    fun hasModify(): Boolean {
        return hasRotateChange() || hasMirrorChange()
    }

    private fun postEvent(event: Any) {
        editBundle.postEvent(event)
    }

    fun release() {
        resetCropState()
    }

    fun resetCropState() {
        currAngle = 0f
        currMirrot = null
        cropBoxRect.setEmpty()
    }

    fun resetRotate() {
        currAngle = 0f
    }

    fun resetMirror() {
        currMirrot = null
    }

    fun resetCropBox() {
        postEvent(ResetCropBoxEvent(cropRectType))
    }

}