package com.onyx.gallery.handler

import com.onyx.android.sdk.utils.ResManager
import com.onyx.gallery.R
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.models.EraseArgs

/**
 * Created by Leung 2020/8/24 11:16
 **/

enum class EraseModel {
    MOVE, STROKES, REGION, LAYER
}

enum class EraseWidth(val width: Float) {
    ERASER_WIDTH_1(ResManager.getDimension(R.dimen.eraser_width_1)),
    ERASER_WIDTH_2(ResManager.getDimension(R.dimen.eraser_width_2)),
    ERASER_WIDTH_3(ResManager.getDimension(R.dimen.eraser_width_3)),
    ERASER_WIDTH_4(ResManager.getDimension(R.dimen.eraser_width_4)),
    ERASER_WIDTH_5(ResManager.getDimension(R.dimen.eraser_width_5))
}

class EraseHandler(val globalEditBundle: GlobalEditBundle) {

    var eraseModel = EraseArgs.DEFAULT_MODEL
    var eraseWidth = EraseArgs.DEFAULT_WIDTH

    fun isEraseLayer(): Boolean = eraseModel == EraseModel.LAYER

    fun isEraseOnMove(): Boolean = eraseModel == EraseModel.MOVE

    fun isEraseByStrokes(): Boolean = eraseModel == EraseModel.STROKES

    fun isEraseByRegion(): Boolean = eraseModel == EraseModel.REGION

}