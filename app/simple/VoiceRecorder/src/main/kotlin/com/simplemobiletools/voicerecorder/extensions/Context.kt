package com.simplemobiletools.voicerecorder.extensions

import android.content.Context
import com.simplemobiletools.commons.extensions.internalStoragePath
import com.simplemobiletools.voicerecorder.helpers.Config
import java.io.File

val Context.config: Config get() = Config.newInstance(applicationContext)

fun Context.getSaveRootPath(): String {
    return internalStoragePath
}

fun Context.getDefaultSaveFolder(): File {
    val defaultFolder = File("${getSaveRootPath()}/VoiceRecord")
    if (!defaultFolder.exists()) {
        defaultFolder.mkdirs()
    }
    return defaultFolder
}
