package com.onyx.gallery.viewmodel

import android.graphics.PointF
import androidx.lifecycle.MutableLiveData
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
            MenuAction.CROP_CUSTOMIZE,
            MenuAction.CROP_1_1,
            MenuAction.CROP_4_3,
            MenuAction.CROP_3_4,
            MenuAction.CROP_16_9_h,
            MenuAction.CROP_16_9_v -> onCropTypeChange(action)
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

    private fun onCropTypeChange(action: MenuAction) {

    }

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