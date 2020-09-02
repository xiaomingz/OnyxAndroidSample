package com.onyx.gallery.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.onyx.android.sdk.api.device.epd.EpdController
import com.onyx.android.sdk.api.device.epd.UpdateMode
import com.onyx.android.sdk.utils.EventBusUtils
import com.onyx.gallery.App
import com.onyx.gallery.BuildConfig
import com.onyx.gallery.R
import com.onyx.gallery.action.ShareAction
import com.onyx.gallery.dialogs.DeleteWithRememberDialog
import com.onyx.gallery.dialogs.PropertiesDialog
import com.onyx.gallery.event.ui.ApplyFastModeEvent
import com.onyx.gallery.extensions.*
import com.onyx.gallery.fragments.PhotoFragment
import com.onyx.gallery.fragments.VideoFragment
import com.onyx.gallery.fragments.ViewPagerFragment
import com.onyx.gallery.helpers.*
import com.onyx.gallery.models.Medium
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.IS_FROM_GALLERY
import com.simplemobiletools.commons.helpers.PERMISSION_WRITE_STORAGE
import com.simplemobiletools.commons.helpers.REAL_FILE_PATH
import com.simplemobiletools.commons.models.FileDirItem
import kotlinx.android.synthetic.main.fragment_holder.*
import kotlinx.android.synthetic.main.view_action_bar.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.FileInputStream

open class PhotoVideoActivity : SimpleActivity(), ViewPagerFragment.FragmentListener {
    @Volatile
    private var inFastMode = false
    private val TAG = this::class.java.simpleName

    private var mMedium: Medium? = null
    private var mIsFromGallery = false
    private var mFragment: ViewPagerFragment? = null
    private var mUri: Uri? = null

