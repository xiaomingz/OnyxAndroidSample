package com.onyx.gallery.viewmodel

import android.graphics.Rect
import androidx.lifecycle.MutableLiveData
import com.onyx.gallery.action.shape.CreateImageShapeAction
import com.onyx.gallery.bundle.EditBundle

/**
 * Created by Leung on 2020/5/7
 */
class EditContentViewModel(editBundle: EditBundle) : BaseViewModel(editBundle) {

    val textInput = MutableLiveData("")
    val textInputSelection = MutableLiveData(0)

    fun loadImageToHostView(filePath: String?, rect: Rect) =
            filePath?.run {
                val createImageShapeAction = CreateImageShapeAction(editBundle)
                createImageShapeAction.setFilePath(this)
                createImageShapeAction.setScribbleRect(rect)
                createImageShapeAction.execute(null)
            }
}