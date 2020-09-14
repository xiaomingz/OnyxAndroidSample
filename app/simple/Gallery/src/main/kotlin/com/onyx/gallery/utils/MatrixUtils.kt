package com.onyx.gallery.utils

import android.graphics.Matrix
import android.graphics.PointF

/**
 * Created by Leung 2020/9/10 15:18
 **/
object MatrixUtils {

    fun mapPointF(pointF: PointF, matrix: Matrix): PointF {
        val floatArray = floatArrayOf(pointF.x, pointF.y)
        matrix.mapPoints(floatArray)
        return PointF(floatArray[0], floatArray[1])
    }

}