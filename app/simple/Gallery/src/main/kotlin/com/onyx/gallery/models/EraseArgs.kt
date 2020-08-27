package com.onyx.gallery.models

import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.gallery.handler.EraseModel
import com.onyx.gallery.handler.EraseWidth

/**
 * Created by Leung 2020/8/24 16:52
 **/
class EraseArgs(val eraseWidth: Float, val eraseModel: EraseModel = DEFAULT_MODEL, val touchPointList: TouchPointList) {
    companion object {
        val DEFAULT_MODEL = EraseModel.STROKES
        val DEFAULT_WIDTH = EraseWidth.ERASER_WIDTH_2.width
    }
}

