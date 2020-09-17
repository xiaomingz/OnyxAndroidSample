package com.onyx.gallery.request.transform

import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung on 2020/6/22
 */
class RotateRequest(editBundle: EditBundle, private val currAngle: Float, private val singleRotateAngle: Float) : BaseRequest(editBundle) {
    val rotateMatrix = Matrix()

    override fun execute(drawHandler: DrawHandler) {
        drawHandler.clearScreen()
        drawHandler.rotateScreen(singleRotateAngle, editBundle.getContainerCenterPoint())

        rotateMatrix.set(craeteRotateMatrix())

        val rectF = RectF(drawHandler.orgLimitRect)
        rotateMatrix.mapRect(rectF)
        drawHandler.rotateLimitRect(Rect().apply { rectF.round(this) })

        renderShapesToBitmap = true
        renderToScreen = true
    }

    fun craeteRotateMatrix(): Matrix {
        val angle = currAngle
        val centerPoint = editBundle.getContainerCenterPoint()
        return Matrix().apply {
            postRotate(angle, centerPoint.x, centerPoint.y)
        }
    }

}