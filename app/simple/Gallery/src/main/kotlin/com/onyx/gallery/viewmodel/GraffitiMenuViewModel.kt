package com.onyx.gallery.viewmodel

import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.lifecycle.MutableLiveData
import com.onyx.gallery.action.StrokeColorChangeAction
import com.onyx.gallery.action.shape.ShapeChangeAction
import com.onyx.gallery.helpers.DrawArgs
import com.onyx.gallery.models.MenuAction

/**
 * Created by Leung on 2020/5/6
 */
class GraffitiMenuViewModel : BaseMenuViewModel() {

    val seekBarMax = 20
    private val stepSize = 1
    private val minStrokeWidth = DrawArgs.minStrokeWidth
    val currStrokeWidth: MutableLiveData<Int> = MutableLiveData(DrawArgs.minStrokeWidth.toInt())
    val onChangeListener: OnSeekBarChangeListener by lazy { initOnSeekBarChangeListener() }

    private fun initOnSeekBarChangeListener(): OnSeekBarChangeListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            currStrokeWidth.value = progress.coerceAtMost(seekBarMax).coerceAtLeast(minStrokeWidth.toInt())
            globalEditBundle.drawHandler.setStrokeWidth(progress.toFloat())
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
        }
    }

    override fun onHandleMenu(action: MenuAction): Boolean {
        super.onHandleMenu(action)
        when (action) {
            MenuAction.SCRIBBLE_BRUSH,
            MenuAction.SCRIBBLE_CIRCLE,
            MenuAction.SCRIBBLE_RECTANGLE,
            MenuAction.SCRIBBLE_TRIANGLE,
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

    private fun onSelectColor(action: MenuAction) = setSelectColorAction(action)

    private fun onSelectShape(action: MenuAction) = setSelectShapeAction(action)

    private fun setSelectShapeAction(action: MenuAction) {
        selectShapeAction.value = action
        ShapeChangeAction().setShapeType(getShapeTypeFromNoteMenuAction(action)).execute(null)
    }

    private fun setSelectColorAction(action: MenuAction) {
        selectColorAction.value = action
        StrokeColorChangeAction(getColorFromNoteMenuAction(action)).execute(null)
    }

    private fun onStrokeWidthAdd() = currStrokeWidth.run { value = value?.plus(stepSize)?.coerceAtMost(seekBarMax) }

    private fun onStrokeWidthSub() = currStrokeWidth.run { value = value?.minus(stepSize)?.coerceAtLeast(minStrokeWidth.toInt()) }

}