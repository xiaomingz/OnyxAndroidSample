package com.onyx.gallery.request

import android.graphics.*
import com.onyx.android.sdk.scribble.data.RenderColorConfig
import com.onyx.android.sdk.scribble.shape.RenderContext
import com.onyx.android.sdk.scribble.utils.ShapeUtils
import com.onyx.android.sdk.utils.FileUtils
import com.onyx.android.sdk.utils.MtpUtils
import com.onyx.gallery.common.BaseRequest
import com.onyx.gallery.helpers.NoteManager
import java.io.File

/**
 * Created by Leung on 2020/5/8
 */
class SaveEditPictureRequest(private val filePath: String) : BaseRequest() {

    override fun execute(noteManager: NoteManager) {
        val opts = BitmapFactory.Options()
        val imageBitmap = decodeFile(filePath, opts)
        val newBitmap = createBitmap(filePath, opts)
        val shapeBitmap = createShapeBitmap(opts)
        drawBitmap(newBitmap, imageBitmap, shapeBitmap)
        saveBitmapToFile(filePath, newBitmap)
        newBitmap.recycle()
    }

    private fun createBitmap(path: String, opts: BitmapFactory.Options): Bitmap {
        opts.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, opts)
        val config = Bitmap.Config.ARGB_8888
        return Bitmap.createBitmap(opts.outWidth, opts.outHeight, config)
    }

    private fun decodeFile(path: String, opts: BitmapFactory.Options): Bitmap {
        opts.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(path, BitmapFactory.Options())
    }

    private fun createShapeBitmap(opts: BitmapFactory.Options): Bitmap {
        val renderContext = createRenderContext(opts.outWidth, opts.outHeight)
        val handwritingShape = noteManager.handwritingShape
        var matrix = Matrix()
        val normalizedMatrix = Matrix()
        matrix.postScale(globalEditBundle.initScaleFactor, globalEditBundle.initScaleFactor)
        matrix.postTranslate(globalEditBundle.initDx, globalEditBundle.initDy)
        matrix.invert(normalizedMatrix)
        handwritingShape.forEach {
            for (point in it.points) {
                val normalPoint = ShapeUtils.matrixTouchPoint(point, normalizedMatrix)
                point.set(normalPoint)
            }
        }
        ShapeUtils.renderShapes(handwritingShape, renderContext, true)
        return renderContext.getBitmap()
    }

    private fun createRenderContext(width: Int, height: Int): RenderContext {
        val pngRect = Rect(0, 0, width, height)
        val renderContext = RenderContext.create(Paint(), Matrix())
        renderContext.createBitmap(pngRect)
        renderContext.updateCanvas()
        renderContext.renderColorConfig = RenderColorConfig.EPD_RENDER_COLOR
        return renderContext
    }

    private fun drawBitmap(newBitmap: Bitmap, imageBitmap: Bitmap, shapeBitmap: Bitmap) {
        val canvas = Canvas(newBitmap)
        canvas.drawBitmap(imageBitmap, 0f, 0f, null)
        canvas.drawBitmap(shapeBitmap, 0f, 0f, null)
    }

    private fun saveBitmapToFile(path: String, bitmap: Bitmap) {
        FileUtils.deleteFile(path)
        val file = File(path)
        FileUtils.saveBitmapToFile(bitmap, file, Bitmap.CompressFormat.PNG, 100)
        MtpUtils.updateMtpDb(context, file)
    }


}