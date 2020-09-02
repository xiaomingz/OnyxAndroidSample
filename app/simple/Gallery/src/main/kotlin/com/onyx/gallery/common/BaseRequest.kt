package com.onyx.gallery.common

import androidx.annotation.WorkerThread
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.android.sdk.utils.Benchmark
import com.onyx.gallery.BuildConfig
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.handler.DrawHandler

/**
 * Created by Leung on 2020/5/16
 */
abstract class BaseRequest(protected val editBundle: EditBundle) : RxRequest() {
    private val reportLogTimeThreshold = 1500

    val drawHandler = editBundle.drawHandler
    val cropHandler = editBundle.cropHandler
    val insertTextHandler = editBundle.insertTextHandler
    val undoRedoHandler = editBundle.undoRedoHandler

    @Volatile
    var renderToScreen = false

    @Volatile
    var renderShapesToBitmap = false

    @Volatile
    private var pauseRawDrawingRender = false

    @Volatile
    private var pauseRawInputReader = false

    @WorkerThread
    @Throws(Exception::class)
    override fun execute() {
        val benchmark = Benchmark()
        beforeExecute(drawHandler)
        execute(drawHandler)
        afterExecute(drawHandler)
        if (BuildConfig.DEBUG || benchmark.duration() >= reportLogTimeThreshold) {
            benchmark.report(javaClass.simpleName)
        }
    }

    open fun afterExecute(drawHandler: DrawHandler) {
        if (renderShapesToBitmap) {
            drawHandler.renderShapesToBitmap()
        }
        if (renderToScreen) {
            drawHandler.renderToScreen()
        }
    }

    @WorkerThread
    @Throws(Exception::class)
    abstract fun execute(drawHandler: DrawHandler)

    open fun beforeExecute(drawHandler: DrawHandler) {
        if (pauseRawDrawingRender) {
            drawHandler.setRawDrawingRenderEnabled(false)
        }
        if (pauseRawInputReader) {
            drawHandler.setRawInputReaderEnable(false)
        }
    }

    open fun setPauseRawDraw(pauseRawDrawing: Boolean): BaseRequest {
        pauseRawDrawingRender = pauseRawDrawing
        pauseRawInputReader = pauseRawDrawing
        return this
    }

    open fun canRawDrawingRenderEnabled(): Boolean {
        val touchHandler = editBundle.touchHandlerManager.activateHandler ?: return false
        return touchHandler.canRawDrawingRenderEnabled()
    }

}