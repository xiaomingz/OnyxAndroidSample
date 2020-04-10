package com.simplemobiletools.voicerecorder.extensions

import android.content.Context
import com.simplemobiletools.commons.extensions.internalStoragePath
import com.simplemobiletools.voicerecorder.R
import com.simplemobiletools.voicerecorder.helpers.Config
import java.io.File

val Context.config: Config get() = Config.newInstance(applicationContext)
fun Context.getDefaultSaveFolder() = File("$internalStoragePath/${getString(R.string.app_name)}")
