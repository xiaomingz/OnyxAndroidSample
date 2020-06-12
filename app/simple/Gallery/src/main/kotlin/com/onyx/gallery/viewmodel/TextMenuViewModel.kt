package com.onyx.gallery.viewmodel

import android.widget.SeekBar
import androidx.lifecycle.MutableLiveData
import com.onyx.android.sdk.scribble.shape.ShapeFactory
import com.onyx.android.sdk.utils.ResManager
import com.onyx.gallery.R
import com.onyx.gallery.action.shape.ShapeChangeAction
import com.onyx.gallery.handler.InsertTextHandler
import com.onyx.gallery.models.MenuAction

/**
 * Created by Leung on 2020/6/5
 */
class TextMenuViewModel : BaseMenuViewModel() {

    var bold = MutableLiveData(false)
    var traditional = MutableLiveData(false)

    private val stepFontSize = ResManager.getAppContext().resources.getDimension(R.dimen.edit_text_shape_text_size_step).toInt()
    val maxFontSize = ResManager.getAppContext().resources.getDimension(R.dimen.edit_text_shape_text_size_max).toInt()
    val minFontSize = ResManager.getAppContext().resources.getDimension(R.dimen.edit_text_shape_text_size_min).toInt()
    val currFontSize = MutableLiveData(ResManager.getAppContext().resources.getDimension(R.dimen.edit_text_shape_text_size).toInt())
    val onChangeListener: SeekBar.OnSeekBarChangeListener by lazy { initOnSeekBarChangeListener() }

    override fun updateTouchHandler() {
        ShapeChangeAction().setShapeType(ShapeFactory.SHAPE_EDIT_TEXT_SHAPE).execute(null)
    }

    private fun initOnSeekBarChangeListener(): SeekBar.OnSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, fontSize: Int, fromUser: Boolean) {
            currFontSize.value = fontSize
            onFontSizeChange(fontSize.toFloat())
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
        }
    }

    override fun onHandleMenu(action: MenuAction): Boolean {
        super.onHandleMenu(action)
        when (action) {
            MenuAction.FONT_BOLD -> onFontBoldChange()
            MenuAction.NOTE_COLOR_BLACK,
            MenuAction.NOTE_COLOR_DARK_GREY,
            MenuAction.NOTE_COLOR_MEDIUM_GREY,
            MenuAction.NOTE_COLOR_LIGHT_GREY,
            MenuAction.NOTE_COLOR_WHITE,
            MenuAction.NOTE_COLOR_RED,
            MenuAction.NOTE_COLOR_GREEN,
            MenuAction.NOTE_COLOR_BLUE -> onSelectColor(action)
            MenuAction.FONT_SIZE_SUBTRACTION -> onStrokeWidthSub()
            MenuAction.FONT_SIZE_ADDITION -> onStrokeWidthAdd()
            else -> return false
        }
        return true
    }

    private fun onStrokeWidthAdd() = currFontSize.run { value = value?.plus(stepFontSize)?.coerceAtMost(maxFontSize) }

    private fun onStrokeWidthSub() = currFontSize.run { value = value?.minus(stepFontSize)?.coerceAtLeast(minFontSize) }

    private fun onFontSizeChange(textSize: Float) {
        getInsertTextHandler().onTextSizeEvent(textSize)
    }

    private fun onFontBoldChange() {
        bold.value = !(bold.value)!!
        getInsertTextHandler().onTextBoldEvent(bold.value!!)
    }

    private fun onSelectColor(action: MenuAction) {
        selectColorAction.value = action
        getInsertTextHandler().onChangeColorEvent(getColorFromNoteMenuAction(action))
    }

    private fun getInsertTextHandler(): InsertTextHandler = globalEditBundle.insertTextHandler


}