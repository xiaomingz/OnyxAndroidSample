package com.onyx.gallery.helpers

import com.onyx.android.sdk.utils.DeviceUtils
import com.onyx.android.sdk.utils.ResManager
import com.onyx.gallery.R

/**
 * Created by Leung on 2020/6/8
 */
class InsertTextConfig {

    private val defaultTextSize = ResManager.getAppContext().resources.getDimension(R.dimen.edit_text_shape_text_size)
    private val defaultTextColor = ResManager.getColor(R.color.default_text_color)

    var textSize = defaultTextSize
    var textSpacing = 1f
    var italic = false
    var bold = false
    var fontId = ""
    var fontFace = ""
    var isIndentation = false
    var textColor = defaultTextColor
    var fontType: DeviceUtils.FontType? = null

    fun reset() {
        textSize = defaultTextSize
        textSpacing = 1.0f
        italic = false
        bold = false
        fontId = ""
        fontFace = ""
        isIndentation = false
        textColor = defaultTextColor
    }
}