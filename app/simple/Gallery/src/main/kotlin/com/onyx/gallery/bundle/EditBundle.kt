package com.onyx.gallery.bundle

import android.app.Activity
import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.net.Uri
import com.onyx.android.sdk.data.Size
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.RxManager
import com.onyx.android.sdk.rx.RxRequest
import com.onyx.android.sdk.utils.DeviceReceiver
import com.onyx.gallery.App
import com.onyx.gallery.R
import com.onyx.gallery.handler.*
import com.onyx.gallery.handler.touch.TouchHandlerManager
import com.onyx.gallery.helpers.DeviceConfig
import com.onyx.gallery.helpers.SystemUIChangeReceiver
import com.simplemobiletools.commons.extensions.getRealPathFromURI
import com.simplemobiletools.commons.extensions.isPathOnOTG
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.commons.helpers.REAL_FILE_PATH
import java.io.File

/**
 * Created by Leung on 2020/4/30
 */
class EditBundle constructor(context: Context) : BaseBundle(context) {

    var canFingerTouch = true
    var supportZoom = true
    var uri: Uri? = null
    lateinit var orgImagePath: String
    lateinit var imagePath: String

    var orgImageSize = Size(0, 0)
    var renderImageSize = Size(0, 0)

    var offsetX: Float = 0f
    var offsetY: Float = 0f
    var initScaleFactor = 0f

    private val receiver = DeviceReceiver()
    val undoRedoHandler: UndoRedoHandler by lazy { UndoRedoHandler() }
    val drawHandler = DrawHandler(context, this, eventBus)
    val rxManager: RxManager by lazy { RxManager.Builder.sharedSingleThreadManager() }
    val insertTextHandler = InsertTextHandler(this)
    val cropHandler = CropHandler(this)
    val eraseHandler = EraseHandler(this)
    val touchHandlerManager = TouchHandlerManager(this)
    val menuStateHandler by lazy { MenuStateHandler(this) }

    companion object {
        fun newSingleThreadManager() = RxManager.Builder.newSingleThreadManager()
    }

    fun parseIntent(host: Activity) {
        uri = parseImageUri(host)
        orgImagePath = imagePath
        receiver.systemUIChangeListener = SystemUIChangeReceiver(eventBus)
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
            imagePath = intent.extras!!.getString(REAL_FILE_PATH)
            uri = when {
                host.isPathOnOTG(imagePath) -> uri
                imagePath.startsWith("file:/") -> Uri.parse(imagePath)
                else -> Uri.fromFile(File(imagePath))
            }
        } else {
            imagePath = host.getRealPathFromURI(uri).apply {
                uri = Uri.fromFile(File(this))
            }
        }
        return uri
    }

    fun <T : RxRequest?> enqueue(request: T, callback: RxCallback<T>?) {
        rxManager.enqueue(request, callback)
    }

    fun release() {
        receiver.enable(App.instance, false)
        drawHandler.release()
        cropHandler.release()
        insertTextHandler.release()
        eraseHandler.release()
        touchHandlerManager.deactivateHandler()
        menuStateHandler.release()
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
        matrix.postTranslate(offsetX, offsetY)
        return matrix
    }

    fun getNormalizedMatrix(): Matrix {
        val matrix = Matrix()
        val normalizedMatrix = Matrix()
        matrix.postScale(initScaleFactor, initScaleFactor)
        matrix.postTranslate(offsetX, offsetY)
        matrix.invert(normalizedMatrix)
        return normalizedMatrix
    }

    fun undo() {
        touchHandlerManager.activateHandler?.undo()
    }

    fun redo() {
        touchHandlerManager.activateHandler?.redo()
    }

    fun onAfterSaveImage() {
        renderImageSize = Size(orgImageSize.width, orgImageSize.height)
        initScaleFactor = 1f
        offsetX = 0f
        offsetX = 0f
        drawHandler.resetRenderContextMatrix()
    }

    fun isSupportHandwriting() = getDeviceConfig().isSupportHandwriting()

    private fun getDeviceConfig(): DeviceConfig = DeviceConfig.sharedInstance(context)

}