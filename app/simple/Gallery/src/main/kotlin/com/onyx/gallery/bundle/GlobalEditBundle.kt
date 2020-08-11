package com.onyx.gallery.bundle

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Matrix
import android.graphics.PointF
import android.net.Uri
import android.provider.MediaStore
import com.onyx.android.sdk.data.Size
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxManager
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.android.sdk.utils.DeviceReceiver
import com.onyx.gallery.App
import com.onyx.gallery.R
import com.onyx.gallery.handler.CropHandler
import com.onyx.gallery.handler.DrawHandler
import com.onyx.gallery.handler.InsertTextHandler
import com.onyx.gallery.handler.UndoRedoHandler
import com.onyx.gallery.handler.touch.TouchHandlerManager
import com.onyx.gallery.helpers.SystemUIChangeReceiver
import com.simplemobiletools.commons.extensions.getRealPathFromURI
import com.simplemobiletools.commons.extensions.isPathOnOTG
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.commons.helpers.REAL_FILE_PATH
import java.io.File

/**
 * Created by Leung on 2020/4/30
 */
class GlobalEditBundle private constructor(context: Context) : BaseBundle(context) {

    var canFingerTouch = true
    var supportZoom = true
    var uri: Uri? = null
    lateinit var orgPath: String
    lateinit var filePath: String
    private var saveUri: Uri? = null

    var initDx = 0f
    var initDy = 0f
    var initScaleFactor = 0f

    val undoRedoHandler: UndoRedoHandler by lazy { UndoRedoHandler() }
    val drawHandler = DrawHandler(context, this, eventBus)
    val rxManager: RxManager by lazy { RxManager.Builder.sharedSingleThreadManager() }
    val touchHandlerManager = TouchHandlerManager(this)
    val insertTextHandler = InsertTextHandler(this)
    val cropHandler = CropHandler(this)

    private val receiver = DeviceReceiver()

    companion object {
        val instance: GlobalEditBundle by lazy {
            GlobalEditBundle(App.instance)
        }
    }

    fun parseIntent(host: Activity) {
        uri = parseImageUri(host)
        saveUri = parseSaveUri(host.intent)
        orgPath = filePath
        receiver.systemUIChangeListener = SystemUIChangeReceiver()
        receiver.enable(App.instance, true)
    }

    private fun parseImageUri(host: Activity): Uri? {
        val intent = host.intent
        if (intent == null) {
            host.finish()
        }
        if (intent.data == null) {
            host.toast(R.string.invalid_image_path)
            host.finish()
            return null
        }
        var uri = intent.data!!
        if (intent.extras?.containsKey(REAL_FILE_PATH) == true) {
            filePath = intent.extras!!.getString(REAL_FILE_PATH)
            uri = when {
                host.isPathOnOTG(filePath) -> uri
                filePath.startsWith("file:/") -> Uri.parse(filePath)
                else -> Uri.fromFile(File(filePath))
            }
        } else {
            filePath = host.getRealPathFromURI(uri).apply {
                uri = Uri.fromFile(File(this))
            }
        }
        return uri
    }

    private fun parseSaveUri(intent: Intent): Uri? = when {
        intent.extras?.containsKey(MediaStore.EXTRA_OUTPUT) == true -> intent.extras!!.get(MediaStore.EXTRA_OUTPUT) as Uri
        else -> uri!!
    }

    fun <T : RxRequest?> enqueue(request: T, callback: RxCallback<T>?) {
        rxManager.enqueue(request, callback)
    }

    fun release() {
        receiver.enable(App.instance, false)
        drawHandler.release()
        cropHandler.release()
        insertTextHandler.release()
        touchHandlerManager.deactivateHandler()
    }

    fun getContainerCenterPoint(): PointF {
        val surfaceRect = drawHandler.surfaceRect
        return PointF((surfaceRect.width() / 2).toFloat(), (surfaceRect.height() / 2).toFloat())
    }

    fun scaleToContainer(imageSize: Size): Float {
        val surfaceRect = drawHandler.surfaceRect
        val containerWidth = surfaceRect.width().toFloat()
        val containerHeight = surfaceRect.height().toFloat()
        val scaleFactor = Math.min(containerWidth / imageSize.width, containerHeight / imageSize.height)
        imageSize.width = (imageSize.width * scaleFactor).toInt()
        imageSize.height = (imageSize.height * scaleFactor).toInt()
        return scaleFactor
    }

    fun getInitMatrix(): Matrix {
        val matrix = Matrix()
        matrix.postScale(initScaleFactor, initScaleFactor)
        matrix.postTranslate(initDx, initDy)
        return matrix
    }

    fun getNormalizedMatrix(): Matrix {
        val matrix = Matrix()
        val normalizedMatrix = Matrix()
        matrix.postScale(initScaleFactor, initScaleFactor)
        matrix.postTranslate(initDx, initDy)
        matrix.invert(normalizedMatrix)
        return normalizedMatrix
    }

    fun undo() {
        touchHandlerManager.activateHandler?.undo()
    }

    fun redo() {
        touchHandlerManager.activateHandler?.redo()
    }

}