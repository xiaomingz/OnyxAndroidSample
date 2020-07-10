package com.onyx.gallery.viewmodel

import androidx.lifecycle.ViewModel
import com.onyx.gallery.bundle.GlobalEditBundle

/**
 * Created by Leung on 2020/5/6
 */
abstract class BaseViewModel : ViewModel() {
    protected val globalEditBundle = GlobalEditBundle.instance
    protected val noteManage = globalEditBundle.drawHandler

    fun postEvent(event: Any) {
        globalEditBundle.eventBus.post(event)
    }
}