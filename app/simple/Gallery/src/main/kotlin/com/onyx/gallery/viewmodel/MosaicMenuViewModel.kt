package com.onyx.gallery.viewmodel

import android.widget.SeekBar
import androidx.lifecycle.MutableLiveData
import com.onyx.gallery.action.shape.ShapeChangeAction
import com.onyx.gallery.action.shape.StrokeWidthChangeAction
import com.onyx.gallery.helpers.DrawArgs
import com.onyx.gallery.models.MenuAction
import com.onyx.gallery.utils.ExpandShapeFactory

/**
 * Created by Leung on 2020/6/8
 */
class MosaicMenuViewModel : BaseMenuViewModel() {
    private val stepStrokeWidth = DrawArgs.stepStrokeWidth
    val maxStrokeWidth = DrawArgs.maxStrokeWidth
    val minStrokeWidth = DrawArgs.minStrokeWidth
    val currStrokeWidth: MutableLiveData<Int> = MutableLiveData(DrawArgs.defaultStrokeWidth)
    val onChangeListener: SeekBar.OnSeekBarChangeListener by lazy { initOnSeekBarChangeListener() }

    override fun updateTouchHandler() {
        ShapeChangeAction(ExpandShapeFactory.SHAP_MOSAIC).execute(null)
    }

    private fun initOnSeekBarChangeListener(): SeekBar.OnSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, strokeWidth: Int, fromUser: Boolean) {
            currStrokeWidth.value = strokeWidth
            StrokeWidthChangeAction(strokeWidth.toFloat()).execute(null)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
        }
    }

    override fun onHandleMenu(action: MenuAction): Boolean {
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