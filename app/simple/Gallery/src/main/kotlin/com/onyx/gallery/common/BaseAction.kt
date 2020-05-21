package com.onyx.gallery.common

import com.onyx.android.sdk.rx.RxAction
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.bundle.GlobalEditBundle

/**
 * Created by Leung on 2020/5/9
 */
abstract class BaseAction<T : RxRequest> : RxAction<T>() {
    var showProgress = false

    val globalEditBundle = GlobalEditBundle.instance
    val context = globalEditBundle.context

    open fun setAbort() {}

}