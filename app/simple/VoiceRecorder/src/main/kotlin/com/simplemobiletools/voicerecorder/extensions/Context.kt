package com.simplemobiletools.voicerecorder.extensions

import android.content.Context
import com.simplemobiletools.commons.extensions.internalStoragePath
import com.simplemobiletools.commons.helpers.isQPlus
import com.simplemobiletools.voicerecorder.R
import com.simplemobiletools.voicerecorder.helpers.Config
import java.io.File

val Context.config: Config get() = Config.newInstance(applicationContext)
fun Context.getDefaultSaveFolder(): File {
    val folderPath = if (isQPlus()) {
        cacheDir.absolutePath
    } else {
        val defaultFolder = File("$internalStoragePath/${getString(R.string.app_name)}")
        if (!defaultFolder.exists()) {
            defaultFolder.mkdir()
        }
        defaultFolder.absolutePath
    }
    return File(folderPath)
}
