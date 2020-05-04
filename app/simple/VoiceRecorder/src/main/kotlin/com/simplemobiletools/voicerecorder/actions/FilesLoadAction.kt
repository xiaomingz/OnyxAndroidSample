package com.simplemobiletools.voicerecorder.actions

import android.app.Activity
import android.content.Context
import androidx.annotation.WorkerThread
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import com.simplemobiletools.voicerecorder.R
import com.simplemobiletools.voicerecorder.extensions.getDefaultSaveFolder
import java.io.File

class FilesLoadAction {

    fun execute(activity:Activity, callback: (List<File>) -> Unit ) {
        ensureBackgroundThread {
            val files = loadAudioFiles(activity);
            files?.apply {
                activity.runOnUiThread {
                    callback.invoke(files)
                }
            } ?: activity.toast(R.string.content_is_empty)
        }
    }

    @WorkerThread
    private fun loadAudioFiles(context: Context): List<File>? {
        val files = context.getDefaultSaveFolder().listFiles()
        return files?.sortedWith(java.util.Comparator { o1, o2 -> (o2.lastModified() - o1.lastModified()).toInt() })
                ?.map { it }
    }
}