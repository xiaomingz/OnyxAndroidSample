package com.onyx.gallery.handler.touch

import com.onyx.gallery.bundle.GlobalEditBundle

/**
 * Created by Leung on 2020/6/5
 */

enum class TouchHandlerType {
    SCRIBBLE, GRAPHICS, TEXT_INSERTION, CROP, MOSAIC
}

class TouchHandlerManager(globalEditBundle: GlobalEditBundle) {

    var activateHandler: TouchHandler? = null
    private val touchHandlerMap = mutableMapOf<TouchHandlerType, TouchHandler>()

    init {
        touchHandlerMap[TouchHandlerType.SCRIBBLE] = ScribbleTouchHandler(globalEditBundle)
        touchHandlerMap[TouchHandlerType.GRAPHICS] = GraphicsTouchHandler(globalEditBundle)
        touchHandlerMap[TouchHandlerType.TEXT_INSERTION] = InsertTextTouchHandler(globalEditBundle)
        touchHandlerMap[TouchHandlerType.TEXT_INSERTION] = InsertTextTouchHandler(globalEditBundle)
        touchHandlerMap[TouchHandlerType.CROP] = CropTouchHandler(globalEditBundle)
        touchHandlerMap[TouchHandlerType.MOSAIC] = MosaicTouchHandler(globalEditBundle)
        activateHandler(TouchHandlerType.SCRIBBLE)
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