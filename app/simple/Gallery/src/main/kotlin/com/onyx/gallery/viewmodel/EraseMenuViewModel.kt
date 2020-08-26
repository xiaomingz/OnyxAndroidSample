package com.onyx.gallery.viewmodel

import androidx.lifecycle.MutableLiveData
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.gallery.action.erase.EraseAction
import com.onyx.gallery.action.erase.UpdateEraseAction
import com.onyx.gallery.action.shape.ShapeChangeAction
import com.onyx.gallery.handler.EraseModel
import com.onyx.gallery.handler.EraseWidth
import com.onyx.gallery.models.EraseArgs
import com.onyx.gallery.models.MenuAction
import com.onyx.gallery.utils.ExpandShapeFactory

/**
 * Created by Leung 2020/8/24 10:50
 **/
class EraseMenuViewModel : BaseMenuViewModel() {

    val eraseModel = MutableLiveData(EraseModel.STROKES)
    val eraseWidth = MutableLiveData(EraseWidth.ERASER_WIDTH_2)
    val eraseWidthEnable = MutableLiveData(false)

    override fun updateTouchHandler() {
        ShapeChangeAction(ExpandShapeFactory.ERASE).execute(null)
    }

    fun onEraseWidthChange(eraseWidth: EraseWidth) {
        if (!eraseWidthEnable.value!!) {
            return
        }
        this.eraseWidth.value = eraseWidth
        checkEraseWidthEnable()
        updateEraseTouchHander()
    }

    override fun onHandleMenu(action: MenuAction): Boolean {
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
        UpdateEraseAction(eraseModel.value!!, eraseWidth.value!!).execute(null)
    }

    private fun eraseLayer() {
        val eraseArgs = EraseArgs(EraseArgs.DEFAULT_WIDTH, EraseModel.LAYER, TouchPointList())
        EraseAction(eraseArgs).execute(null)
    }

}