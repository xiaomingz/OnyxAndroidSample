package com.onyx.gallery.helpers

import android.graphics.*
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import java.security.MessageDigest

/**
 * Created by Leung 2020/8/20 15:28
 **/
class RoundTransform(val radius: Float = 0f, val isCenterCrop: Boolean = true) : BitmapTransformation() {

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val bitmap: Bitmap = if (isCenterCrop) {
            TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight)
        } else {
            TransformationUtils.fitCenter(pool, toTransform, outWidth, outHeight)
        }
        return roundCrop(pool, bitmap)
    }

    private fun roundCrop(pool: BitmapPool, source: Bitmap): Bitmap {
        var result: Bitmap = pool[source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_4444]
        if (result == null) {
            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_4444)
        }
        val canvas = Canvas(result)
        val paint = Paint()
        paint.setShader(BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP))
        paint.setAntiAlias(true)
        val rectF = RectF(0f, 0f, source.getWidth().toFloat(), source.getHeight().toFloat())
        canvas.drawRoundRect(rectF, radius, radius, paint)
        return result
    }
}