package com.onyx.gallery.common

import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.helpers.NoteManager
import org.greenrobot.eventbus.EventBus

/**
 * Created by Leung on 2020/5/9
 */
abstract class BaseEditAction<T : RxRequest> : BaseAction<T>() {

    val noteManager: NoteManager = globalEditBundle.noteManager
    val eventBus: EventBus = globalEditBundle.eventBus

}