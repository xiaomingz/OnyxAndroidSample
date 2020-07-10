package com.onyx.gallery.common

import com.onyx.android.sdk.rx.RxRequest
import org.greenrobot.eventbus.EventBus

/**
 * Created by Leung on 2020/5/9
 */
abstract class BaseEditAction<T : RxRequest> : BaseAction<T>() {

    val drawHandler = globalEditBundle.drawHandler
    val eventBus: EventBus = globalEditBundle.eventBus

}