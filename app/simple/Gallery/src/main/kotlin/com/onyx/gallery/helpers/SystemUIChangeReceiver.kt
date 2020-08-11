package com.onyx.gallery.helpers

import com.onyx.android.sdk.utils.DeviceReceiver.SystemUIChangeListener
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.event.ui.FloatButtonChangedEvent
import com.onyx.gallery.event.ui.FloatButtonMenuStateChangedEvent

/**
 * Created by Leung 2020/8/11 15:49
 **/
class SystemUIChangeReceiver : SystemUIChangeListener() {

    override fun onFloatButtonChanged(active: Boolean) {
        GlobalEditBundle.instance.eventBus.post(FloatButtonChangedEvent(active))
    }

    override fun onFloatButtonMenuStateChanged(open: Boolean) {
        GlobalEditBundle.instance.eventBus.post(FloatButtonMenuStateChangedEvent(open))
    }

}