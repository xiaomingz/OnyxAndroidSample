package com.onyx.gallery.viewmodel

import android.graphics.Rect
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.gallery.action.shape.CreateImageShapeAction
import com.onyx.gallery.action.shape.ShapeChangeAction
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.models.MenuState

/**
 * Created by Leung on 2020/5/7
 */
class EditContentViewModel(editBundle: EditBundle) : BaseViewModel(editBundle) {

    companion object {
        private const val DELAY_MILLIS = 500L
    }

    val textInput = MutableLiveData("")
    val textInputSelection = MutableLiveData(0)

    private val handler = Handler()

    override fun updateTouchHandler() {
    }

    override fun onSaveMenuState(menuState: MenuState) {

    }

    override fun onUpdateMenuState(menuState: MenuState) {
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }

    fun loadImageToHostView(filePath: String?, rect: Rect) =
            filePath?.run {
                val createImageShapeAction = CreateImageShapeAction(editBundle)
                createImageShapeAction.setFilePath(this)
                createImageShapeAction.setScribbleRect(rect)
                createImageShapeAction.execute(object : RxCallback<RxRequest>() {
                    override fun onNext(rxRequest: RxRequest) {
                        handler.postDelayed({ ShapeChangeAction(editBundle, drawHandler.getCurrShapeType()).execute(null) }, DELAY_MILLIS)
                    }
                })
            }
}