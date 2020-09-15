package com.onyx.gallery.request.transform

import android.graphics.Rect
import android.graphics.RectF
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung on 2020/6/22
 */
class RotateRequest(editBundle: EditBundle, private val singleRotateAngle: Float) : BaseRequest(editBundle) {
    override fun execute(drawHandler: DrawHandler) {
        drawHandler.clearScreen()
        drawHandler.rotateScreen(singleRotateAngle, editBundle.getContainerCenterPoint())

        val rotateMatrix = cropHandler.getRotateMatrix()
        val drawHandler = editBundle.drawHandler
        val rectF = RectF(drawHandler.orgLimitRect)
        rotateMatrix.mapRect(rectF)
        drawHandler.rotateLimitRect(Rect().apply { rectF.round(this) })

        renderShapesToBitmap = true
        renderToScreen = true
    }
}