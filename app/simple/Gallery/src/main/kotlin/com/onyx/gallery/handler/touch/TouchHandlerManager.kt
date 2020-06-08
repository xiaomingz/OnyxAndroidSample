package com.onyx.gallery.handler.touch

import com.onyx.gallery.bundle.GlobalEditBundle

/**
 * Created by Leung on 2020/6/5
 */

enum class TouchHandlerType {
    SCRIBBLE, GRAPHICS, TEXT_INSERTION
}

class TouchHandlerManager(globalEditBundle: GlobalEditBundle) {

    lateinit var activateHandlerType: TouchHandlerType
    private val touchHandlerMap = mutableMapOf<TouchHandlerType, TouchHandler>()

    init {
        touchHandlerMap[TouchHandlerType.SCRIBBLE] = ScribbleTouchHandler(globalEditBundle)
        touchHandlerMap[TouchHandlerType.GRAPHICS] = GraphicsTouchHandler(globalEditBundle)
        touchHandlerMap[TouchHandlerType.TEXT_INSERTION] = TextInsertTouchHandler(globalEditBundle)
        activateHandler(TouchHandlerType.SCRIBBLE)
    }

    private fun getTouchHandler(type: TouchHandlerType?): TouchHandler? = touchHandlerMap[type]

    fun activateHandler(touchHandlerType: TouchHandlerType) {
        deactivateHandler()
        getTouchHandler(touchHandlerType)?.onActivate()
        activateHandlerType = touchHandlerType
    }

    fun deactivateHandler() {
        for ((key, value) in touchHandlerMap) {
            value.onDeactivate()
        }
    }
}