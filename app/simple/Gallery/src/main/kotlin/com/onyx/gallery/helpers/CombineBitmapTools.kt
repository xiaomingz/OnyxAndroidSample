package com.onyx.gallery.helpers

import android.graphics.*
import java.io.Serializable
import java.util.*

class CombineBitmapTools private constructor() {

    companion object {

        fun combineBitmap(config: CombineConfig, bitmapList: List<Bitmap>?): Bitmap? {
            if (bitmapList.isNullOrEmpty()) {
                return null
            }

            val resultBitmap: Bitmap?
            var len = bitmapList.size
            if (len > config.max) {
                len = config.max
            }
            val list = ArrayList<Bitmap>()
            for (i in 0 until len) {
                list.add(bitmapList[i])
            }
            val bitmapEntityList = generateCombineBitmapEntity(config, list)
            resultBitmap = getCombineBitmaps(bitmapEntityList!!, config)
            return resultBitmap
        }

        private fun getCombineBitmaps(entityList: List<CombineBitmapEntity>, config: CombineConfig): Bitmap? {
            var newBitmap: Bitmap? = Bitmap.createBitmap(config.width.toInt(), config.height.toInt(), Bitmap.Config.ARGB_8888)
            for (i in entityList.indices) {
                newBitmap = mixtureBitmap(newBitmap, entityList[i].bitmap, PointF(entityList[i].x, entityList[i].y))
            }
            return newBitmap
        }

        private fun mixtureBitmap(first: Bitmap?, second: Bitmap?, fromPoint: PointF?): Bitmap? {
            if (first == null || second == null || fromPoint == null) {
                return null
            }
            val newBitmap = Bitmap.createBitmap(first.width, first.height, Bitmap.Config.ARGB_8888)
            val cv = Canvas(newBitmap)
            cv.drawBitmap(first, 0f, 0f, null)
            cv.drawBitmap(second, fromPoint.x, fromPoint.y, null)
            return newBitmap
        }

        fun generateCombineBitmapEntity(config: CombineConfig, size: Int): List<CombineBitmapEntity>? {
            var len = size
            if (len <= 0) {
                return null
            }
            if (len > config.max) {
                len = config.max
            }
            val columnRowCount = generateColumnRowCountByCount(len)
            val perBitmapWidth = (config.width - config.divider * (columnRowCount.columns + 1)) * 1.0f / columnRowCount.columns
            var perBitmapHeight = (config.height - config.divider * (columnRowCount.rows + 1)) * 1.0f / columnRowCount.rows
            if (config.equilateral) {
                perBitmapHeight = perBitmapWidth
            }
            val list = LinkedList<CombineBitmapEntity>()
            var index = 0
            for (row in 0 until columnRowCount.rows) {
                for (column in 0 until columnRowCount.columns) {
                    if (index >= len) {
                        break
                    }
                    val bitmapEntity = CombineBitmapEntity()
                    bitmapEntity.y = config.divider * (row + 1) + row * perBitmapHeight
                    bitmapEntity.x = config.divider * (column + 1) + column * perBitmapWidth
                    bitmapEntity.width = perBitmapWidth
                    bitmapEntity.height = perBitmapHeight
                    list.add(bitmapEntity)
                    ++index
                }
            }
            return list
        }

        private fun generateCombineBitmapEntity(config: CombineConfig, bitmapList: List<Bitmap>?): List<CombineBitmapEntity>? {
            if (bitmapList.isNullOrEmpty()) {
                return null
            }
            val list = generateCombineBitmapEntity(config, bitmapList.size)
            if (list.isNullOrEmpty()) {
                return list
            }
            for (i in list.indices) {
                val entity = list.get(i)
                entity.bitmap = createScaledBitmap(bitmapList[i], entity.width.toInt(), entity.height.toInt())
            }
            return list
        }

        fun createScaledBitmap(origin: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
            val scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
            val ratioX = newWidth / origin.width.toFloat()
            val ratioY = newHeight / origin.height.toFloat()
            val middleX = newWidth / 2.0f
            val middleY = newHeight / 2.0f
            val scaleMatrix = Matrix()
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
            val canvas = Canvas(scaledBitmap)
            canvas.matrix = scaleMatrix
            canvas.drawBitmap(origin, middleX - origin.width / 2, middleY - origin.height / 2, Paint(Paint.FILTER_BITMAP_FLAG))
            return scaledBitmap
        }

        private fun generateColumnRowCountByCount(count: Int): ColumnRowCount {
            when (count) {
                1, 2, 3, 4 -> return ColumnRowCount(2, 2)
                5, 6 -> return ColumnRowCount(2, 3)
                7, 8, 9 -> return ColumnRowCount(3, 3)
                else -> return ColumnRowCount(1, 1)
            }
        }

        private class ColumnRowCount(var rows: Int, var columns: Int) : Serializable

        class CombineBitmapEntity : Serializable {
            var x: Float = 0.toFloat()
            var y: Float = 0.toFloat()
            var width: Float = 0.toFloat()
            var height: Float = 0.toFloat()
            var bitmap: Bitmap? = null
        }

        class CombineConfig(var width: Float, var height: Float) : Serializable {
            var divider: Float = 0.toFloat()
            var max = DEFAULT_MAX_COUNT
            var equilateral = false

            constructor(width: Float, height: Float, divider: Float, max: Int) : this(width, height) {
                this.max = max
                this.divider = divider
            }

            companion object {

                val DEFAULT: CombineConfig
                val DEFAULT_MAX_COUNT = 4

                init {
                    DEFAULT = CombineConfig(512f, 681f, 14f, DEFAULT_MAX_COUNT)
                }
            }
        }
    }
}