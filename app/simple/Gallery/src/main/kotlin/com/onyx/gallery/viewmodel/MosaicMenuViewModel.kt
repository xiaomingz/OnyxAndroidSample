package com.onyx.gallery.viewmodel

import android.widget.SeekBar
import androidx.lifecycle.MutableLiveData
import com.onyx.gallery.action.shape.ShapeChangeAction
import com.onyx.gallery.action.shape.StrokeWidthChangeAction
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.helpers.DrawArgs
import com.onyx.gallery.models.MenuAction
import com.onyx.gallery.models.MenuState
import com.onyx.gallery.utils.ExpandShapeFactory

/**
 * Created by Leung on 2020/6/8
 */
class MosaicMenuViewModel(editBundle: EditBundle) : BaseMenuViewModel(editBundle) {
    private val stepStrokeWidth = DrawArgs.stepStrokeWidth
    val maxStrokeWidth = DrawArgs.maxStrokeWidth
    val minStrokeWidth = DrawArgs.minStrokeWidth
    val currStrokeWidth = MutableLiveData<Int>(getStrokeWidth().toInt())
    val onChangeListener: SeekBar.OnSeekBarChangeListener by lazy { initOnSeekBarChangeListener() }
    val isSeekBarEnable = MutableLiveData(true)

    override fun onTouchChange(isTouching: Boolean) {
        super.onTouchChange(isTouching)
        isSeekBarEnable.value = !isTouching
    }

    override fun onUpdateMenuState(menuState: MenuState) {
        currStrokeWidth.value = menuState.storeWidth.toInt()
    }

    override fun onSaveMenuState(menuState: MenuState) {
        menuState.storeWidth = currStrokeWidth.value!!.toFloat()
    }

    override fun updateTouchHandler() {
        ShapeChangeAction(editBundle, ExpandShapeFactory.SHAPE_MOSAIC).execute(null)
    }

    private fun initOnSeekBarChangeListener(): SeekBar.OnSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, strokeWidth: Int, fromUser: Boolean) {
            currStrokeWidth.value = strokeWidth
            StrokeWidthChangeAction(editBundle, strokeWidth.toFloat()).execute(null)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
        }
    }

    override fun onHandleMenu(action: MenuAction): Boolean {
        if (isTouching()) {
            return false
        }
        when (action) {
            MenuAction.STROKE_WIDTH_ADDITION -> onStrokeWidthAdd()
            MenuAction.STROKE_WIDTH_SUBTRACTION -> onStrokeWidthSub()
            else -> return super.onHandleMenu(action)
        }
        return true
    }

    private fun onStrokeWidthAdd() = currStrokeWidth.run { value = value?.plus(stepStrokeWidth)?.coerceAtMost(maxStrokeWidth) }

    private fun onStrokeWidthSub() = currStrokeWidth.run { value = value?.minus(stepStrokeWidth)?.coerceAtLeast(minStrokeWidth) }
}