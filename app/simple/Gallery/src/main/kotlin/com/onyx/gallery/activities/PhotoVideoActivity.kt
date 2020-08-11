package com.onyx.gallery.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.onyx.android.sdk.utils.StringUtils
import com.onyx.gallery.BuildConfig
import com.onyx.gallery.R
import com.onyx.gallery.action.ShareAction
import com.onyx.gallery.dialogs.DeleteWithRememberDialog
import com.onyx.gallery.dialogs.PropertiesDialog
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
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import com.simplemobiletools.commons.models.FileDirItem
import kotlinx.android.synthetic.main.bottom_actions.*
import kotlinx.android.synthetic.main.fragment_holder.*
import kotlinx.android.synthetic.main.view_action_bar.*
import java.io.File
import java.io.FileInputStream

open class PhotoVideoActivity : SimpleActivity(), ViewPagerFragment.FragmentListener {
    private var mMedium: Medium? = null
    private var mIsFullScreen = false
    private var mIsFromGallery = false
    private var mFragment: ViewPagerFragment? = null
    private var mUri: Uri? = null

    var mIsVideo = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_holder)
        showPhotoVideoBrowseMenu()
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
        configActionBar()
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
                        bottom_actions.beGone()
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
                bottom_actions.beGone()
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
                    bottom_actions.beGone()
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
        tvTitle.setText(mMedium!!.name)
        initFavorites(mMedium!!)
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
        initBottomActions()
    }

    private fun initFavorites(medium: Medium) {
        ensureBackgroundThread {
            getFavoritePaths().forEach { favoritePath ->
                if (StringUtils.isEquals(favoritePath, medium.path)) {
                    runOnUiThread { ivFavorites.isActivated = true }
                }
            }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.photo_video_menu, menu)
        mMedium?.apply {
            menu.findItem(R.id.menu_add_to_favorites).isVisible = !isFavorite
            menu.findItem(R.id.menu_remove_from_favorites).isVisible = isFavorite
        }
        updateMenuItemColors(menu, false, Color.BLACK)
        return true
    }

    private fun configActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (mMedium == null || mUri == null) {
            return true
        }

        when (item.itemId) {
            R.id.menu_add_to_favorites -> toggleFavorite()
            R.id.menu_remove_from_favorites -> toggleFavorite()
            R.id.menu_edit -> openImageEditor()
            R.id.menu_properties -> showProperties()
            R.id.menu_share -> shareImage()
            R.id.menu_delete -> checkDeleteConfirmation()
            R.id.menu_set_as -> setAs(mUri!!.toString())
            R.id.menu_open_with -> openPath(mUri!!.toString(), true)
            R.id.menu_properties -> showProperties()
            R.id.menu_show_on_map -> showFileOnMap(mUri!!.toString())
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun openImageEditor() {
        mMedium?.run { openEditor(path) }
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

    private fun toggleFavorite() {
        val medium = mMedium ?: return
        medium.isFavorite = !medium.isFavorite
        updateFavorites()
        ensureBackgroundThread {
            updateFavorite(medium.path, medium.isFavorite)
            invalidateOptionsMenu()
        }
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

    private fun initBottomActions() {
        initBottomActionButtons()
    }


    private fun initBottomActionButtons() {
        arrayListOf(bottom_favorite, bottom_delete, bottom_rotate, bottom_properties, bottom_change_orientation, bottom_slideshow, bottom_show_on_map,
                bottom_toggle_file_visibility, bottom_rename, bottom_copy, bottom_move, bottom_resize).forEach {
            it.beGone()
        }

        val visibleBottomActions = if (config.bottomActions) config.visibleBottomActions else 0
        bottom_edit.beVisibleIf(visibleBottomActions and BOTTOM_ACTION_EDIT != 0 && mMedium?.isImage() == true)
        bottom_edit.setOnClickListener {
            if (mUri != null && bottom_actions.alpha == 1f) {
                mMedium?.path?.let { path -> openEditor(path) }
            }
        }

        bottom_share.beVisibleIf(visibleBottomActions and BOTTOM_ACTION_SHARE != 0)
        bottom_share.setOnClickListener {
            if (mMedium != null && bottom_actions.alpha == 1f) {
                ShareAction(this, mMedium!!.path).execute(null)
            }
        }

        bottom_set_as.beVisibleIf(visibleBottomActions and BOTTOM_ACTION_SET_AS != 0 && mMedium?.isImage() == true)
        bottom_set_as.setOnClickListener {
            setAs(mUri!!.toString())
        }

        bottom_show_on_map.beVisibleIf(visibleBottomActions and BOTTOM_ACTION_SHOW_ON_MAP != 0)
        bottom_show_on_map.setOnClickListener {
            showFileOnMap(mUri!!.toString())
        }
    }

    override fun fragmentClicked() {
        mIsFullScreen = !mIsFullScreen
        if (mIsFullScreen) {
            hideSystemUI(true)
        } else {
            showSystemUI(true)
        }

        val newAlpha = if (mIsFullScreen) 0f else 1f
        top_shadow.animate().alpha(newAlpha).start()
        if (!bottom_actions.isGone()) {
            bottom_actions.animate().alpha(newAlpha).start()
        }
    }

    override fun videoEnded() = false

    override fun goToPrevItem() {}

    override fun goToNextItem() {}

    override fun launchViewVideoIntent(path: String) {}

    override fun isSlideShowActive() = false

    private fun updateFavorites() {
        mMedium?.run {
            ivFavorites.isActivated = isFavorite
        }
    }

    override fun onFavoritesClick(view: View) {
        super.onFavoritesClick(view)
        toggleFavorite()
    }

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
