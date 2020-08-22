package com.onyx.gallery.activities

import android.app.Activity
import android.app.WallpaperManager
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.onyx.android.sdk.kui.dialog.ContextPopMenuView
import com.onyx.android.sdk.utils.ResManager
import com.onyx.gallery.R
import com.onyx.gallery.adapters.MediaAdapter
import com.onyx.gallery.asynctasks.GetMediaAsynctask
import com.onyx.gallery.databases.GalleryDatabase
import com.onyx.gallery.dialogs.FilterMediaDialog
import com.onyx.gallery.dialogs.ViewStyle
import com.onyx.gallery.dialogs.ViewStyleChangeDialog
import com.onyx.gallery.dialogs.ViewStyleChangeDialogType
import com.onyx.gallery.extensions.*
import com.onyx.gallery.helpers.*
import com.onyx.gallery.interfaces.MediaOperationsListener
import com.onyx.gallery.models.Medium
import com.onyx.gallery.models.ThumbnailItem
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.PERMISSION_WRITE_STORAGE
import com.simplemobiletools.commons.helpers.REQUEST_EDIT_IMAGE
import com.simplemobiletools.commons.helpers.SORT_BY_RANDOM
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import com.simplemobiletools.commons.models.FileDirItem
import com.simplemobiletools.commons.views.MyGridLayoutManager
import kotlinx.android.synthetic.main.activity_media.*
import kotlinx.android.synthetic.main.view_action_bar.*
import java.io.File
import java.io.IOException
import java.util.*

class MediaActivity : SimpleActivity(), MediaOperationsListener {
    private val LAST_MEDIA_CHECK_PERIOD = 3000L

    private var mPath = ""
    private var mIsGetImageIntent = false
    private var mIsGetVideoIntent = false
    private var mIsGetAnyIntent = false
    private var mIsGettingMedia = false
    private var mAllowPickingMultiple = false
    private var mShowAll = false
    private var mLoadedInitialPhotos = false
    private var mIsSearchOpen = false
    private var mLastSearchedText = ""
    private var mLatestMediaId = 0L
    private var mLatestMediaDateId = 0L
    private var mLastMediaHandler = Handler()
    private var mTempShowHiddenHandler = Handler()
    private var mCurrAsyncTask: GetMediaAsynctask? = null
    private var mSearchMenuItem: MenuItem? = null

    private var mStoredAnimateGifs = true
    private var mStoredCropThumbnails = true
    private var mStoredScrollHorizontally = true
    private var mStoredShowInfoBubble = true
    private var mStoredShowFileTypes = true
    private var mStoredTextColor = 0
    private var mStoredPrimaryColor = 0

    companion object {
        var mMedia = ArrayList<ThumbnailItem>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
        showImageListMenu()
        intent.apply {
            mIsGetImageIntent = getBooleanExtra(GET_IMAGE_INTENT, false)
            mIsGetVideoIntent = getBooleanExtra(GET_VIDEO_INTENT, false)
            mIsGetAnyIntent = getBooleanExtra(GET_ANY_INTENT, false)
            mAllowPickingMultiple = getBooleanExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        }

        try {
            mPath = intent.getStringExtra(DIRECTORY)
            tvTitle.setText(mPath)
        } catch (e: Exception) {
            showErrorToast(e)
            finish()
            return
        }

        storeStateVariables()

        if (mShowAll) {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            registerFileUpdateListener()
        }

        media_empty_text.setOnClickListener {
            showFilterMediaDialog()
        }

        updateWidgets()
    }

