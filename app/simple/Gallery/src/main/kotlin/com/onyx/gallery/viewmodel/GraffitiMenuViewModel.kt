package com.onyx.gallery.viewmodel

import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.lifecycle.MutableLiveData
import com.onyx.gallery.action.shape.ShapeChangeAction
import com.onyx.gallery.action.shape.StrokeColorChangeAction
import com.onyx.gallery.action.shape.StrokeWidthChangeAction
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.helpers.DrawArgs
import com.onyx.gallery.models.MenuAction
import com.onyx.gallery.models.MenuState

/**
 * Created by Leung on 2020/5/6
 */
class GraffitiMenuViewModel(editBundle: EditBundle) : BaseMenuViewModel(editBundle) {

    private val stepStrokeWidth = DrawArgs.stepStrokeWidth
    val maxStrokeWidth = DrawArgs.maxStrokeWidth
    val minStrokeWidth = DrawArgs.minStrokeWidth
    var currStrokeWidth = MutableLiveData<Int>(getStrokeWidth().toInt())

    val onChangeListener: OnSeekBarChangeListener by lazy { initOnSeekBarChangeListener() }
    val isSeekBarEnable = MutableLiveData(true)

    override fun onUpdateMenuState(menuState: MenuState) {
        currStrokeWidth.value = menuState.storeWidth.toInt()
        onSelectColor(getMenuActionByColor(menuState.storecColor))
        onSelectShape(getMenuActionByShapeType(menuState.shapeType))
    }

    override fun onSaveMenuState(menuState: MenuState) {
        menuState.storeWidth = currStrokeWidth.value!!.toFloat()
        menuState.storecColor = getColorFromNoteMenuAction(selectColorAction.value!!)
        menuState.shapeType = getShapeTypeFromNoteMenuAction(selectShapeAction.value!!)
    }

    override fun onTouchChange(isTouching: Boolean) {
        super.onTouchChange(isTouching)
        isSeekBarEnable.value = !isTouching
    }

    private fun initOnSeekBarChangeListener(): OnSeekBarChangeListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, strokeWidth: Int, fromUser: Boolean) {
            currStrokeWidth.value = strokeWidth
            setStrokeWidth(strokeWidth.toFloat())
            StrokeWidthChangeAction(editBundle, strokeWidth.toFloat()).execute(null)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
        }
    }

    override fun onHandleMenu(action: MenuAction): Boolean {
        super.onHandleMenu(action)
        if (isTouching()) {
            return false
        }
        when (action) {
            MenuAction.SCRIBBLE_BRUSH,
            MenuAction.SCRIBBLE_CIRCLE,
            MenuAction.SCRIBBLE_RECTANGLE,
            MenuAction.SCRIBBLE_TRIANGLE,
            MenuAction.SCRIBBLE_DASH_LINE,
            MenuAction.SCRIBBLE_WAVE_LINE,
            MenuAction.SCRIBBLE_ARROW_LINE,
            MenuAction.SCRIBBLE_LINE -> onSelectShape(action)
            MenuAction.NOTE_COLOR_BLACK,
            MenuAction.NOTE_COLOR_DARK_GREY,
            MenuAction.NOTE_COLOR_MEDIUM_GREY,
            MenuAction.NOTE_COLOR_LIGHT_GREY,
            MenuAction.NOTE_COLOR_WHITE,
            MenuAction.NOTE_COLOR_RED,
            MenuAction.NOTE_COLOR_GREEN,
            MenuAction.NOTE_COLOR_BLUE -> onSelectColor(action)
            MenuAction.STROKE_WIDTH_ADDITION -> onStrokeWidthAdd()
            MenuAction.STROKE_WIDTH_SUBTRACTION -> onStrokeWidthSub()
            else -> return false
        }
        return true
    }

    private fun onSelectColor(action: MenuAction) {
        selectColorAction.value = action
        StrokeColorChangeAction(editBundle, getColorFromNoteMenuAction(action)).execute(null)
    }

    private fun onSelectShape(action: MenuAction) {
        selectShapeAction.value = action
        ShapeChangeAction(editBundle, getShapeTypeFromNoteMenuAction(action)).execute(null)
    }

    private fun onStrokeWidthAdd() {
        currStrokeWidth.run {
            value = value?.plus(stepStrokeWidth)?.coerceAtMost(maxStrokeWidth)
            setStrokeWidth(value!!.toFloat())
        }
    }

    private fun onStrokeWidthSub() = currStrokeWidth.run { value = value?.minus(stepStrokeWidth)?.coerceAtLeast(minStrokeWidth) }

}