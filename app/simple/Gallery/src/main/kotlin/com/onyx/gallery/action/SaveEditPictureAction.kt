package com.onyx.gallery.action

import androidx.appcompat.app.AppCompatActivity
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.dialogs.ConfirmSaveDialog
import com.onyx.gallery.event.result.SaveEditPictureResultEvent
import com.onyx.gallery.event.ui.UpdateTouchHandlerEvent
import com.onyx.gallery.request.SaveEditPictureRequest

/**
 * Created by Leung on 2020/5/20
 */
class SaveEditPictureAction(private val hostActivity: AppCompatActivity, private val filePath: String, private val onCancelCallback: () -> Unit) : BaseEditAction<RxRequest>() {

    override fun execute(rxCallback: RxCallback<RxRequest>?) {
        if (!hasModify()) {
            return hostActivity.finish()
        }
        drawHandler.setRawDrawingEnabled(false)
        ConfirmSaveDialog { isSaveAs -> saveImage(isSaveAs, rxCallback) }
                .apply { onCancelCallback = this@SaveEditPictureAction.onCancelCallback }
                .apply { show(hostActivity.supportFragmentManager, ConfirmSaveDialog::class.java.simpleName) }
    }

    private fun hasModify(): Boolean {
        return drawHandler.hasModify() || globalEditBundle.cropHandler.hasModify()
    }

    private fun saveImage(isSaveAs: Boolean, rxCallback: RxCallback<RxRequest>?) {
        globalEditBundle.enqueue(SaveEditPictureRequest(filePath, isSaveAs), object : RxCallback<SaveEditPictureRequest>() {

            override fun onNext(request: SaveEditPictureRequest) {
                RxCallback.onNext(rxCallback, request)
                eventBus.post(SaveEditPictureResultEvent())
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                RxCallback.onError(rxCallback, e)
                eventBus.post(SaveEditPictureResultEvent(e))
            }

            override fun onFinally() {
                super.onFinally()
                updateTouchHandler()
            }
        })
    }

    private fun updateTouchHandler() {
        eventBus.post(UpdateTouchHandlerEvent())
    }

}