package com.onyx.gallery.action

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.onyx.android.sdk.rx.RequestChain
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.R
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.dialogs.ConfirmSaveDialog
import com.onyx.gallery.event.result.SaveEditPictureResultEvent
import com.onyx.gallery.event.ui.UpdateTouchHandlerEvent
import com.onyx.gallery.request.SaveEditPictureRequest
import com.onyx.gallery.request.image.CreateImageShapeRequest
import com.onyx.gallery.utils.ToastUtils

/**
 * Created by Leung on 2020/5/20
 */
class SaveEditPictureAction(editBundle: EditBundle, private val hostActivity: AppCompatActivity, private val filePath: String, private val isExit: Boolean = false, private val onCancelCallback: () -> Unit) : BaseEditAction<RxRequest>(editBundle) {

    override fun execute(rxCallback: RxCallback<RxRequest>?) {
        if (isExit && !hasModify()) {
            hostActivity.setResult(Activity.RESULT_OK, Intent())
            hostActivity.finish()
            return
        }
        drawHandler.setRawDrawingEnabled(false)
        var messageResId = R.string.is_save_image_edit
        if (isExit) {
            messageResId = R.string.exit_image_edit
        }
        ConfirmSaveDialog().apply {
            messageRes = messageResId
            onConfirmCallback = { isSaveAs -> saveImage(isSaveAs, rxCallback) }
        }.apply { onCancelCallback = this@SaveEditPictureAction.onCancelCallback }
                .apply { show(hostActivity.supportFragmentManager, ConfirmSaveDialog::class.java.simpleName) }
    }

    private fun hasModify(): Boolean {
        return drawHandler.hasModify() || editBundle.cropHandler.hasModify() || editBundle.insertTextHandler.hasModify()
    }

    private fun saveImage(isSaveAs: Boolean, rxCallback: RxCallback<RxRequest>?) {
        val saveEditPictureRequest = SaveEditPictureRequest(editBundle, filePath, isSaveAs)
        val createImageShapeRequest = CreateImageShapeRequest(editBundle)
        val requestChain = object : RequestChain<BaseRequest>() {
            override fun beforeExecute(request: RxRequest?) {
                super.beforeExecute(request)
                if (request is CreateImageShapeRequest) {
                    editBundle.onAfterSaveImage()
                    request.setImageFilePath(saveEditPictureRequest.imagePath)
                    request.setScribbleRect(drawHandler.surfaceRect)
                }
            }
        }

        requestChain.addRequest(saveEditPictureRequest)
        requestChain.addRequest(createImageShapeRequest)
        editBundle.enqueue(requestChain, object : RxCallback<RxRequest>() {

            override fun onNext(request: RxRequest) {
                RxCallback.onNext(rxCallback, request)
                var tipResID = R.string.save_success
                if (isSaveAs) {
                    tipResID = R.string.save_as_success
                }
                ToastUtils.showScreenCenterToast(hostActivity, tipResID)
                eventBus.post(SaveEditPictureResultEvent(isExit = isExit))
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                RxCallback.onError(rxCallback, e)
                eventBus.post(SaveEditPictureResultEvent(e, isExit))
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