    var mIsVideo = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_holder)
        if (checkAppSideloading()) {
            return
        }

        handlePermission(PERMISSION_WRITE_STORAGE) {
            if (it) {
                checkIntent(savedInstanceState)
            } else {
                toast(R.string.no_storage_permissions)
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window.statusBarColor = Color.TRANSPARENT
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusUtils.ensureUnregister(App.eventBus, this)
        ensureQuitFastMode()
    }

    private fun ensureQuitFastMode() {
        if (!inFastMode) {
            return
        }
        EpdController.applyApplicationFastMode(TAG, false, true)
        inFastMode = false
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onApplyFastModeEvent(event: ApplyFastModeEvent) {
        if (event.enable and !inFastMode) {
            EpdController.applyApplicationFastMode(TAG, true, false, UpdateMode.ANIMATION_QUALITY, Int.MAX_VALUE)
            inFastMode = true
        }
        if (!event.enable and inFastMode) {
            if (mFragment is VideoFragment && (mFragment as VideoFragment).mIsPlaying) {
                return
            }
            EpdController.applyApplicationFastMode(TAG, false, true, UpdateMode.ANIMATION_QUALITY, Int.MAX_VALUE)
            inFastMode = false
        }
    }

    private fun checkIntent(savedInstanceState: Bundle? = null) {
        if (intent.data == null && intent.action == Intent.ACTION_VIEW) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        mUri = intent.data ?: return
        val uri = mUri.toString()
        if (uri.startsWith("content:/") && uri.contains("/storage/")) {
            val guessedPath = uri.substring(uri.indexOf("/storage/"))
            if (getDoesFilePathExist(guessedPath)) {
                val extras = intent.extras ?: Bundle()
                extras.apply {
                    putString(REAL_FILE_PATH, guessedPath)
                    intent.putExtras(this)
                }
            }
        }

        var filename = getFilenameFromUri(mUri!!)
        mIsFromGallery = intent.getBooleanExtra(IS_FROM_GALLERY, false)
        if (mIsFromGallery && filename.isVideoFast() && config.openVideosOnSeparateScreen) {
            launchVideoPlayer()
            return
        }

        if (intent.extras?.containsKey(REAL_FILE_PATH) == true) {
            val realPath = intent.extras!!.getString(REAL_FILE_PATH)
            if (realPath != null && getDoesFilePathExist(realPath)) {
                if (realPath.getFilenameFromPath().contains('.') || filename.contains('.')) {
                    if (isFileTypeVisible(realPath)) {
                        handleLockedFolderOpening(realPath.getParentPath()) { success ->
                            if (success) {
                                sendViewPagerIntent(realPath)
                            }
                            finish()
                        }
                        return
                    }
                } else {
                    filename = realPath.getFilenameFromPath()
                }
            }
        }

        if (mUri!!.scheme == "file") {
            if (filename.contains('.')) {
                handleLockedFolderOpening(mUri!!.path!!.getParentPath()) { success ->
                    if (success) {
                        rescanPaths(arrayListOf(mUri!!.path!!))
                        sendViewPagerIntent(mUri!!.path!!)
                    }
                    finish()
                }
            }
            return
        } else {
            val path = applicationContext.getRealPathFromURI(mUri!!) ?: ""
            if (path != mUri.toString() && path.isNotEmpty() && mUri!!.authority != "mms" && filename.contains('.') && getDoesFilePathExist(path)) {
                if (isFileTypeVisible(path)) {
                    handleLockedFolderOpening(path.getParentPath()) { success ->
                        if (success) {
                            rescanPaths(arrayListOf(mUri!!.path!!))
                            sendViewPagerIntent(path)
                        }
                        finish()
                    }
                    return
                }
            }
        }

        checkNotchSupport()
        val bundle = Bundle()
        val file = File(mUri.toString())
        val type = when {
            filename.isVideoFast() -> TYPE_VIDEOS
            filename.isGif() -> TYPE_GIFS
            filename.isRawFast() -> TYPE_RAWS
            filename.isSvg() -> TYPE_SVGS
            file.isPortrait() -> TYPE_PORTRAITS
            else -> TYPE_IMAGES
        }

        mIsVideo = type == TYPE_VIDEOS
        mMedium = Medium(null, filename, mUri.toString(), mUri!!.path!!.getParentPath(), 0, 0, file.length(), type, 0, false, 0L)
        updateMenu()
        tvTitle.setText(mMedium!!.name)
        bundle.putSerializable(MEDIUM, mMedium)

        if (savedInstanceState == null) {
            mFragment = if (mIsVideo) VideoFragment() else PhotoFragment()
            mFragment!!.listener = this
            mFragment!!.arguments = bundle
            supportFragmentManager.beginTransaction().replace(R.id.fragment_placeholder, mFragment!!).commit()
        }

        if (config.blackBackground) {
            fragment_holder.background = ColorDrawable(Color.BLACK)
        }

        if (config.maxBrightness) {
            val attributes = window.attributes
            attributes.screenBrightness = 1f
            window.attributes = attributes
        }
        EventBusUtils.ensureRegister(App.eventBus, this)
    }

    private fun updateMenu() {
        val medium = mMedium ?: return
        if (mIsVideo) {
            showExternalVideoBrowseMenu()
        } else if (medium.isGIF()) {
            showExternalGIFBrowseMenu()
        } else {
            showExternalImageBrowseMenu()
        }
    }

    private fun launchVideoPlayer() {
        val newUri = getFinalUriFromPath(mUri.toString(), BuildConfig.APPLICATION_ID)
        if (newUri == null) {
            toast(R.string.unknown_error_occurred)
            return
        }

        var isPanorama = false
        val realPath = intent?.extras?.getString(REAL_FILE_PATH) ?: ""
        try {
            if (realPath.isNotEmpty()) {
                val fis = FileInputStream(File(realPath))
                parseFileChannel(realPath, fis.channel, 0, 0, 0) {
                    isPanorama = true
                }
            }
        } catch (ignored: Exception) {
        } catch (ignored: OutOfMemoryError) {
        }

        if (isPanorama) {
            Intent(applicationContext, PanoramaVideoActivity::class.java).apply {
                putExtra(PATH, realPath)
                startActivity(this)
            }
        } else {
            val mimeType = getUriMimeType(mUri.toString(), newUri)
            Intent(applicationContext, VideoPlayerActivity::class.java).apply {
                setDataAndType(newUri, mimeType)
                addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
                if (intent.extras != null) {
                    putExtras(intent.extras!!)
                }

                startActivity(this)
            }
        }
        finish()
    }

    private fun sendViewPagerIntent(path: String) {
        Intent(this, ViewPagerActivity::class.java).apply {
            putExtra(SHOW_FAVORITES, intent.getBooleanExtra(SHOW_FAVORITES, false))
            putExtra(IS_VIEW_INTENT, true)
            putExtra(IS_FROM_GALLERY, mIsFromGallery)
            putExtra(PATH, path)
            startActivity(this)
        }
    }

    private fun checkDeleteConfirmation() {
        val medium = mMedium ?: return
        if (config.isDeletePasswordProtectionOn) {
            handleDeletePasswordProtection {
                deleteConfirmed(medium)
            }
        } else if (config.tempSkipDeleteConfirmation || config.skipDeleteConfirmation) {
            deleteConfirmed(medium)
        } else {
            askConfirmDelete()
        }
    }

    private fun askConfirmDelete() {
        val medium = mMedium ?: return
        val filename = "\"${medium.path.getFilenameFromPath()}\""

        val baseString = if (config.useRecycleBin && !medium.getIsInRecycleBin()) {
            R.string.move_to_recycle_bin_confirmation
        } else {
            R.string.deletion_confirmation
        }

        val message = String.format(resources.getString(baseString), filename)
        DeleteWithRememberDialog(this, message) {
            config.tempSkipDeleteConfirmation = it
            deleteConfirmed(medium)
        }
    }

    private fun deleteConfirmed(medium: Medium) {
        val path = medium.path
        if (getIsPathDirectory(path) || !path.isMediaFile()) {
            return
        }

        val fileDirItem = FileDirItem(path, path.getFilenameFromPath())
        if (config.useRecycleBin && !medium.getIsInRecycleBin()) {
            movePathsInRecycleBin(arrayListOf(path)) {
                if (it) {
                    tryDeleteFileDirItem(fileDirItem, false, false) {
                        deleteDirectoryIfEmpty(fileDirItem)
                    }
                } else {
                    toast(R.string.unknown_error_occurred)
                }
            }
        } else {
            handleDeletion(fileDirItem)
        }
    }

    private fun deleteDirectoryIfEmpty(fileDirItem: FileDirItem) {
        if (config.deleteEmptyFolders && !fileDirItem.isDownloadsFolder() && fileDirItem.isDirectory && fileDirItem.getProperFileCount(this, true) == 0) {
            tryDeleteFileDirItem(fileDirItem, true, true)
        }
    }

    private fun handleDeletion(fileDirItem: FileDirItem) {
        tryDeleteFileDirItem(fileDirItem, false, true) {
            deleteDirectoryIfEmpty(fileDirItem)
        }
    }

    private fun shareImage() {
        val medium = mMedium ?: return
        ShareAction(this, medium.path).execute(null)
    }

    private fun showProperties() {
        mMedium?.path?.let { PropertiesDialog(this, it).show(supportFragmentManager, PropertiesDialog::class.java.simpleName) }
    }

    private fun isFileTypeVisible(path: String): Boolean {
        val filter = config.filterMedia
        return !(path.isImageFast() && filter and TYPE_IMAGES == 0 ||
                path.isVideoFast() && filter and TYPE_VIDEOS == 0 ||
                path.isGif() && filter and TYPE_GIFS == 0 ||
                path.isRawFast() && filter and TYPE_RAWS == 0 ||
                path.isSvg() && filter and TYPE_SVGS == 0 ||
                path.isPortrait() && filter and TYPE_PORTRAITS == 0)
    }

    override fun fragmentClicked() {

    }

    override fun videoEnded() = false

    override fun goToPrevItem() {}

    override fun goToNextItem() {}

    override fun launchViewVideoIntent(path: String) {}

    override fun isSlideShowActive() = false

    override fun onEditClick(view: View) {
        super.onEditClick(view)
        mMedium?.path?.let { openEditor(it) }
    }

    override fun onPropertiesClick(view: View) {
        super.onPropertiesClick(view)
        showProperties()
    }

    override fun onShareClick(view: View) {
        super.onShareClick(view)
        shareImage()
    }

    override fun onDeleteClick(view: View) {
        super.onDeleteClick(view)
        checkDeleteConfirmation()
    }
}
