package com.onyx.gallery.viewmodel

import androidx.lifecycle.MutableLiveData
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.gallery.action.erase.EraseAction
import com.onyx.gallery.action.erase.UpdateEraseAction
import com.onyx.gallery.action.shape.ShapeChangeAction
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.handler.EraseModel
import com.onyx.gallery.handler.EraseWidth
import com.onyx.gallery.models.EraseArgs
import com.onyx.gallery.models.MenuAction
import com.onyx.gallery.models.MenuState
import com.onyx.gallery.utils.ExpandShapeFactory

/**
 * Created by Leung 2020/8/24 10:50
 **/
class EraseMenuViewModel(editBundle: EditBundle) : BaseMenuViewModel(editBundle) {

    val eraseModel = MutableLiveData<EraseModel>()
    val eraseWidth = MutableLiveData<EraseWidth>()
    val eraseWidthEnable = MutableLiveData<Boolean>()

    override fun onUpdateMenuState(menuState: MenuState) {
        eraseModel.value = menuState.eraseModel
        eraseWidth.value = menuState.eraseWidth
        eraseWidthEnable.value = menuState.eraseWidthEnable
    }

    override fun updateTouchHandler() {
        ShapeChangeAction(editBundle, ExpandShapeFactory.ERASE).execute(null)
    }

    override fun onSaveMenuState(menuState: MenuState) {
        menuState.eraseModel = eraseModel.value!!
        menuState.eraseWidth = eraseWidth.value!!
        menuState.eraseWidthEnable = eraseWidthEnable.value!!
    }

    fun onEraseWidthChange(eraseWidth: EraseWidth) {
        if (isTouching()) {
            return
        }
        if (!eraseWidthEnable.value!!) {
            return
        }
        this.eraseWidth.value = eraseWidth
        checkEraseWidthEnable()
        updateEraseTouchHander()
    }

    override fun onHandleMenu(action: MenuAction): Boolean {
        if (isTouching()) {
            return false
        }
        eraseModel.value = when (action) {
            MenuAction.ERASE_STROKES -> EraseModel.STROKES
            MenuAction.ERASE_REGION -> EraseModel.REGION
            MenuAction.ERASE_LAYER -> {
                eraseLayer()
                return true
            }
            else -> EraseModel.MOVE
        }
        checkEraseWidthEnable()
        updateEraseTouchHander()
        return super.onHandleMenu(action)
    }

    private fun checkEraseWidthEnable() {
        eraseWidthEnable.value = eraseModel.value == EraseModel.MOVE
    }

    private fun updateEraseTouchHander() {
        UpdateEraseAction(editBundle, eraseModel.value!!, eraseWidth.value!!).execute(null)
        ShapeChangeAction(editBundle, ExpandShapeFactory.ERASE).execute(null)
    }

    private fun eraseLayer() {
        val eraseArgs = EraseArgs(EraseArgs.DEFAULT_WIDTH, EraseModel.LAYER, TouchPointList())
        EraseAction(editBundle, eraseArgs).execute(null)
    }

}