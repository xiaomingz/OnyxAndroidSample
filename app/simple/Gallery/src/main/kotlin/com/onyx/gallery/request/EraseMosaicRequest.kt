package com.onyx.gallery.request

import android.graphics.Path
import android.graphics.RectF
import android.graphics.Region
import com.onyx.android.sdk.pen.data.TouchPointList
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung 2020/7/14 12:26
 **/
class EraseMosaicRequest(val pointList: TouchPointList) : BaseRequest() {

    override fun execute(drawHandler: DrawHandler) {
        val mosaicPathList = drawHandler.getMosaicPathList()
        val removedMosaicPaths = hitTest(pointList, mosaicPathList)
        if (removedMosaicPaths.isEmpty()) {
            return
        }
        undoRedoHandler.eraseMosaics(removedMosaicPaths)
        updateCurrMosaicPath(mosaicPathList)
        renderShapesToBitmap = true
        renderToScreen = true
    }

    private fun hitTest(pointList: TouchPointList, mosaicPathList: MutableList<Path>): MutableList<Path> {
        val removedMosaicPaths = mutableListOf<Path>()
        val bounds = RectF()
        val region = Region()
        mosaicPathList.forEach { path ->
            path.computeBounds(bounds, true)
            region.setPath(path, Region(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt()))
            if (hitTestInRegion(pointList, region)) {
                removedMosaicPaths.add(path)
            }
        }
        return removedMosaicPaths
    }

    private fun hitTestInRegion(pointList: TouchPointList, region: Region): Boolean {
        pointList.points.forEach { touchPoint ->
            if (region.contains(touchPoint.x.toInt(), touchPoint.y.toInt())) {
                return true
            }
        }
        return false
    }

    private fun updateCurrMosaicPath(mosaicPathList: MutableList<Path>) {
        val mosaicPath = Path()
        if (!mosaicPathList.isEmpty()) {
            val path = mosaicPathList.get(mosaicPathList.size - 1)
            mosaicPath.set(path)
        }
        drawHandler.setCurrMosaicPath(mosaicPath)
    }
}