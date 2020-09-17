package com.onyx.gallery.viewmodel

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import com.onyx.gallery.action.shape.ShapeChangeAction
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.event.ui.ResetCropBoxEvent
import com.onyx.gallery.event.ui.ShowSaveCropMenuEvent
import com.onyx.gallery.handler.CropHandler
import com.onyx.gallery.handler.MirrorModel
import com.onyx.gallery.models.MenuAction
import com.onyx.gallery.models.MenuState
import com.onyx.gallery.utils.ExpandShapeFactory
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Leung on 2020/6/8
 */
class CropMenuViewModel(editBundle: EditBundle) : BaseMenuViewModel(editBundle) {

    private val delayMillis = 300L
    val handler = Handler()
    val xAxisMirror = MutableLiveData(MirrorModel.LEFT)
    val yAxisMirror = MutableLiveData(MirrorModel.TOP)
    var cropAction = MutableLiveData<MenuAction>(getCropRectType())

    override fun onUpdateMenuState(menuState: MenuState) {
        cropAction.value = menuState.cropRectType
    }

    override fun updateTouchHandler() {
        ShapeChangeAction(editBundle, ExpandShapeFactory.CROP).execute(null)
        if (cropAction.value == null) {
            cropAction.value = MenuAction.CROP_CUSTOMIZE
        }
        cropAction.value?.let {
            handler.postDelayed({
                onHandleMenu(it)
            }, delayMillis)
        }
    }

    override fun onSaveMenuState(menuState: MenuState) {
        setCropRectType(cropAction.value!!)
    }

    fun getCropHandler(): CropHandler = editBundle.cropHandler

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onResetCropBoxEvent(event: ResetCropBoxEvent) {
        onHandleMenu(event.cropRectType)
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
        showSaveCropMenu()
        return true
    }

    private fun showSaveCropMenu() {
        postEvent(ShowSaveCropMenuEvent())
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
        val cropHandler = getCropHandler()
        cropHandler.onRotateToLeft()
        resetMirror()
    }

    private fun onRotateToRight() {
        val cropHandler = getCropHandler()
        cropHandler.onRotateToRight()
        resetMirror()
    }

    private fun onXAxisChange() {
        val cropHandler = getCropHandler()
        xAxisMirror.value = cropHandler.onXAxisChange()
        resetRotate()
    }

    private fun onYAxisChange() {
        val cropHandler = getCropHandler()
        yAxisMirror.value = cropHandler.onYAxisChange()
        resetRotate()
    }

    fun resetCropState() {
        resetRotate()
    }

    private fun resetRotate() {
        cropAction.value = null
        getCropHandler().resetRotate()
    }

    private fun resetMirror() {
        yAxisMirror.value = null
        getCropHandler().resetMirror()
    }

}