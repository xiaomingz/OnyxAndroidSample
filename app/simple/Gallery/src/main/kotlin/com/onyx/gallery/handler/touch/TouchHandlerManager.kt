package com.onyx.gallery.handler.touch

import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.event.ui.UpdateOptionsMenuEvent

/**
 * Created by Leung on 2020/6/5
 */

enum class TouchHandlerType {
    EPD_SHAPE, NORMAL_SHAPE, TEXT_INSERTION, CROP, MOSAIC, ERASE
}

class TouchHandlerManager(private val globalEditBundle: GlobalEditBundle) {

    var activateHandler: TouchHandler? = null
    private val touchHandlerMap = mutableMapOf<TouchHandlerType, TouchHandler>()

    init {
        touchHandlerMap[TouchHandlerType.EPD_SHAPE] = EpdShapeTouchHandler(globalEditBundle)
        touchHandlerMap[TouchHandlerType.NORMAL_SHAPE] = NormalShapeTouchHandler(globalEditBundle)
        touchHandlerMap[TouchHandlerType.TEXT_INSERTION] = InsertTextTouchHandler(globalEditBundle)
        touchHandlerMap[TouchHandlerType.CROP] = CropTouchHandler(globalEditBundle)
        touchHandlerMap[TouchHandlerType.MOSAIC] = MosaicTouchHandler(globalEditBundle)
        touchHandlerMap[TouchHandlerType.ERASE] = EraseTouchHandler(globalEditBundle)
        activateHandler(TouchHandlerType.EPD_SHAPE)
    }

    private fun getTouchHandler(type: TouchHandlerType?): TouchHandler? = touchHandlerMap[type]

    fun activateHandler(touchHandlerType: TouchHandlerType) {
        deactivateHandler()
        activateHandler = getTouchHandler(touchHandlerType)?.apply { onActivate() }
        globalEditBundle.eventBus.post(UpdateOptionsMenuEvent())
    }

    fun deactivateHandler() {
        activateHandler?.onDeactivate()
    }

    fun isTouching(): Boolean {
        activateHandler?.let { touchHandler ->
            return touchHandler.isTouching()
        }
        return false
    }

}