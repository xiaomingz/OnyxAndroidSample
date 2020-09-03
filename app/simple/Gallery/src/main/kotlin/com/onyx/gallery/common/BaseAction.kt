package com.onyx.gallery.common

import com.onyx.android.sdk.rx.RxAction
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.bundle.EditBundle

/**
 * Created by Leung on 2020/5/9
 */
abstract class BaseAction<T : RxRequest>(val editBundle: EditBundle) : RxAction<T>() {
    var showProgress = false

    val context = editBundle.context

    open fun setAbort() {}

}