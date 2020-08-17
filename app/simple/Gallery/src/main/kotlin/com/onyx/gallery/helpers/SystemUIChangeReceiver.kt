package com.onyx.gallery.helpers

import android.content.Intent
import com.onyx.android.sdk.utils.DeviceReceiver.SystemUIChangeListener
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.event.ui.*

/**
 * Created by Leung 2020/8/11 15:49
 **/
class SystemUIChangeReceiver : SystemUIChangeListener() {

    override fun onFloatButtonChanged(active: Boolean) {
        postEvent(FloatButtonChangedEvent(active))
    }

    override fun onFloatButtonMenuStateChanged(open: Boolean) {
        postEvent(FloatButtonMenuStateChangedEvent(open))
    }

    override fun onStatusBarChanged(show: Boolean) {
        postEvent(StatusBarChangedEvent(show))
    }

    override fun onSystemUIChanged(action: String?, open: Boolean) {
        postEvent(SystemUIChangedEvent(action, open))
    }

    override fun onNoFocusSystemDialogChanged(open: Boolean) {
        super.onNoFocusSystemDialogChanged(open)
        postEvent(NoFocusSystemDialogChanged(open))
    }

    override fun onSystemIconChanged(action: String?, iconType: String, open: Boolean) {
        if (open) {
            postEvent(DirtyRenderNoteEvent())
        }
    }

    override fun onHomeClicked() {
        super.onHomeClicked()
        postEvent(HomeClickedEvent())
    }

    override fun onToastChanged(show: Boolean) {
        super.onToastChanged(show)
        if (show) {
            postEvent(ShowToastEvent())
        } else {
            postEvent(HideToastEvent())
        }
    }

    override fun onShutDown(intent: Intent?) {
        super.onShutDown(intent)
        postEvent(ShutDownEvent())
    }

    override fun onReboot() {
        super.onReboot()
        postEvent(RebootEvent())
    }

    private fun postEvent(event: Any) {
        GlobalEditBundle.instance.eventBus.post(event)
    }

}