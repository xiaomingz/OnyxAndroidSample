package com.onyx.gallery.viewmodel

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import com.onyx.gallery.handler.CropHandler
import com.onyx.gallery.handler.MirrorModel
import com.onyx.gallery.handler.touch.TouchHandlerType
import com.onyx.gallery.models.MenuAction

/**
 * Created by Leung on 2020/6/8
 */
class CropMenuViewModel : BaseMenuViewModel() {

    val handler = Handler()
    val xAxisMirror = MutableLiveData(MirrorModel.LEFT)
    val yAxisMirror = MutableLiveData(MirrorModel.TOP)
    var cropAction = MutableLiveData<MenuAction>()

    override fun updateTouchHandler() {
        globalEditBundle.touchHandlerManager.activateHandler(TouchHandlerType.CROP)
        globalEditBundle.drawHandler.setRawDrawingRenderEnabled(false)
        cropAction.value?.let {
            handler.postDelayed({
                onHandleMenu(it)
            }, 300)
        }
    }

    fun getCropHandler(): CropHandler = globalEditBundle.cropHandler

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
        getCropHandler().onCropChange_customize()
        cropAction.value = MenuAction.CROP_CUSTOMIZE
    }

    private fun onCropChange_1_1() {
        getCropHandler().onCropChange_1_1()
        cropAction.value = MenuAction.CROP_1_1
    }

    private fun onCropChange_4_3() {
        getCropHandler().onCropChange_4_3()
        cropAction.value = MenuAction.CROP_4_3
    }

    private fun onCropChange_3_4() {
        getCropHandler().onCropChange_3_4()
        cropAction.value = MenuAction.CROP_3_4
    }

    private fun onCropChange_h_16_9() {
        getCropHandler().onCropChange_h_16_9()
        cropAction.value = MenuAction.CROP_16_9_h
    }

    private fun onCropChange_v_16_9() {
        getCropHandler().onCropChange_v_16_9()
        cropAction.value = MenuAction.CROP_16_9_v
    }

    private fun onRotateToLeft() {
        getCropHandler().onRotateToLeft()
    }

    private fun onRotateToRight() {
        getCropHandler().onRotateToRight()
    }

    private fun onXAxisChange() {
        xAxisMirror.value = getCropHandler().onXAxisChange()
    }

    private fun onYAxisChange() {
        yAxisMirror.value = getCropHandler().onYAxisChange()
    }

    fun resetCropState() {
        resetCropRect()
    }

    private fun resetCropRect() {
        cropAction.value = null
    }

}