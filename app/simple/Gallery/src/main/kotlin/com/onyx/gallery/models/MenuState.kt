package com.onyx.gallery.models

import com.onyx.android.sdk.data.FontInfo
import com.onyx.gallery.handler.EraseModel
import com.onyx.gallery.handler.EraseWidth
import com.onyx.gallery.helpers.DrawArgs
import com.onyx.gallery.viewmodel.FontSelectViewModel

/**
 * Created by Leung 2020/9/17 16:27
 **/
data class MenuState(
        var storecColor: Int = DrawArgs.defaultStrokeColor,
        var storeWidth: Float = DrawArgs.defaultStrokeWidth,
        var shapeType: Int = DrawArgs.defaultShape,
        var textSize: Float = DrawArgs.getDefaultTextSize(),
        var textColor: Int = DrawArgs.defaultStrokeColor,
        var typeface: FontInfo? = null,
        var bold: Boolean = false,
        var indentation: Boolean = false,
        var traditional: Boolean = false,
        var cropRectType: MenuAction = MenuAction.CROP_CUSTOMIZE,
        var eraseModel: EraseModel = EraseModel.STROKES,
        var eraseWidth: EraseWidth = EraseWidth.ERASER_WIDTH_2,
        var eraseWidthEnable: Boolean = false,
        var fontTabIndex: Int = FontSelectViewModel.TAB_FONT_CN
)