    override fun onStart() {
        super.onStart()
        mTempShowHiddenHandler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
        if (mStoredAnimateGifs != config.animateGifs) {
            getMediaAdapter()?.updateAnimateGifs(config.animateGifs)
        }

        if (mStoredCropThumbnails != config.cropThumbnails) {
            getMediaAdapter()?.updateCropThumbnails(config.cropThumbnails)
        }

        if (mStoredScrollHorizontally != config.scrollHorizontally) {
            mLoadedInitialPhotos = false
            media_grid.adapter = null
            getMedia()
        }

        if (mStoredShowFileTypes != config.showThumbnailFileTypes) {
            getMediaAdapter()?.updateShowFileTypes(config.showThumbnailFileTypes)
        }

        if (mStoredTextColor != config.textColor) {
            getMediaAdapter()?.updateTextColor(config.textColor)
        }

        if (mStoredPrimaryColor != config.primaryColor) {
            getMediaAdapter()?.updatePrimaryColor(config.primaryColor)
        }

        invalidateOptionsMenu()
        media_empty_text_label.setTextColor(config.textColor)
        media_empty_text.setTextColor(getAdjustedPrimaryColor())

        if (mMedia.isEmpty() || config.getFolderSorting(mPath) and SORT_BY_RANDOM == 0) {
            tryLoadGallery()
        }
    }

    override fun onPause() {
        super.onPause()
        mIsGettingMedia = false
        storeStateVariables()
        mLastMediaHandler.removeCallbacksAndMessages(null)

        if (!mMedia.isEmpty()) {
            mCurrAsyncTask?.stopFetching()
        }
    }

