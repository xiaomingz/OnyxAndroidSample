package com.onyx.gallery.helpers

import com.onyx.android.sdk.utils.DeviceUtils
import com.onyx.android.sdk.utils.ResManager
import com.onyx.gallery.R

/**
 * Created by Leung on 2020/6/8
 */
data class InsertTextConfig(
        var textSize: Float = ResManager.getAppContext().resources.getDimension(R.dimen.edit_text_shape_text_size),
        var textSpacing: Float = 1.0f,
        var italic: Boolean = false,
        var bold: Boolean = false,
        var fontFace: Boolean = false
) {
    var fontType: DeviceUtils.FontType? = null
}