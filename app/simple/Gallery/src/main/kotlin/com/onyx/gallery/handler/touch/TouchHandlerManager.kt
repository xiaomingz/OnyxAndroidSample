package com.onyx.gallery.handler.touch

import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.event.ui.UpdateOptionsMenuEvent

/**
 * Created by Leung on 2020/6/5
 */

enum class TouchHandlerType {
    EPD_SHAPE, NORMAL_SHAPE, TEXT_INSERTION, CROP, MOSAIC, ERASE
}

class TouchHandlerManager(private val editBundle: EditBundle) {

    var activateHandler: TouchHandler? = null
    private val touchHandlerMap = mutableMapOf<TouchHandlerType, TouchHandler>()

    init {
        touchHandlerMap[TouchHandlerType.EPD_SHAPE] = EpdShapeTouchHandler(editBundle)
        touchHandlerMap[TouchHandlerType.NORMAL_SHAPE] = NormalShapeTouchHandler(editBundle)
        touchHandlerMap[TouchHandlerType.TEXT_INSERTION] = InsertTextTouchHandler(editBundle)
        touchHandlerMap[TouchHandlerType.CROP] = CropTouchHandler(editBundle)
        touchHandlerMap[TouchHandlerType.MOSAIC] = MosaicTouchHandler(editBundle)
        touchHandlerMap[TouchHandlerType.ERASE] = EraseTouchHandler(editBundle)
        activateHandler(TouchHandlerType.EPD_SHAPE)
    }

    private fun getTouchHandler(type: TouchHandlerType?): TouchHandler? = touchHandlerMap[type]

    fun activateHandler(touchHandlerType: TouchHandlerType) {
        deactivateHandler()
        activateHandler = getTouchHandler(touchHandlerType)?.apply { onActivate() }
        editBundle.eventBus.post(UpdateOptionsMenuEvent())
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

    fun canRawDrawingRenderEnabled(): Boolean {
        activateHandler?.let { touchHandler ->
            return touchHandler.canRawDrawingRenderEnabled()
        }
        return false
    }

}