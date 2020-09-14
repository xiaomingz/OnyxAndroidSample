package com.onyx.gallery.handler

import android.text.TextUtils
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.gallery.bundle.EditBundle

/**
 * Created by Leung 2020/8/31 14:18
 **/
class CacheHandler private constructor() {

    companion object {
        val instance = CacheHandler()

        private val cacheHandwritingMap = mutableMapOf<String, MutableList<Shape>>()
    }

    fun cacheHandwritingShape(editBundle: EditBundle) {
        val shapes = editBundle.drawHandler.getHandwritingShape()
        if (shapes.isEmpty()) {
            return
        }
        val imagePath = editBundle.imagePath
        if (cacheHandwritingMap.containsKey(imagePath)) {
            cacheHandwritingMap.remove(imagePath)
        }
        cacheHandwritingMap.put(imagePath, shapes)
    }

    fun restoreHandwritingShape(imagePath: String, drawHandler: DrawHandler) {
        val restoreHandwritingShape = mutableListOf<Shape>()
        if (!drawHandler.isSurfaceCreated || TextUtils.isEmpty(imagePath)) {
            return
        }
        if (!hasCacheHandwritingShape(imagePath)) {
            return
        }
        cacheHandwritingMap.remove(imagePath)?.run {
            restoreHandwritingShape.addAll(this)
            drawHandler.addShapes(restoreHandwritingShape)
        }
    }

    fun hasCacheHandwritingShape(imagePath: String): Boolean {
        return cacheHandwritingMap.containsKey(imagePath)
    }


}