    override fun onStop() {
        super.onStop()
        mSearchMenuItem?.collapseActionView()

        if (config.temporarilyShowHidden || config.tempSkipDeleteConfirmation) {
            mTempShowHiddenHandler.postDelayed({
                config.temporarilyShowHidden = false
                config.tempSkipDeleteConfirmation = false
            }, SHOW_TEMP_HIDDEN_DURATION)
        } else {
            mTempShowHiddenHandler.removeCallbacksAndMessages(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (config.showAll && !isChangingConfigurations) {
            config.temporarilyShowHidden = false
            config.tempSkipDeleteConfirmation = false
            unregisterFileUpdateListener()
            GalleryDatabase.destroyInstance()
        }

        mTempShowHiddenHandler.removeCallbacksAndMessages(null)
        mMedia.clear()
    }

    private fun storeStateVariables() {
        config.apply {
            mStoredAnimateGifs = animateGifs
            mStoredCropThumbnails = cropThumbnails
            mStoredScrollHorizontally = scrollHorizontally
            mStoredShowInfoBubble = showInfoBubble
            mStoredShowFileTypes = showThumbnailFileTypes
            mStoredTextColor = textColor
            mStoredPrimaryColor = primaryColor
            mShowAll = showAll
        }
    }

    private fun searchQueryChanged(text: String) {
        ensureBackgroundThread {
            try {
                val filtered = mMedia.filter { it is Medium && it.name.contains(text, true) } as ArrayList
                filtered.sortBy { it is Medium && !it.name.startsWith(text, true) }
                val grouped = MediaFetcher(applicationContext).groupMedia(filtered as ArrayList<Medium>, mPath)
                runOnUiThread {
                    if (grouped.isEmpty()) {
                        media_empty_text_label.text = getString(R.string.no_items_found)
                        media_empty_text_label.beVisible()
                    } else {
                        media_empty_text_label.beGone()
                    }

                    getMediaAdapter()?.updateMedia(grouped)
                }
            } catch (ignored: Exception) {
            }
        }
    }

    private fun tryLoadGallery() {
        handlePermission(PERMISSION_WRITE_STORAGE) {
            if (it) {
                val dirName = when {
                    mPath == FAVORITES -> getString(R.string.favorites)
                    mPath == RECYCLE_BIN -> getString(R.string.recycle_bin)
                    mPath == config.OTGPath -> getString(R.string.usb)
                    else -> getHumanizedFilename(mPath)
                }
                tvTitle.setText(if (mShowAll) resources.getString(R.string.all_folders) else dirName)
                getMedia()
                setupLayoutManager()
            } else {
                toast(R.string.no_storage_permissions)
                finish()
            }
        }
    }

    private fun getMediaAdapter() = media_grid.adapter as? MediaAdapter

    private fun setupAdapter() {
        if (!mShowAll && isDirEmpty()) {
            return
        }

        val currAdapter = media_grid.adapter
        if (currAdapter == null) {
            MediaAdapter(this, mMedia.clone() as ArrayList<ThumbnailItem>, this, mIsGetImageIntent || mIsGetVideoIntent || mIsGetAnyIntent,
                    mAllowPickingMultiple, mPath, media_grid) {
                if (it is Medium && !isFinishing) {
                    itemClicked(it.path)
                }
            }.apply {
                media_grid.adapter = this
            }
            setupLayoutManager()
        } else if (mLastSearchedText.isEmpty()) {
            (currAdapter as MediaAdapter).updateMedia(mMedia)
        } else {
            searchQueryChanged(mLastSearchedText)
        }
    }

    private fun checkLastMediaChanged() {
        if (isDestroyed || config.getFolderSorting(mPath) and SORT_BY_RANDOM != 0) {
            return
        }

        mLastMediaHandler.removeCallbacksAndMessages(null)
        mLastMediaHandler.postDelayed({
            ensureBackgroundThread {
                val mediaId = getLatestMediaId()
                val mediaDateId = getLatestMediaByDateId()
                if (mLatestMediaId != mediaId || mLatestMediaDateId != mediaDateId) {
                    mLatestMediaId = mediaId
                    mLatestMediaDateId = mediaDateId
                    runOnUiThread {
                        getMedia()
                    }
                } else {
                    checkLastMediaChanged()
                }
            }
        }, LAST_MEDIA_CHECK_PERIOD)
    }

    private fun showFilterMediaDialog() {
        FilterMediaDialog(this) {
            mLoadedInitialPhotos = false
            media_grid.adapter = null
            getMedia()
        }
    }

    private fun deleteDirectoryIfEmpty() {
        val fileDirItem = FileDirItem(mPath, mPath.getFilenameFromPath(), true)
        if (config.deleteEmptyFolders && !fileDirItem.isDownloadsFolder() && fileDirItem.isDirectory && fileDirItem.getProperFileCount(this, true) == 0) {
            tryDeleteFileDirItem(fileDirItem, true, true)
        }
    }

    private fun getMedia() {
        if (mIsGettingMedia) {
            return
        }

        mIsGettingMedia = true
        if (mLoadedInitialPhotos) {
            startAsyncTask()
        } else {
            getCachedMedia(mPath, mIsGetVideoIntent, mIsGetImageIntent) {
                if (it.isNotEmpty()) {
                    gotMedia(it, true)
                }
                startAsyncTask()
            }
        }

        mLoadedInitialPhotos = true
    }

    private fun startAsyncTask() {
        mCurrAsyncTask?.stopFetching()
        mCurrAsyncTask = GetMediaAsynctask(applicationContext, mPath, mIsGetImageIntent, mIsGetVideoIntent, mShowAll) {
            ensureBackgroundThread {
                val oldMedia = mMedia.clone() as ArrayList<ThumbnailItem>
                val newMedia = it
                try {
                    gotMedia(newMedia, false)
                    oldMedia.filter { !newMedia.contains(it) }.mapNotNull { it as? Medium }.filter { !getDoesFilePathExist(it.path) }.forEach {
                        mediaDB.deleteMediumPath(it.path)
                    }
                } catch (e: Exception) {
                }
            }
        }

        mCurrAsyncTask!!.execute()
    }

    private fun isDirEmpty(): Boolean {
        return if (mMedia.size <= 0 && config.filterMedia > 0) {
            if (mPath != FAVORITES && mPath != RECYCLE_BIN) {
                deleteDirectoryIfEmpty()
                deleteDBDirectory()
            }

            if (mPath == FAVORITES) {
                ensureBackgroundThread {
                    directoryDao.deleteDirPath(FAVORITES)
                }
            }

            finish()
            true
        } else {
            false
        }
    }

    private fun deleteDBDirectory() {
        ensureBackgroundThread {
            try {
                directoryDao.deleteDirPath(mPath)
            } catch (ignored: Exception) {
            }
        }
    }

    private fun toggleTemporarilyShowHidden(show: Boolean) {
        mLoadedInitialPhotos = false
        config.temporarilyShowHidden = show
        getMedia()
        invalidateOptionsMenu()
    }

    private fun setupLayoutManager() {
        val viewType = config.getFolderViewType(if (mShowAll) SHOW_ALL else mPath)
        if (viewType == VIEW_TYPE_GRID) {
            setupGridLayoutManager()
        } else {
            setupListLayoutManager()
        }
    }

    private fun setupGridLayoutManager() {
        val layoutManager = media_grid.layoutManager as MyGridLayoutManager
        (media_grid.layoutParams as RelativeLayout.LayoutParams).apply {
            topMargin = 0
            bottomMargin = 0
        }

        if (config.scrollHorizontally) {
            layoutManager.orientation = RecyclerView.HORIZONTAL
        } else {
            layoutManager.orientation = RecyclerView.VERTICAL
        }

        layoutManager.spanCount = config.mediaColumnCnt
        val adapter = getMediaAdapter()
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter?.isASectionTitle(position) == true) {
                    layoutManager.spanCount
                } else {
                    1
                }
            }
        }
    }

    private fun setupListLayoutManager() {
        val layoutManager = media_grid.layoutManager as MyGridLayoutManager
        layoutManager.spanCount = 1
        layoutManager.orientation = RecyclerView.VERTICAL

        (media_grid.layoutParams as RelativeLayout.LayoutParams).apply {
            topMargin = 0
            bottomMargin = 0
        }
    }

    private fun isSetWallpaperIntent() = intent.getBooleanExtra(SET_WALLPAPER_INTENT, false)

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == REQUEST_EDIT_IMAGE) {
            if (resultCode == Activity.RESULT_OK && resultData != null) {
                mMedia.clear()
                refreshItems()
            }
        }
        super.onActivityResult(requestCode, resultCode, resultData)
    }

    private fun itemClicked(path: String) {
        if (isSetWallpaperIntent()) {
            toast(R.string.setting_wallpaper)

            val wantedWidth = wallpaperDesiredMinimumWidth
            val wantedHeight = wallpaperDesiredMinimumHeight
            val ratio = wantedWidth.toFloat() / wantedHeight

            val options = RequestOptions()
                    .override((wantedWidth * ratio).toInt(), wantedHeight)
                    .fitCenter()

            Glide.with(this)
                    .asBitmap()
                    .load(File(path))
                    .apply(options)
                    .into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            try {
                                WallpaperManager.getInstance(applicationContext).setBitmap(resource)
                                setResult(Activity.RESULT_OK)
                            } catch (ignored: IOException) {
                            }

                            finish()
                        }
                    })
        } else if (mIsGetImageIntent || mIsGetVideoIntent || mIsGetAnyIntent) {
            Intent().apply {
                data = Uri.parse(path)
                setResult(Activity.RESULT_OK, this)
            }
            finish()
        } else {
            val isVideo = path.isVideoFast()
            if (isVideo) {
                val extras = HashMap<String, Boolean>()
                extras[SHOW_FAVORITES] = mPath == FAVORITES
                openPath(path, false, extras)
            } else {
                Intent(this, ViewPagerActivity::class.java).apply {
                    putExtra(PATH, path)
                    putExtra(SHOW_ALL, mShowAll)
                    putExtra(SHOW_FAVORITES, mPath == FAVORITES)
                    putExtra(SHOW_RECYCLE_BIN, mPath == RECYCLE_BIN)
                    startActivity(this)
                }
            }
        }
    }

    private fun gotMedia(media: ArrayList<ThumbnailItem>, isFromCache: Boolean) {
        mIsGettingMedia = false
        checkLastMediaChanged()
        mMedia = media

        runOnUiThread {
            media_empty_text_label.beVisibleIf(media.isEmpty() && !isFromCache)
            media_empty_text.beVisibleIf(media.isEmpty() && !isFromCache)

            if (media_empty_text_label.isVisible()) {
                media_empty_text_label.text = getString(R.string.no_media_with_filters)
            }
            media_grid.beVisibleIf(media_empty_text_label.isGone())

            setupAdapter()
        }

        mLatestMediaId = getLatestMediaId()
        mLatestMediaDateId = getLatestMediaByDateId()
        if (!isFromCache) {
            val mediaToInsert = (mMedia).filter { it is Medium && it.deletedTS == 0L }.map { it as Medium }
            try {
                mediaDB.insertAll(mediaToInsert)
            } catch (e: Exception) {
            }
        }
    }

    override fun tryDeleteFiles(fileDirItems: ArrayList<FileDirItem>) {
        val filtered = fileDirItems.filter { !getIsPathDirectory(it.path) && it.path.isMediaFile() } as ArrayList
        if (filtered.isEmpty()) {
            return
        }

        if (config.useRecycleBin && !filtered.first().path.startsWith(recycleBinPath)) {
            val movingItems = resources.getQuantityString(R.plurals.moving_items_into_bin, filtered.size, filtered.size)
            toast(movingItems)

            movePathsInRecycleBin(filtered.map { it.path } as ArrayList<String>) {
                if (it) {
                    deleteFilteredFiles(filtered)
                } else {
                    toast(R.string.unknown_error_occurred)
                }
            }
        } else {
            val deletingItems = resources.getQuantityString(R.plurals.deleting_items, filtered.size, filtered.size)
            toast(deletingItems)
            deleteFilteredFiles(filtered)
        }
    }

    private fun deleteFilteredFiles(filtered: ArrayList<FileDirItem>) {
        deleteFiles(filtered) {
            if (!it) {
                toast(R.string.unknown_error_occurred)
                return@deleteFiles
            }

            mMedia.removeAll { filtered.map { it.path }.contains((it as? Medium)?.path) }

            ensureBackgroundThread {
                val useRecycleBin = config.useRecycleBin
                filtered.forEach {
                    if (it.path.startsWith(recycleBinPath) || !useRecycleBin) {
                        deleteDBPath(it.path)
                    }
                }
            }

            if (mMedia.isEmpty()) {
                deleteDirectoryIfEmpty()
                deleteDBDirectory()
                finish()
            }
        }
    }

    override fun refreshItems() {
        getMedia()
    }

    override fun selectedPaths(paths: ArrayList<String>) {
        Intent().apply {
            putExtra(PICKED_PATHS, paths)
            setResult(Activity.RESULT_OK, this)
        }
        finish()
    }

    override fun onMoreClick(view: View) {
        val builder = ContextPopMenuView.Builder(this)
        builder.setMenuLayoutId(R.menu.menu_main)
                .setOffsetXY(ResManager.getDimens(R.dimen.more_pop_offset_x), ResManager.getDimens(R.dimen.more_pop_offset_y))
                .setTarget(ivMore)
                .setItemListener { parent, view, position, id ->
                    showViewStyleChangeDialog()
                }
                .create()
                .showPopView()
    }

    private fun showViewStyleChangeDialog() {
        var viewStyle = ViewStyle.GRID
        val currViewType = config.getFolderViewType(mPath)
        if (currViewType == VIEW_TYPE_LIST) {
            viewStyle = ViewStyle.LIST
        }
        ViewStyleChangeDialog(ViewStyleChangeDialogType.WITH_USE_FOR_THIS_FOLDER, viewStyle, isUseForThisFolder = config.hasCustomViewType(mPath)) { viewStyle, isGroupByDirectory, isUseForThisFolder ->
            val viewType = if (viewStyle == ViewStyle.GRID) VIEW_TYPE_GRID else VIEW_TYPE_LIST
            if (isUseForThisFolder) {
                config.saveFolderViewType(mPath, viewType)
            } else {
                config.removeFolderViewType(mPath)
            }
            config.viewTypeFiles = viewType
            invalidateOptionsMenu()
            setupLayoutManager()
            media_grid.adapter = null
            setupAdapter()
        }.show(supportFragmentManager, ViewStyleChangeDialog::class.java.simpleName)
    }

}
