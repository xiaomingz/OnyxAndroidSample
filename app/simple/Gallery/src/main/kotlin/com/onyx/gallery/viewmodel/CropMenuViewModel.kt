package com.onyx.gallery.viewmodel

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.PointF
import android.graphics.RectF
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import com.onyx.gallery.event.ui.UpdateCropRectEvent
import com.onyx.gallery.handler.MirrorModel
import com.onyx.gallery.handler.touch.TouchHandlerType
import com.onyx.gallery.models.MenuAction
import com.onyx.gallery.request.transform.MirrorRequest
import com.onyx.gallery.request.transform.RotateRequest

/**
 * Created by Leung on 2020/6/8
 */
class CropMenuViewModel : BaseMenuViewModel() {

    var rotateAngle = 90f
    val handler = Handler()
    val xAxisMirror = MutableLiveData(MirrorModel.LEFT)
    val yAxisMirror = MutableLiveData(MirrorModel.TOP)
    var cropAction = MutableLiveData(MenuAction.CROP_CUSTOMIZE)

    override fun updateTouchHandler() {
        globalEditBundle.touchHandlerManager.activateHandler(TouchHandlerType.CROP)
        globalEditBundle.drawHandler.setRawDrawingRenderEnabled(false)
        handler.postDelayed({
            onHandleMenu(cropAction.value!!)
        }, 500)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onHandleMenu(action: MenuAction): Boolean {
        when (action) {
            MenuAction.CROP_CUSTOMIZE -> onCropChange_customize()
            MenuAction.CROP_1_1 -> onCropChange_1_1()
            MenuAction.CROP_4_3 -> onCropChange_4_3()
            MenuAction.CROP_3_4 -> onCropChange_3_4()
            MenuAction.CROP_16_9_h -> onCropChange_h_16_9()
            MenuAction.CROP_16_9_v -> onCropChange_v_16_9()
            MenuAction.ROTATE_LEFT -> onRotateToLeft()
            MenuAction.ROTATE_RIGHT -> onRotateToRight()
            MenuAction.MIRROR_LEFT,
            MenuAction.MIRROR_RIGHT -> onXAxisChange()
            MenuAction.MIRROR_TOP,
            MenuAction.MIRROR_BOTTOM -> onYAxisChange()
            else -> return super.onHandleMenu(action)
        }
        return true
    }

    private fun onCropChange_customize() {
        val surfaceView = globalEditBundle.drawHandler.surfaceView ?: return
        val imageBitmap = globalEditBundle.drawHandler.getImageBitmap() ?: return
        val width: Int = surfaceView.width
        val height: Int = surfaceView.height
        val centerPoint = Point(width / 2, height / 2)
        val offset = Math.min(imageBitmap.width / 4, imageBitmap.height / 4).toFloat()
        val cropRect = RectF(centerPoint.x - offset, centerPoint.y - offset, centerPoint.x + offset, centerPoint.y + offset)
        postEvent(UpdateCropRectEvent(cropRect))
        cropAction.value = MenuAction.CROP_CUSTOMIZE
    }

    private fun onCropChange_1_1() {
        val currLimitRect = globalEditBundle.drawHandler.currLimitRect
        var cropRect = RectF(currLimitRect)
        postEvent(UpdateCropRectEvent(cropRect))
        cropAction.value = MenuAction.CROP_1_1
    }

    private fun onCropChange_4_3() {
        val currLimitRect = globalEditBundle.drawHandler.currLimitRect
        val width = currLimitRect.width()
        val height = currLimitRect.height()
        var cropWidth = width
        var cropHeight = 3 * cropWidth / 4
        var cropRect = RectF(currLimitRect.left.toFloat(), currLimitRect.top.toFloat(),
                currLimitRect.left + cropWidth.toFloat(),
                currLimitRect.top + cropHeight.toFloat())
        postEvent(UpdateCropRectEvent(cropRect))
        cropAction.value = MenuAction.CROP_4_3
    }

    private fun onCropChange_3_4() {
        val currLimitRect = globalEditBundle.drawHandler.currLimitRect
        val width = currLimitRect.width()
        val height = currLimitRect.height()
        var cropHeight = height
        var cropWidth = 3 * cropHeight / 4
        var cropRect = RectF(currLimitRect.left.toFloat(), currLimitRect.top.toFloat(),
                currLimitRect.left + cropWidth.toFloat(),
                currLimitRect.top + cropHeight.toFloat())
        postEvent(UpdateCropRectEvent(cropRect))
        cropAction.value = MenuAction.CROP_3_4
    }

    private fun onCropChange_h_16_9() {
        val currLimitRect = globalEditBundle.drawHandler.currLimitRect
        val width = currLimitRect.width()
        val height = currLimitRect.height()
        val cropWidth = width
        val cropHeight = 9 * width / 16
        var cropRect = RectF(currLimitRect.left.toFloat(), currLimitRect.top.toFloat(),
                currLimitRect.left + cropWidth.toFloat(),
                currLimitRect.top + cropHeight.toFloat())
        postEvent(UpdateCropRectEvent(cropRect))
        cropAction.value = MenuAction.CROP_16_9_h
    }

    private fun onCropChange_v_16_9() {
        val currLimitRect = globalEditBundle.drawHandler.currLimitRect
        val width = currLimitRect.width()
        val height = currLimitRect.height()
        val cropHeight = height
        val cropWidth = 9 * height / 16
        var cropRect = RectF(currLimitRect.left.toFloat(), currLimitRect.top.toFloat(),
                currLimitRect.left + cropWidth.toFloat(),
                currLimitRect.top + cropHeight.toFloat())
        postEvent(UpdateCropRectEvent(cropRect))
        cropAction.value = MenuAction.CROP_16_9_v
    }

    private fun getImageBitmap(): Bitmap = globalEditBundle.drawHandler.getImageBitmap()!!

    private fun onRotateToLeft() {
        val centerPoint = getCenterPoint()
        globalEditBundle.enqueue(RotateRequest(-rotateAngle, centerPoint), null)
    }

    private fun onRotateToRight() {
        val centerPoint = getCenterPoint()
        globalEditBundle.enqueue(RotateRequest(rotateAngle, centerPoint), null)
    }

    private fun onXAxisChange() {
        val mirrorModel = if (xAxisMirror.value == MirrorModel.LEFT) {
            MirrorModel.RIGHT
        } else {
            MirrorModel.LEFT
        }
        xAxisMirror.value = mirrorModel
        onMirror(mirrorModel)
    }

    private fun onYAxisChange() {
        val mirrorModel = if (yAxisMirror.value == MirrorModel.TOP) {
            MirrorModel.BOTTOM
        } else {
            MirrorModel.TOP
        }
        yAxisMirror.value = mirrorModel
        onMirror(mirrorModel)
    }

    private fun onMirror(mirrorModel: MirrorModel) {
        globalEditBundle.enqueue(MirrorRequest(mirrorModel), null)
    }

    private fun getCenterPoint(): PointF {
        val surfaceView = globalEditBundle.drawHandler.surfaceView
        return PointF((surfaceView!!.width / 2).toFloat(), (surfaceView.height / 2).toFloat())
    }

}