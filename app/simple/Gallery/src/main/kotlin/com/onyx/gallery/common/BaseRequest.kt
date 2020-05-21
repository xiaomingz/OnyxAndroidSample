package com.onyx.gallery.common

import androidx.annotation.WorkerThread
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.android.sdk.utils.Benchmark
import com.onyx.gallery.BuildConfig
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.helpers.NoteManager

/**
 * Created by Leung on 2020/5/16
 */
abstract class BaseRequest : RxRequest() {
    private val REPORT_LOG_TIME_THRESHOLD = 1500

    protected val globalEditBundle = GlobalEditBundle.instance
    protected val noteManager = globalEditBundle.noteManager

    @Volatile
    var renderToScreen = true

    @Volatile
    var renderShapesToBitmap = false

    @Volatile
    var pauseRawDrawingRender = true

    @Volatile
    var pauseRawInputReader = true

    @WorkerThread
    @Throws(Exception::class)
    override fun execute() {
        val benchmark = Benchmark()
        beforeExecute(noteManager)
        execute(noteManager)
        afterExecute(noteManager)
        if (BuildConfig.DEBUG || benchmark.duration() >= REPORT_LOG_TIME_THRESHOLD) {
            benchmark.report(javaClass.simpleName)
        }
    }

    open fun afterExecute(noteManager: NoteManager) {
        if (renderShapesToBitmap) {
            noteManager.renderShapesToBitmap()
        }
        if (renderToScreen) {
            noteManager.renderToScreen()
        }
        setPauseRawDraw(false)
        noteManager.setRawDrawingRenderEnabled(true)
        noteManager.setRawInputReaderEnable(true)
    }

    @WorkerThread
    @Throws(Exception::class)
    abstract fun execute(noteManager: NoteManager)

    open fun beforeExecute(noteManager: NoteManager) {
        if (pauseRawDrawingRender) {
            noteManager.isRawDrawingRenderEnabled = false
        }
        if (pauseRawInputReader) {
            noteManager.setRawInputReaderEnable(false)
        }
    }

    open fun setPauseRawDraw(pauseRawDrawing: Boolean): BaseRequest {
        pauseRawDrawingRender = pauseRawDrawing
        pauseRawInputReader = pauseRawDrawing
        return this
    }


}