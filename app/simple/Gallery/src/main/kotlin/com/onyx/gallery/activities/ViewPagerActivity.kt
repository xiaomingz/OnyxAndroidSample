package com.onyx.gallery.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Html
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.onyx.android.sdk.api.device.epd.EpdController
import com.onyx.android.sdk.api.device.epd.UpdateMode
import com.onyx.android.sdk.utils.EventBusUtils
import com.onyx.gallery.App
import com.onyx.gallery.BuildConfig
import com.onyx.gallery.R
import com.onyx.gallery.action.ShareAction
import com.onyx.gallery.adapters.MyPagerAdapter
import com.onyx.gallery.asynctasks.GetMediaAsynctask
import com.onyx.gallery.dialogs.ConfirmDialog
import com.onyx.gallery.dialogs.PropertiesDialog
import com.onyx.gallery.event.ui.ApplyFastModeEvent
import com.onyx.gallery.extensions.*
import com.onyx.gallery.fragments.PhotoFragment
import com.onyx.gallery.fragments.VideoFragment
import com.onyx.gallery.fragments.ViewPagerFragment
import com.onyx.gallery.helpers.*
import com.onyx.gallery.models.Medium
import com.onyx.gallery.models.ThumbnailItem
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.*
import com.simplemobiletools.commons.models.FileDirItem
import kotlinx.android.synthetic.main.activity_medium.*
import kotlinx.android.synthetic.main.view_action_bar.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.util.*

class ViewPagerActivity : SimpleActivity(), ViewPager.OnPageChangeListener, ViewPagerFragment.FragmentListener {
    @Volatile
    private var inFastMode = false
    private val TAG = this::class.java.simpleName

    private val REQUEST_VIEW_VIDEO = 1

    private var mPath = ""
    private var mDirectory = ""
    private var mIsFullScreen = false
    private var mPos = -1
    private var mShowAll = false
    private var mIsSlideshowActive = false
    private var mPrevHashcode = 0

    private var mSlideshowHandler = Handler()
    private var mSlideshowInterval = SLIDESHOW_DEFAULT_INTERVAL
    private var mSlideshowMoveBackwards = false
    private var mSlideshowMedia = mutableListOf<Medium>()
    private var mAreSlideShowMediaVisible = false

    private var mIsOrientationLocked = false

