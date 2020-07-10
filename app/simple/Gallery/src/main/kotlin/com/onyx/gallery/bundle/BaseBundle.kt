package com.onyx.gallery.bundle

import android.content.Context
import org.greenrobot.eventbus.EventBus

/**
 * Created by Leung on 2020/4/30
 */
open class BaseBundle(val context: Context) {

    val eventBus: EventBus by lazy {
        EventBus()
    }

    fun postEvent(event: Any) = eventBus.post(event)
}