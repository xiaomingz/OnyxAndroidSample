package com.onyx.gallery.viewmodel

import android.graphics.Rect
import com.onyx.gallery.action.shape.CreateImageShapeAction

/**
 * Created by Leung on 2020/5/7
 */
class EditContentViewModel : BaseViewModel() {

    fun loadImageToHostView(filePath: String?, rect: Rect) =
            filePath?.run {
                val createImageShapeAction = CreateImageShapeAction()
                createImageShapeAction.setFilePath(this)
                createImageShapeAction.setScribbleRect(rect)
                createImageShapeAction.execute(null)
            }
}