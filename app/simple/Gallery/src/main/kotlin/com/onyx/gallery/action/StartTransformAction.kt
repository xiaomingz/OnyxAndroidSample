package com.onyx.gallery.action

import android.graphics.RectF
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.request.StartTransformRequest

/**
 * Created by Leung on 2020/6/10
 */
class StartTransformAction(private val transformShapes: List<Shape>) : BaseEditAction<RxRequest>() {

    override fun execute(rxCallback: RxCallback<RxRequest>?) {
        val limitRect = drawHandler.currLimitRect
        val startTransformRequest = StartTransformRequest(transformShapes, RectF(limitRect))
        globalEditBundle.enqueue(startTransformRequest, rxCallback)
    }

}