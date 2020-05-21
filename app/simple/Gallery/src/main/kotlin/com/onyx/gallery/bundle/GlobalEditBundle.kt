package com.onyx.gallery.bundle

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import com.onyx.android.sdk.scribble.shape.ShapeFactory
import com.onyx.gallery.App
import com.onyx.gallery.R
import com.onyx.gallery.event.eventhandler.EventHandlerManager
import com.onyx.gallery.helpers.NoteManager
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
    var uri: Uri? = null
    var filePath: String? = null
    private var saveUri: Uri? = null

    var initDx = 0f
    var initDy = 0f
    var initScaleFactor = 0f

    var currShapeType = ShapeFactory.SHAPE_BRUSH_SCRIBBLE
        set(value) {
            if (field == value) {
                return
            }
            lastShapeType = currShapeType
            field = value
        }

    private var lastShapeType = ShapeFactory.SHAPE_BRUSH_SCRIBBLE

    val noteManager: NoteManager = NoteManager(context, eventBus)
    val eventHandlerManager: EventHandlerManager = EventHandlerManager(this)

    companion object {
        val instance: GlobalEditBundle by lazy {
            GlobalEditBundle(App.instance)
        }
    }

    fun parseIntent(host: Activity) {
        uri = parseImageUri(host)
        saveUri = parseSaveUri(host.intent)
    }

    private fun parseImageUri(host: Activity): Uri? {
        val intent = host.intent
        if (intent.data == null) {
            host.toast(R.string.invalid_image_path)
            host.finish()
            return null
        }
        var uri = intent.data!!
        if (intent.extras?.containsKey(REAL_FILE_PATH) == true) {
            filePath = intent.extras!!.getString(REAL_FILE_PATH)
            uri = when {
                host.isPathOnOTG(filePath!!) -> uri
                filePath!!.startsWith("file:/") -> Uri.parse(filePath)
                else -> Uri.fromFile(File(filePath))
            }
        } else {
            filePath = host.getRealPathFromURI(uri)
            filePath?.apply {
                uri = Uri.fromFile(File(this))
            }
        }
        return uri
    }

    private fun parseSaveUri(intent: Intent): Uri? = when {
        intent.extras?.containsKey(MediaStore.EXTRA_OUTPUT) == true -> intent.extras!!.get(MediaStore.EXTRA_OUTPUT) as Uri
        else -> uri!!
    }


}