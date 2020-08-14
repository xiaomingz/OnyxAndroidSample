package com.onyx.gallery.viewmodel

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import com.onyx.android.sdk.scribble.shape.ShapeFactory
import com.onyx.gallery.action.shape.ShapeChangeAction
import com.onyx.gallery.handler.CropHandler
import com.onyx.gallery.handler.MirrorModel
import com.onyx.gallery.models.MenuAction
import com.onyx.gallery.utils.ExpandShapeFactory

/**
 * Created by Leung on 2020/6/8
 */
class CropMenuViewModel : BaseMenuViewModel() {

    private val delayMillis = 300L
    val handler = Handler()
    val xAxisMirror = MutableLiveData(MirrorModel.LEFT)
    val yAxisMirror = MutableLiveData(MirrorModel.TOP)
    var cropAction = MutableLiveData(MenuAction.CROP_CUSTOMIZE)

    override fun updateTouchHandler() {
        ShapeChangeAction(ExpandShapeFactory.CROP).execute(null)
        cropAction.value?.let {
            handler.postDelayed({
                onHandleMenu(it)
            }, delayMillis)
        }
    }

    fun getCropHandler(): CropHandler = globalEditBundle.cropHandler

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onHandleMenu(action: MenuAction): Boolean {
        when (action) {
            MenuAction.CROP_CUSTOMIZE -> onCropChangeToCustomize()
            MenuAction.CROP_1_1 -> onCropChangeTo_1_1()
            MenuAction.CROP_4_3 -> onCropChangeTo_4_3()
            MenuAction.CROP_3_4 -> onCropChangeTo_3_4()
            MenuAction.CROP_16_9_HORIZONTAL -> onCropChangeToHorizontal_16_9()
            MenuAction.CROP_16_9_VERTICAL -> onCropChangeToVertical_16_9()
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

    private fun onCropChangeToCustomize() {
        getCropHandler().onCropChangeToCustomize()
        cropAction.value = MenuAction.CROP_CUSTOMIZE
    }

    private fun onCropChangeTo_1_1() {
        getCropHandler().onCropChangeTo_1_1()
        cropAction.value = MenuAction.CROP_1_1
    }

    private fun onCropChangeTo_4_3() {
        getCropHandler().onCropChangeTo_4_3()
        cropAction.value = MenuAction.CROP_4_3
    }

    private fun onCropChangeTo_3_4() {
        getCropHandler().onCropChangeTo_3_4()
        cropAction.value = MenuAction.CROP_3_4
    }

    private fun onCropChangeToHorizontal_16_9() {
        getCropHandler().onCropChangeToHorizontal_16_9()
        cropAction.value = MenuAction.CROP_16_9_HORIZONTAL
    }

    private fun onCropChangeToVertical_16_9() {
        getCropHandler().onCropChangeToVertical_16_9()
        cropAction.value = MenuAction.CROP_16_9_VERTICAL
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
        cropAction.value = null
    }

}