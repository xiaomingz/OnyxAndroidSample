package com.onyx.gallery.activities

import android.annotation.SuppressLint
import android.database.ContentObserver
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowManager
import com.simplemobiletools.commons.activities.BaseSimpleActivity
import com.simplemobiletools.commons.dialogs.FilePickerDialog
import com.simplemobiletools.commons.extensions.getParentPath
import com.simplemobiletools.commons.extensions.getRealPathFromURI
import com.simplemobiletools.commons.extensions.scanPathRecursively
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import com.simplemobiletools.commons.helpers.isPiePlus
import com.onyx.gallery.R
import com.onyx.gallery.extensions.addPathToDB
import com.onyx.gallery.extensions.config
import com.onyx.gallery.extensions.updateDirectoryPath
import com.simplemobiletools.commons.extensions.baseConfig

@SuppressLint("Registered")
open class SimpleActivity : BaseSimpleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        useDynamicTheme = false
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (!useDynamicTheme) {
            updateActionbarColor(getActionbarColor())
        }
    }

    open fun getActionbarColor(): Int {
        return baseConfig.backgroundColor
    }

    val observer = object : ContentObserver(null) {
        override fun onChange(selfChange: Boolean, uri: Uri) {
            super.onChange(selfChange, uri)
            val path = getRealPathFromURI(uri)
            if (path != null) {
                updateDirectoryPath(path.getParentPath())
                addPathToDB(path)
            }
        }
    }

    override fun getAppIconIDs() = arrayListOf(
            R.drawable.ic_launcher
    )

    override fun getAppLauncherName() = getString(R.string.app_launcher_name)

    @SuppressLint("InlinedApi")
    protected fun checkNotchSupport() {
        if (isPiePlus()) {
            val cutoutMode = when {
                config.showNotch -> WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                else -> WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
            }

            window.attributes.layoutInDisplayCutoutMode = cutoutMode
            if (config.showNotch) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }
        }
    }

    protected fun registerFileUpdateListener() {
        try {
            contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, observer)
            contentResolver.registerContentObserver(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, true, observer)
        } catch (ignored: Exception) {
        }
    }

    protected fun unregisterFileUpdateListener() {
        try {
            contentResolver.unregisterContentObserver(observer)
        } catch (ignored: Exception) {
        }
    }

    protected fun showAddIncludedFolderDialog(callback: () -> Unit) {
        FilePickerDialog(this, config.lastFilepickerPath, false, config.shouldShowHidden, false, true) {
            config.lastFilepickerPath = it
            config.addIncludedFolder(it)
            callback()
            ensureBackgroundThread {
                scanPathRecursively(it)
            }
        }
    }
}
