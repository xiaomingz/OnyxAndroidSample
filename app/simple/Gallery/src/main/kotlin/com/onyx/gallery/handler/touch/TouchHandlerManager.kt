package com.onyx.gallery.handler.touch

import com.onyx.gallery.bundle.GlobalEditBundle

/**
 * Created by Leung on 2020/6/5
 */

enum class TouchHandlerType {
    EPD_SHAPE, NORMAL_SHAPE, TEXT_INSERTION, CROP, MOSAIC
}

class TouchHandlerManager(globalEditBundle: GlobalEditBundle) {

    var activateHandler: TouchHandler? = null
    private val touchHandlerMap = mutableMapOf<TouchHandlerType, TouchHandler>()

    init {
        touchHandlerMap[TouchHandlerType.EPD_SHAPE] = EpdShapeTouchHandler(globalEditBundle)
        touchHandlerMap[TouchHandlerType.NORMAL_SHAPE] = NormalShapeTouchHandler(globalEditBundle)
        touchHandlerMap[TouchHandlerType.TEXT_INSERTION] = InsertTextTouchHandler(globalEditBundle)
        touchHandlerMap[TouchHandlerType.CROP] = CropTouchHandler(globalEditBundle)
        touchHandlerMap[TouchHandlerType.MOSAIC] = MosaicTouchHandler(globalEditBundle)
        activateHandler(TouchHandlerType.EPD_SHAPE)
    }

    private fun getTouchHandler(type: TouchHandlerType?): TouchHandler? = touchHandlerMap[type]

    fun activateHandler(touchHandlerType: TouchHandlerType) {
        deactivateHandler()
        activateHandler = getTouchHandler(touchHandlerType)?.apply { onActivate() }
    }

    fun deactivateHandler() {
        activateHandler?.onDeactivate()
    }
}