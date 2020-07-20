package com.onyx.gallery.views.shape

import com.onyx.android.sdk.scribble.shape.BaseShape
import com.onyx.android.sdk.scribble.utils.ShapeUtils

/**
 * Created by Leung 2020/7/11 15:07
 **/
open class BaseLineShape : BaseShape() {

    override fun isAddMovePoint(): Boolean = false

    override fun hitTest(x: Float, y: Float, radius: Float): Boolean {
        val renderPoints = floatArrayOf(downPoint.x, downPoint.y, currentPoint.x, currentPoint.y)
        matrix?.mapPoints(renderPoints)
        return ShapeUtils.hitTestLine(renderPoints[0], renderPoints[1], renderPoints[2], renderPoints[3], x, y, radius)
    }
}