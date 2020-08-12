package com.onyx.gallery.activities

import android.annotation.SuppressLint
import android.database.ContentObserver
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import com.onyx.gallery.R
import com.onyx.gallery.extensions.addPathToDB
import com.onyx.gallery.extensions.config
import com.onyx.gallery.extensions.updateDirectoryPath
import com.simplemobiletools.commons.activities.BaseSimpleActivity
import com.simplemobiletools.commons.dialogs.FilePickerDialog
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import com.simplemobiletools.commons.helpers.isPiePlus
import kotlinx.android.synthetic.main.view_action_bar.*

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

    fun showCropMenu(isShowCropMenu: Boolean) {
        ivSave.beVisibleIf(!isShowCropMenu)
        ivOk.beVisibleIf(isShowCropMenu)
    }

    fun showImageListMenu() {
        hideAllMenu()
        ivMore.beVisibleIf(true)
    }

    fun showImageBrowseMenu() {
        hideAllMenu()
        arrayOf(ivEdit, ivProperties, ivShare, ivDelete).forEach { it.beVisibleIf(true) }
    }

    fun showPhotoVideoBrowseMenu() {
        hideAllMenu()
        arrayOf(ivEdit, ivProperties, ivShare).forEach { it.beVisibleIf(true) }
    }

    fun showExternalVideoBrowseMenu() {
        hideAllMenu()
    }

    fun showVideoBrowseMenu() {
        hideAllMenu()
        arrayOf(ivProperties, ivShare).forEach { it.beVisibleIf(true) }
    }

    fun showGIFBrowseMenu() {
        hideAllMenu()
        arrayOf(ivProperties, ivShare).forEach { it.beVisibleIf(true) }
    }

    fun showImageEditMenu() {
        hideAllMenu()
        arrayOf(ivUndo, ivRedo, ivSave).forEach { it.beVisibleIf(true) }
    }

    fun hideAllMenu() {
        arrayOf(ivUndo, ivRedo, ivSave, ivOk,
                ivFavorites, ivEdit, ivProperties, ivShare, ivDelete, ivMore).forEach { it.beVisibleIf(false) }
    }

    open fun onBlackClick(view: View) {
        finish()
    }

    open fun onUndoClick(view: View) {}
    open fun onRedoClick(view: View) {}
    open fun onSaveClick(view: View) {}
    open fun onOkClick(view: View) {}
    open fun onFavoritesClick(view: View) {}
    open fun onEditClick(view: View) {}
    open fun onPropertiesClick(view: View) {}
    open fun onShareClick(view: View) {}
    open fun onDeleteClick(view: View) {}
    open fun onMoreClick(view: View) {}

}