    private var mMediaFiles = ArrayList<Medium>()
    private var mIgnoredPaths = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_medium)
        (MediaActivity.mMedia.clone() as ArrayList<ThumbnailItem>).filter { it is Medium }.mapTo(mMediaFiles) { it as Medium }

        handlePermission(PERMISSION_WRITE_STORAGE) {
            if (it) {
                initViewPager()
                EventBusUtils.ensureRegister(App.eventBus, this)
            } else {
                toast(R.string.no_storage_permissions)
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!hasPermission(PERMISSION_WRITE_STORAGE)) {
            finish()
            return
        }
        setupOrientation()

        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val filename = getCurrentMedium()?.name ?: mPath.getFilenameFromPath()
        supportActionBar?.title = Html.fromHtml("<font color=#FFFFFF'>$filename</font>")
        window.statusBarColor = Color.TRANSPARENT
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusUtils.ensureUnregister(App.eventBus, this)
        ensureQuitFastMode()
        if (intent.extras?.containsKey(IS_VIEW_INTENT) == true) {
            config.temporarilyShowHidden = false
        }

        if (config.isThirdPartyIntent) {
            config.isThirdPartyIntent = false

            if (intent.extras == null || !intent.getBooleanExtra(IS_FROM_GALLERY, false)) {
                mMediaFiles.clear()
            }
        }
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
            getCurrentFragment()?.let { fragment ->
                if (fragment is VideoFragment && fragment.mIsPlaying) {
                    return
                }
            }
            EpdController.applyApplicationFastMode(TAG, false, true, UpdateMode.ANIMATION_QUALITY, Int.MAX_VALUE)
            inFastMode = false
        }
    }

    private fun shareImage(imagePath: String) {
        ShareAction(this, imagePath).execute(null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == REQUEST_EDIT_IMAGE && resultCode == Activity.RESULT_OK && resultData != null) {
            mPos = -1
            mPrevHashcode = 0
            refreshViewPager()
        } else if (requestCode == REQUEST_SET_AS && resultCode == Activity.RESULT_OK) {
            toast(R.string.wallpaper_set_successfully)
        } else if (requestCode == REQUEST_VIEW_VIDEO && resultCode == Activity.RESULT_OK && resultData != null) {
            if (resultData.getBooleanExtra(GO_TO_NEXT_ITEM, false)) {
                goToNextItem()
            } else if (resultData.getBooleanExtra(GO_TO_PREV_ITEM, false)) {
                goToPrevItem()
            }
        }
        super.onActivityResult(requestCode, resultCode, resultData)
    }

    private fun initViewPager() {
        val uri = intent.data
        if (uri != null) {
            var cursor: Cursor? = null
            try {
                val proj = arrayOf(MediaStore.Images.Media.DATA)
                cursor = contentResolver.query(uri, proj, null, null, null)
                if (cursor?.moveToFirst() == true) {
                    mPath = cursor.getStringValue(MediaStore.Images.Media.DATA)
                }
            } finally {
                cursor?.close()
            }
        } else {
            try {
                mPath = intent.getStringExtra(PATH)
                mShowAll = config.showAll
            } catch (e: Exception) {
                showErrorToast(e)
                finish()
                return
            }
        }

        if (intent.extras?.containsKey(REAL_FILE_PATH) == true) {
            mPath = intent.extras!!.getString(REAL_FILE_PATH)!!
        }

        if (mPath.isEmpty()) {
            toast(R.string.unknown_error_occurred)
            finish()
            return
        }

        if (mPath.isPortrait() && getPortraitPath() == "") {
            val newIntent = Intent(this, ViewPagerActivity::class.java)
            newIntent.putExtras(intent!!.extras!!)
            newIntent.putExtra(PORTRAIT_PATH, mPath)
            newIntent.putExtra(PATH, "${mPath.getParentPath().getParentPath()}/${mPath.getFilenameFromPath()}")

            startActivity(newIntent)
            finish()
            return
        }

        if (!getDoesFilePathExist(mPath) && getPortraitPath() == "") {
            finish()
            return
        }

        if (intent.extras?.containsKey(IS_VIEW_INTENT) == true) {
            if (isShowHiddenFlagNeeded()) {
                if (!config.isHiddenPasswordProtectionOn) {
                    config.temporarilyShowHidden = true
                }
            }

            config.isThirdPartyIntent = true
        }

        val isShowingFavorites = intent.getBooleanExtra(SHOW_FAVORITES, false)
        val isShowingRecycleBin = intent.getBooleanExtra(SHOW_RECYCLE_BIN, false)
        mDirectory = when {
            isShowingFavorites -> FAVORITES
            isShowingRecycleBin -> RECYCLE_BIN
            else -> mPath.getParentPath()
        }
        supportActionBar?.title = mPath.getFilenameFromPath()

        view_pager.onGlobalLayout {
            if (!isDestroyed) {
                if (mMediaFiles.isNotEmpty()) {
                    gotMedia(mMediaFiles as ArrayList<ThumbnailItem>)
                }
            }
        }

        refreshViewPager()
        view_pager.offscreenPageLimit = 2

        if (config.blackBackground) {
            view_pager.background = ColorDrawable(Color.BLACK)
        }

        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            mIsFullScreen = if (visibility and View.SYSTEM_UI_FLAG_LOW_PROFILE == 0) {
                false
            } else {
                visibility and View.SYSTEM_UI_FLAG_FULLSCREEN != 0
            }
        }

        if (intent.action == "com.android.camera.action.REVIEW") {
            ensureBackgroundThread {
                if (mediaDB.getMediaFromPath(mPath).isEmpty()) {
                    val type = when {
                        mPath.isVideoFast() -> TYPE_VIDEOS
                        mPath.isGif() -> TYPE_GIFS
                        mPath.isSvg() -> TYPE_SVGS
                        mPath.isRawFast() -> TYPE_RAWS
                        mPath.isPortrait() -> TYPE_PORTRAITS
                        else -> TYPE_IMAGES
                    }

                    val isFavorite = favoritesDB.isFavorite(mPath)
                    val duration = if (type == TYPE_VIDEOS) mPath.getVideoDuration() else 0
                    val ts = System.currentTimeMillis()
                    val medium = Medium(null, mPath.getFilenameFromPath(), mPath, mPath.getParentPath(), ts, ts, File(mPath).length(), type, duration, isFavorite, 0)
                    mediaDB.insert(medium)
                }
            }
        }
    }

    private fun setupOrientation() {
        if (!mIsOrientationLocked) {
            if (config.screenRotation == ROTATE_BY_DEVICE_ROTATION) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
            } else if (config.screenRotation == ROTATE_BY_SYSTEM_SETTING) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }

    private fun updatePagerItems(media: MutableList<Medium>) {
        val pagerAdapter = MyPagerAdapter(this, supportFragmentManager, media)
        if (!isDestroyed) {
            pagerAdapter.shouldInitFragment = mPos < 5
            view_pager.apply {
                adapter = pagerAdapter
                pagerAdapter.shouldInitFragment = true
                setCurrentItem(mPos, false)
                removeOnPageChangeListener(this@ViewPagerActivity)
                addOnPageChangeListener(this@ViewPagerActivity)
                updateMenu()
                updateRefreshModel()
            }
        }
    }

    private fun goToNextMedium(forward: Boolean) {
        val oldPosition = view_pager.currentItem
        val newPosition = if (forward) oldPosition + 1 else oldPosition - 1
        if (newPosition == -1 || newPosition > view_pager.adapter!!.count - 1) {
            slideshowEnded(forward)
        } else {
            view_pager.setCurrentItem(newPosition, false)
        }
    }

    private fun slideshowEnded(forward: Boolean) {
        if (config.loopSlideshow) {
            if (forward) {
                view_pager.setCurrentItem(0, false)
            } else {
                view_pager.setCurrentItem(view_pager.adapter!!.count - 1, false)
            }
        } else {
            toast(R.string.slideshow_ended)
        }
    }

    private fun scheduleSwipe() {
        mSlideshowHandler.removeCallbacksAndMessages(null)
        if (mIsSlideshowActive) {
            if (getCurrentMedium()!!.isImage() || getCurrentMedium()!!.isGIF() || getCurrentMedium()!!.isPortrait()) {
                mSlideshowHandler.postDelayed({
                    if (mIsSlideshowActive && !isDestroyed) {
                        swipeToNextMedium()
                    }
                }, mSlideshowInterval * 1000L)
            } else {
                (getCurrentFragment() as? VideoFragment)!!.playVideo()
            }
        }
    }

    private fun swipeToNextMedium() {
        if (config.slideshowAnimation == SLIDESHOW_ANIMATION_NONE) {
            goToNextMedium(!mSlideshowMoveBackwards)
        }
    }

    private fun getCurrentPhotoFragment() = getCurrentFragment() as? PhotoFragment

    private fun getPortraitPath() = intent.getStringExtra(PORTRAIT_PATH) ?: ""

    private fun isShowHiddenFlagNeeded(): Boolean {
        val file = File(mPath)
        if (file.isHidden) {
            return true
        }

        var parent = file.parentFile ?: return false
        while (true) {
            if (parent.isHidden || parent.list()?.any { it.startsWith(NOMEDIA) } == true) {
                return true
            }

            if (parent.absolutePath == "/") {
                break
            }
            parent = parent.parentFile ?: return false
        }

        return false
    }

    private fun getCurrentFragment() = (view_pager.adapter as? MyPagerAdapter)?.getCurrentFragment(view_pager.currentItem)

    private fun showProperties() {
        if (getCurrentMedium() != null) {
            PropertiesDialog(this, getCurrentPath()).show(supportFragmentManager, PropertiesDialog::class.java.simpleName)
        }
    }

    private fun checkDeleteConfirmation() {
        if (getCurrentMedium() == null) {
            return
        }
        askConfirmDelete()
    }

    private fun askConfirmDelete() {
        val filename = "\"${getCurrentPath().getFilenameFromPath()}\""

        val baseString = R.string.deletion_confirmation

        val message = String.format(resources.getString(baseString), filename)
        ConfirmDialog(message) {
            config.tempSkipDeleteConfirmation = false
            deleteConfirmed()
        }.show(supportFragmentManager, ConfirmDialog::class.java.simpleName)
    }

    private fun deleteConfirmed() {
        val path = getCurrentMedia().getOrNull(mPos)?.path ?: return
        if (getIsPathDirectory(path) || !path.isMediaFile()) {
            return
        }

        val fileDirItem = FileDirItem(path, path.getFilenameFromPath())
        handleDeletion(fileDirItem)
    }

    private fun handleDeletion(fileDirItem: FileDirItem) {
        mIgnoredPaths.add(fileDirItem.path)
        val media = mMediaFiles.filter { !mIgnoredPaths.contains(it.path) } as ArrayList<ThumbnailItem>
        runOnUiThread {
            gotMedia(media, true)
        }

        tryDeleteFileDirItem(fileDirItem, false, true) {
            mIgnoredPaths.remove(fileDirItem.path)
            deleteDirectoryIfEmpty()
        }
    }

    private fun isDirEmpty(media: ArrayList<Medium>): Boolean {
        return if (media.isEmpty()) {
            deleteDirectoryIfEmpty()
            finish()
            true
        } else {
            false
        }
    }

    private fun refreshViewPager() {
        if (config.getFolderSorting(mDirectory) and SORT_BY_RANDOM == 0) {
            GetMediaAsynctask(applicationContext, mDirectory, false, false, mShowAll) {
                gotMedia(it)
            }.execute()
        }
    }

    private fun gotMedia(thumbnailItems: ArrayList<ThumbnailItem>, ignorePlayingVideos: Boolean = false) {
        val media = thumbnailItems.asSequence().filter { it is Medium && !mIgnoredPaths.contains(it.path) }.map { it as Medium }.toMutableList() as ArrayList<Medium>
        if (isDirEmpty(media) || media.hashCode() == mPrevHashcode) {
            return
        }

        if (!ignorePlayingVideos && (getCurrentFragment() as? VideoFragment)?.mIsPlaying == true) {
            return
        }

        mPrevHashcode = media.hashCode()
        if (mMediaFiles.size != media.size) {
            mPos = 0
        }
        mMediaFiles = media
        mPos = if (mPos == -1) {
            getPositionInList(media)
        } else {
            Math.min(mPos, mMediaFiles.size - 1)
        }

        updateActionbarTitle()
        updatePagerItems(mMediaFiles.toMutableList())
        updateFavorites()
        invalidateOptionsMenu()
        checkOrientation()
    }

    private fun getPositionInList(items: MutableList<Medium>): Int {
        mPos = 0
        for ((i, medium) in items.withIndex()) {
            val portraitPath = getPortraitPath()
            if (portraitPath != "") {
                val portraitPaths = File(portraitPath).parentFile?.list()
                if (portraitPaths != null) {
                    for (path in portraitPaths) {
                        if (medium.name == path) {
                            return i
                        }
                    }
                }
            } else if (medium.path == mPath) {
                return i
            }
        }
        return mPos
    }

    private fun deleteDirectoryIfEmpty() {
        val fileDirItem = FileDirItem(mDirectory, mDirectory.getFilenameFromPath(), File(mDirectory).isDirectory)
        if (config.deleteEmptyFolders && !fileDirItem.isDownloadsFolder() && fileDirItem.isDirectory && fileDirItem.getProperFileCount(this, true) == 0) {
            tryDeleteFileDirItem(fileDirItem, true, true)
            scanPathRecursively(mDirectory)
        }
    }

    private fun checkOrientation() {
        if (!mIsOrientationLocked && config.screenRotation == ROTATE_BY_ASPECT_RATIO) {
            var flipSides = false
            try {
                val pathToLoad = getCurrentPath()
                val exif = ExifInterface(pathToLoad)
                val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)
                flipSides = orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270
            } catch (e: Exception) {
            }
            val resolution = applicationContext.getResolution(getCurrentPath()) ?: return
            val width = if (flipSides) resolution.y else resolution.x
            val height = if (flipSides) resolution.x else resolution.y
            if (width > height) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else if (width < height) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }

    override fun fragmentClicked() {
    }

    override fun videoEnded(): Boolean {
        if (mIsSlideshowActive) {
            swipeToNextMedium()
        }
        return mIsSlideshowActive
    }

    override fun isSlideShowActive() = mIsSlideshowActive

    override fun goToPrevItem() {
        view_pager.setCurrentItem(view_pager.currentItem - 1, false)
        checkOrientation()
    }

    override fun goToNextItem() {
        view_pager.setCurrentItem(view_pager.currentItem + 1, false)
        checkOrientation()
    }

    private fun updateMenu() {
        getCurrentMedium()?.run {
            if (isGIF()) {
                showGIFBrowseMenu()
            } else if (isVideo()) {
                showVideoBrowseMenu()
            } else {
                showImageBrowseMenu()
            }
        }
    }

    private fun updateRefreshModel() {
        getCurrentMedium()?.run {
            if (isGIF()) {
                App.eventBus.post(ApplyFastModeEvent(true))
            } else {
                App.eventBus.post(ApplyFastModeEvent(false))
            }
        }
    }

    override fun launchViewVideoIntent(path: String) {
        ensureBackgroundThread {
            val newUri = getFinalUriFromPath(path, BuildConfig.APPLICATION_ID)
                    ?: return@ensureBackgroundThread
            val mimeType = getUriMimeType(path, newUri)
            Intent().apply {
                action = Intent.ACTION_VIEW
                setDataAndType(newUri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                putExtra(IS_FROM_GALLERY, true)
                putExtra(REAL_FILE_PATH, path)
                putExtra(SHOW_PREV_ITEM, view_pager.currentItem != 0)
                putExtra(SHOW_NEXT_ITEM, view_pager.currentItem != mMediaFiles.size - 1)

                if (resolveActivity(packageManager) != null) {
                    try {
                        startActivityForResult(this, REQUEST_VIEW_VIDEO)
                    } catch (e: NullPointerException) {
                        showErrorToast(e)
                    }
                } else {
                    if (!tryGenericMimeType(this, mimeType, newUri)) {
                        toast(R.string.no_app_found)
                    }
                }
            }
        }
    }

    private fun updateActionbarTitle() {
        runOnUiThread {
            if (mPos < getCurrentMedia().size) {
                tvTitle.setText(getCurrentMedia()[mPos].path.getFilenameFromPath())
            }
        }
    }

    private fun getCurrentMedium(): Medium? {
        return if (getCurrentMedia().isEmpty() || mPos == -1) {
            null
        } else {
            getCurrentMedia()[Math.min(mPos, getCurrentMedia().size - 1)]
        }
    }

    private fun getCurrentMedia() = if (mAreSlideShowMediaVisible) mSlideshowMedia else mMediaFiles

    private fun getCurrentPath() = getCurrentMedium()?.path ?: ""

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        if (mPos != position) {
            mPos = position
            updateMenu()
            updateRefreshModel()
            updateActionbarTitle()
            updateFavorites()
            invalidateOptionsMenu()
            scheduleSwipe()
        }
    }

    private fun updateFavorites() {
        getCurrentMedium()?.run {
            ivFavorites.isActivated = isFavorite
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
        if (state == ViewPager.SCROLL_STATE_IDLE && getCurrentMedium() != null) {
            checkOrientation()
        }
    }

    override fun onEditClick(view: View) {
        super.onEditClick(view)
        openEditor(getCurrentPath())
    }

    override fun onPropertiesClick(view: View) {
        super.onPropertiesClick(view)
        showProperties()
    }

    override fun onShareClick(view: View) {
        super.onShareClick(view)
        shareImage(getCurrentPath())
    }

    override fun onDeleteClick(view: View) {
        super.onDeleteClick(view)
        checkDeleteConfirmation()
    }

}
