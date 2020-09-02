package com.onyx.gallery.common

import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.bundle.EditBundle
import org.greenrobot.eventbus.EventBus

/**
 * Created by Leung on 2020/5/9
 */
abstract class BaseEditAction<T : RxRequest>(editBundle: EditBundle) : BaseAction<T>(editBundle) {

    val drawHandler = editBundle.drawHandler
    val eraseHandler = editBundle.eraseHandler
    val eventBus: EventBus = editBundle.eventBus

}