package com.onyx.gallery.action

import androidx.appcompat.app.AppCompatActivity
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.R
import com.onyx.gallery.common.BaseEditAction
import com.onyx.gallery.dialogs.ConfirmDialog
import com.onyx.gallery.event.ui.UpdateTouchHandlerEvent
import com.onyx.gallery.request.DeleteFileRequest

/**
 * Created by Leung 2020/7/31 10:37
 **/
class DeleteFileAction(private val hostActivity: AppCompatActivity, private val filePath: String) : BaseEditAction<RxRequest>() {

    override fun execute(rxCallback: RxCallback<RxRequest>?) {
        drawHandler.setRawDrawingEnabled(false)
        ConfirmDialog(R.string.isConfirmDeleteFile) { deleteFile() }
                .apply { onCancelCallback = { updateTouchHandler() } }
                .apply { show(hostActivity.supportFragmentManager, ConfirmDialog::class.java.simpleName) }
    }

    private fun deleteFile() {
        globalEditBundle.enqueue(DeleteFileRequest(filePath), object : RxCallback<RxRequest>() {
            override fun onNext(p0: RxRequest) {
                hostActivity.finish()
            }
        })
    }

    private fun updateTouchHandler() {
        eventBus.post(UpdateTouchHandlerEvent())
    }
}