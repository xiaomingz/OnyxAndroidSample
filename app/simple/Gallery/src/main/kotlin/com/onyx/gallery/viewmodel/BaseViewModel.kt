package com.onyx.gallery.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.onyx.android.sdk.utils.EventBusUtils
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.event.touch.TouchChangeEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Leung on 2020/5/6
 */
abstract class BaseViewModel(val editBundle: EditBundle) : ViewModel() {
    protected val drawHandler = editBundle.drawHandler
    private var isTouching = false

    init {
        EventBusUtils.ensureRegister(editBundle.eventBus, this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTouchChangeEvent(event: TouchChangeEvent) {
        isTouching = event.isTouching
        onTouchChange(isTouching)
    }

    override fun onCleared() {
        super.onCleared()
        EventBusUtils.ensureUnregister(editBundle.eventBus, this)
    }

    open fun onTouchChange(isTouching: Boolean) {

    }

    fun postEvent(event: Any) {
        editBundle.eventBus.post(event)
    }

    fun isTouching(): Boolean = editBundle.touchHandlerManager.isTouching() || isTouching

    class ViewModeFactory(protected val editBundle: EditBundle) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(EditBundle::class.java).newInstance(editBundle)
        }
    }
}