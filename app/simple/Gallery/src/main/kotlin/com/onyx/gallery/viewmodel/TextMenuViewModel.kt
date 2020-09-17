package com.onyx.gallery.viewmodel

import android.widget.SeekBar
import androidx.lifecycle.MutableLiveData
import com.onyx.android.sdk.data.FontInfo
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.scribble.shape.ShapeFactory
import com.onyx.android.sdk.utils.ResManager
import com.onyx.gallery.R
import com.onyx.gallery.action.shape.ShapeChangeAction
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.event.ui.ShowFontSelectMenuEvent
import com.onyx.gallery.handler.InsertTextHandler
import com.onyx.gallery.models.MenuAction
import com.onyx.gallery.models.MenuState
import com.onyx.gallery.request.GetFontsRequest


/**
 * Created by Leung on 2020/6/5
 */
class TextMenuViewModel(editBundle: EditBundle) : BaseMenuViewModel(editBundle) {

    var showSubMenu = MutableLiveData(false)

    var bold = MutableLiveData(getTextBold())
    var indentation = MutableLiveData(getTextIndentation())
    var traditional = MutableLiveData(getTextTraditional())

    var currFont = MutableLiveData(ResManager.getString(R.string.loading))
    var currFontInfo: FontInfo? = null

    private val stepFontSize = ResManager.getAppContext().resources.getDimension(R.dimen.edit_text_shape_text_size_step).toInt()
    val maxFontSize = ResManager.getAppContext().resources.getDimension(R.dimen.edit_text_shape_text_size_max).toInt()
    val minFontSize = ResManager.getAppContext().resources.getDimension(R.dimen.edit_text_shape_text_size_min).toInt()
    val currFontSize = MutableLiveData<Int>(getTextSize().toInt())
    val onChangeListener: SeekBar.OnSeekBarChangeListener by lazy { initOnSeekBarChangeListener() }

    override fun onUpdateMenuState(menuState: MenuState) {
        currFontSize.value = menuState.textSize.toInt()
        onSelectColor(getMenuActionByColor(menuState.storecColor))
        menuState.typeface?.run {
            currFont.value = name
            currFontInfo = this
        }
        bold.value = menuState.bold
        indentation.value = menuState.indentation
        traditional.value = menuState.traditional
        loadFontList(currFont.value!!)
    }

    override fun onSaveMenuState(menuState: MenuState) {
        menuState.textSize = currFontSize.value!!.toFloat()
        menuState.typeface = currFontInfo
        menuState.textColor = getColorFromNoteMenuAction(selectColorAction.value!!)
        menuState.bold = bold.value!!
        menuState.indentation = indentation.value!!
        menuState.traditional = traditional.value!!
    }

    override fun updateTouchHandler() {
        ShapeChangeAction(editBundle, ShapeFactory.SHAPE_EDIT_TEXT_SHAPE).execute(null)
    }

    private fun loadFontList(currFontName: String) {
        editBundle.enqueue(GetFontsRequest(editBundle, currFontName), object : RxCallback<GetFontsRequest>() {
            override fun onNext(getFontsRequest: GetFontsRequest) {
                if (currFontInfo != null) {
                    return
                }
                getFontsRequest.detDefaultFont?.run {
                    currFont.value = name
                    currFontInfo = this
                }
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                currFont.value = ResManager.getString(R.string.default_font)
            }
        })
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
            MenuAction.FONT_SELECT -> onFontSelect()
            MenuAction.INDENTATION -> onTextIndentation()
            MenuAction.TRADITIONAL -> onTextTraditional()
            else -> return false
        }
        return true
    }

    private fun onFontSelect() {
        val isShowSubMenu = showSubMenu.value!!
        if (!isShowSubMenu) {
            showSubMenu.value = true
        }
        postEvent(ShowFontSelectMenuEvent())
    }

    private fun onStrokeWidthAdd() = currFontSize.run { value = value?.plus(stepFontSize)?.coerceAtMost(maxFontSize) }

    private fun onStrokeWidthSub() = currFontSize.run { value = value?.minus(stepFontSize)?.coerceAtLeast(minFontSize) }

    private fun onFontSizeChange(textSize: Float) {
        getInsertTextHandler().onTextSizeEvent(textSize)
    }

    private fun onFontBoldChange() {
        bold.value = (!(bold.value)!!).apply {
            getInsertTextHandler().onTextBoldEvent(this)
        }
    }

    private fun onSelectColor(action: MenuAction) {
        selectColorAction.value = action
        getInsertTextHandler().onChangeColorEvent(getColorFromNoteMenuAction(action))
    }

    private fun onTextIndentation() {
        indentation.value = (!(indentation.value)!!).apply {
            getInsertTextHandler().onTextIndentationEvent(this)
        }
    }

    private fun onTextTraditional() {
        traditional.value = (!(traditional.value)!!).apply {
            getInsertTextHandler().onTextTraditionalEvent(this)
        }
    }

    private fun getInsertTextHandler(): InsertTextHandler = editBundle.insertTextHandler

    fun showContent() {
        showSubMenu.value = false
    }

    fun onFontChange(fontInfo: FontInfo) {
        currFontInfo = fontInfo
        currFont.value = fontInfo.name
        getInsertTextHandler().onTextFontFaceEvent(fontInfo)
    }


}