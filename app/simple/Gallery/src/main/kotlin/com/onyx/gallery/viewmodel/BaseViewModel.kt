package com.onyx.gallery.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.onyx.android.sdk.data.FontInfo
import com.onyx.android.sdk.utils.EventBusUtils
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.event.touch.TouchChangeEvent
import com.onyx.gallery.handler.EraseModel
import com.onyx.gallery.handler.EraseWidth
import com.onyx.gallery.models.MenuAction
import com.onyx.gallery.models.MenuState
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Leung on 2020/5/6
 */
abstract class BaseViewModel(val editBundle: EditBundle) : ViewModel() {
    protected val drawHandler = editBundle.drawHandler
    private val menuStateHandler = editBundle.menuStateHandler
    private var isTouching = false

    init {
        EventBusUtils.ensureRegister(editBundle.eventBus, this)
    }

    fun onResume() {
        onUpdateMenuState(getMenuState())
        updateTouchHandler()
    }

    fun onDestroyView() {
        onSaveMenuState(getMenuState())
    }

    abstract fun onSaveMenuState(menuState: MenuState)

    abstract fun onUpdateMenuState(menuState: MenuState)

    abstract fun updateTouchHandler()

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTouchChangeEvent(event: TouchChangeEvent) {
        isTouching = event.isTouching
        onTouchChange(isTouching)
    }

    override fun onCleared() {
        super.onCleared()
        EventBusUtils.ensureUnregister(editBundle.eventBus, this)
    }

    open fun onTouchChange(isTouching: Boolean) {

    }

    fun getMenuState(): MenuState {
        return menuStateHandler.getMenuViewModelState(this::class.java)
    }

    fun postEvent(event: Any) {
        editBundle.eventBus.post(event)
    }

    fun isTouching(): Boolean = editBundle.touchHandlerManager.isTouching() || isTouching

    protected fun getStorecColor(): Int = getMenuState().storecColor
    protected fun getStrokeWidth(): Float = getMenuState().storeWidth
    protected fun getShapeType(): Int = getMenuState().shapeType
    protected fun getTextSize(): Float = getMenuState().textSize
    protected fun getTextColor(): Int = getMenuState().textColor
    protected fun getTypeface(): FontInfo? = getMenuState().typeface
    protected fun getTextBold(): Boolean = getMenuState().bold
    protected fun getTextIndentation(): Boolean = getMenuState().indentation
    protected fun getTextTraditional(): Boolean = getMenuState().traditional
    protected fun getCropRectType(): MenuAction = getMenuState().cropRectType
    protected fun getEraseModel(): EraseModel = getMenuState().eraseModel
    protected fun getEraseWidth(): EraseWidth = getMenuState().eraseWidth
    protected fun getEraseWidthEnable(): Boolean = getMenuState().eraseWidthEnable
    protected fun getFontTabIndex(): Int = getMenuState().fontTabIndex

    protected fun setStorecColor(storeColor: Int) {
        getMenuState().storecColor = storeColor
    }

    protected fun setStrokeWidth(storeWidth: Float) {
        getMenuState().storeWidth = storeWidth
    }

    protected fun setShapeType(shapeType: Int) {
        getMenuState().shapeType = shapeType
    }

    protected fun setTextSize(textSize: Float) {
        getMenuState().textSize = textSize
    }

    protected fun setTextColor(textColor: Int) {
        getMenuState().textColor = textColor
    }

    protected fun setTypeface(typeface: FontInfo) {
        getMenuState().typeface = typeface
    }

    protected fun setTextBold(bold: Boolean) {
        getMenuState().bold = bold
    }

    protected fun setTextIndentation(indentation: Boolean) {
        getMenuState().indentation = indentation
    }

    protected fun setTextTraditional(traditional: Boolean) {
        getMenuState().traditional = traditional
    }

    protected fun setCropRectType(cropRectType: MenuAction) {
        getMenuState().cropRectType = cropRectType
    }

    protected fun setEraseModel(eraseModel: EraseModel) {
        getMenuState().eraseModel = eraseModel
    }

    protected fun setEraseWidth(eraseWidth: EraseWidth) {
        getMenuState().eraseWidth = eraseWidth
    }

    protected fun setEraseWidthEnable(eraseWidthEnable: Boolean) {
        getMenuState().eraseWidthEnable = eraseWidthEnable
    }

    protected fun setFontTabIndex(fontTabIndex: Int) {
        getMenuState().fontTabIndex = fontTabIndex
    }

    class ViewModeFactory(protected val editBundle: EditBundle) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(EditBundle::class.java).newInstance(editBundle)
        }
    }
}