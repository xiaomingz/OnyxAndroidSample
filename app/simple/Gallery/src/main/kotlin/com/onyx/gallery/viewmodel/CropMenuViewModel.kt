package com.onyx.gallery.viewmodel

import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.RectF
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
    val xAxisMirror = MutableLiveData(MirrorModel.LEFT)
    val yAxisMirror = MutableLiveData(MirrorModel.TOP)
    var cropAction = MutableLiveData(MenuAction.CROP_CUSTOMIZE)

    override fun updateTouchHandler() {
        globalEditBundle.touchHandlerManager.activateHandler(TouchHandlerType.CROP)
        globalEditBundle.drawHandler.setRawDrawingRenderEnabled(false)
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
        val imageBitmap = getImageBitmap()
        val width: Int = imageBitmap.width
        val height: Int = imageBitmap.height
        val cropWidth = Math.min(width / 2, height / 2)
        val cropHeight = cropWidth
        val x = (width - cropWidth) / 2
        val y = (height - cropHeight) / 2
        val cropRect = RectF(x.toFloat(), y.toFloat(), (x + cropWidth).toFloat(), (y + cropHeight).toFloat())
        postEvent(UpdateCropRectEvent(cropRect))
        cropAction.value = MenuAction.CROP_CUSTOMIZE
    }

    private fun onCropChange_1_1() {
        val imageBitmap = getImageBitmap()
        val width = imageBitmap.width
        val height = imageBitmap.height
        val cropWidth = Math.min(width, height)
        val cropHeight = cropWidth
        var cropRect = RectF(0f, 0f, cropWidth.toFloat(), cropHeight.toFloat())
        postEvent(UpdateCropRectEvent(cropRect))
        cropAction.value = MenuAction.CROP_1_1
    }

    private fun onCropChange_4_3() {
        val imageBitmap = getImageBitmap()
        val width = imageBitmap.width
        val height = imageBitmap.height
        val cropWidth = 4 * height / 3
        val cropHeight = 3 * width / 4
        var cropRect = RectF(0f, 0f, cropWidth.toFloat(), cropHeight.toFloat())
        postEvent(UpdateCropRectEvent(cropRect))
        cropAction.value = MenuAction.CROP_4_3
    }

    private fun onCropChange_3_4() {
        val imageBitmap = getImageBitmap()
        val width = imageBitmap.width
        val height = imageBitmap.height
        val cropWidth = 3 * height / 4
        val cropHeight = 4 * width / 3
        var cropRect = RectF(0f, 0f, cropWidth.toFloat(), cropHeight.toFloat())
        postEvent(UpdateCropRectEvent(cropRect))
        cropAction.value = MenuAction.CROP_3_4
    }

    private fun onCropChange_h_16_9() {
        val imageBitmap = getImageBitmap()
        val width = imageBitmap.height
        val height = imageBitmap.width
        val cropWidth = 16 * height / 9
        val cropHeight = 9 * width / 16
        var cropRect = RectF(0f, 0f, cropHeight.toFloat(), cropWidth.toFloat())
        postEvent(UpdateCropRectEvent(cropRect))
        cropAction.value = MenuAction.CROP_16_9_h
    }

    private fun onCropChange_v_16_9() {
        val imageBitmap = getImageBitmap()
        val width = imageBitmap.height
        val height = imageBitmap.width
        val cropWidth = 16 * height / 9
        val cropHeight = 9 * width / 16
        var cropRect = RectF(0f, 0f, cropWidth.toFloat(), cropHeight.toFloat())
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