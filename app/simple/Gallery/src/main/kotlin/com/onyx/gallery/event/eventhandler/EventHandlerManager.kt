package com.onyx.gallery.event.eventhandler

import com.onyx.gallery.bundle.GlobalEditBundle

/**
 * Created by Leung on 2020/5/21
 */
class EventHandlerManager(globalEditBundle: GlobalEditBundle) {

    enum class EventHandlerType {
        PEN_EVENT, NORMAL_SHAPE_EVENT
    }

    private val eventHandlerMap = mutableMapOf<EventHandlerType, BaseEventHandler>()

    init {
        eventHandlerMap[EventHandlerType.PEN_EVENT] = PenEventHandler(globalEditBundle)
        eventHandlerMap[EventHandlerType.NORMAL_SHAPE_EVENT] = NormalShapeHandler(globalEditBundle)
        activateHandler(EventHandlerType.PEN_EVENT)
    }

    private fun getEventHandler(type: EventHandlerType?): BaseEventHandler? = eventHandlerMap[type]

    fun activateHandler(eventHandlerType: EventHandlerType) {
        deactivateHandler()
        getEventHandler(eventHandlerType)?.onActivate()
    }

    fun deactivateHandler() {
        for ((key, value) in eventHandlerMap) {
            value.onDeactivate()
        }
    }

}