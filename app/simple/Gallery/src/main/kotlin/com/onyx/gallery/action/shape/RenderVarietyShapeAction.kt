package com.onyx.gallery.action.shape

import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.request.shape.RenderVarietyShapeRequest
import io.reactivex.disposables.Disposable
import java.util.*

/**
 * Created by Leung on 2020/5/20
 */
class RenderVarietyShapeAction : BaseEditAction<BaseRequest>() {

    private val shapeList: MutableList<Shape> = mutableListOf()
    private var disposableList: List<Disposable>? = null

    fun addShape(shape: Shape): RenderVarietyShapeAction {
        shapeList.add(shape)
        return this
    }

    fun getDisposableList(): List<Disposable>? {
        if (disposableList == null) {
            disposableList = ArrayList()
        }
        return disposableList
    }

    fun setDisposableList(disposableList: List<Disposable>?): RenderVarietyShapeAction {
        this.disposableList = disposableList
        return this
    }

    override fun execute(rxCallback: RxCallback<BaseRequest>?) {
        val request = RenderVarietyShapeRequest(shapeList)
        globalEditBundle.enqueue(request, object : RxCallback<BaseRequest>() {
            override fun onNext(rxRequest: BaseRequest) {
                RxCallback.onNext(rxCallback, request)
            }
            override fun onError(e: Throwable) {
                super.onError(e)
                RxCallback.onError(rxCallback, e)
            }
            override fun onFinally() {
                super.onFinally()
                RxCallback.onFinally(rxCallback)
            }
        })
    }

}