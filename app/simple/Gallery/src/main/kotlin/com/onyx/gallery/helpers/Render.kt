package com.onyx.gallery.helpers

import android.graphics.Canvas

/**
 * Created by Leung on 2020/5/20
 */
interface Render {
    @Throws(Exception::class)
    fun renderToCanvas(canvas: Canvas): Boolean
}