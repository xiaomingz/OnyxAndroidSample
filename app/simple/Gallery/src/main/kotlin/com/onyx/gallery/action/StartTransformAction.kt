package com.onyx.gallery.action

import android.graphics.Rect
import android.graphics.RectF
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.request.StartTransformRequest

/**
 * Created by Leung on 2020/6/10
 */
class StartTransformAction(editBundle: EditBundle, private val transformShape: Shape) : BaseEditAction<RxRequest>(editBundle) {

    override fun execute(rxCallback: RxCallback<RxRequest>?) {
        val orgImageSize = editBundle.orgImageSize
        val limitRect = Rect(0, 0, orgImageSize.width, orgImageSize.height)
        val startTransformRequest = StartTransformRequest(editBundle, transformShape, RectF(limitRect))
        editBundle.enqueue(startTransformRequest, rxCallback)
    }

}