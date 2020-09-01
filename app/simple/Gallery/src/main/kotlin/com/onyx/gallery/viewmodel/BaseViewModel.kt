package com.onyx.gallery.viewmodel

import androidx.lifecycle.ViewModel
import com.onyx.android.sdk.utils.EventBusUtils
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.event.touch.TouchChangeEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Leung on 2020/5/6
 */
abstract class BaseViewModel : ViewModel() {
    protected val globalEditBundle = GlobalEditBundle.instance
    protected val noteManage = globalEditBundle.drawHandler
    private var isTouching = false

    init {
        EventBusUtils.ensureRegister(globalEditBundle.eventBus, this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTouchChangeEvent(event: TouchChangeEvent) {
        isTouching = event.isTouching
        onTouchChange(isTouching)
    }

    override fun onCleared() {
        super.onCleared()
        EventBusUtils.ensureUnregister(globalEditBundle.eventBus, this)
    }

    open fun onTouchChange(isTouching: Boolean) {

    }

    fun postEvent(event: Any) {
        globalEditBundle.eventBus.post(event)
    }

    fun isTouching(): Boolean = globalEditBundle.touchHandlerManager.isTouching() || isTouching
}