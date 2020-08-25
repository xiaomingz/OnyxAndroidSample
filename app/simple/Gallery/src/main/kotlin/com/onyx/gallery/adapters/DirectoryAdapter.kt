package com.onyx.gallery.adapters

import android.graphics.drawable.ColorDrawable
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.onyx.gallery.R
import com.onyx.gallery.extensions.config
import com.onyx.gallery.extensions.loadDirImage
import com.onyx.gallery.helpers.*
import com.onyx.gallery.interfaces.DirectoryOperationsListener
import com.onyx.gallery.models.Directory
import com.simplemobiletools.commons.activities.BaseSimpleActivity
import com.simplemobiletools.commons.adapters.MyRecyclerViewAdapter
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.views.FastScroller
import com.simplemobiletools.commons.views.MyRecyclerView
import kotlinx.android.synthetic.main.directory_item_grid.view.dir_check
import kotlinx.android.synthetic.main.directory_item_grid.view.dir_location
import kotlinx.android.synthetic.main.directory_item_grid.view.dir_lock
import kotlinx.android.synthetic.main.directory_item_grid.view.dir_name
import kotlinx.android.synthetic.main.directory_item_grid.view.dir_pin
import kotlinx.android.synthetic.main.directory_item_grid.view.dir_thumbnail
import kotlinx.android.synthetic.main.directory_item_grid.view.photo_cnt
import kotlinx.android.synthetic.main.directory_item_list.view.*

class DirectoryAdapter(activity: BaseSimpleActivity, var dirs: ArrayList<Directory>, val listener: DirectoryOperationsListener?, recyclerView: MyRecyclerView,
                       val isPickIntent: Boolean, fastScroller: FastScroller? = null, itemClick: (Any) -> Unit) :
        MyRecyclerViewAdapter(activity, recyclerView, fastScroller, itemClick) {

    private val config = activity.config
    private val isListViewType = config.viewTypeFolders == VIEW_TYPE_LIST
    private var showMediaCount = config.showMediaCount
    private var animateGifs = config.animateGifs
    private var cropThumbnails = config.cropThumbnails
    private var groupDirectSubfolders = config.groupDirectSubfolders
    private var currentDirectoriesHash = dirs.hashCode()
    private var lockedFolderPaths = ArrayList<String>()

    init {
        setupDragListener(true)
        fillLockedFolders()
    }

    override fun getActionMenuId() = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutType = if (isListViewType) R.layout.directory_item_list else R.layout.directory_item_grid
        return createViewHolder(layoutType, parent)
    }

    override fun onBindViewHolder(holder: MyRecyclerViewAdapter.ViewHolder, position: Int) {
        val dir = dirs.getOrNull(position) ?: return
        holder.bindView(dir, true, false) { itemView, adapterPosition ->
            setupView(itemView, dir)
        }
        bindViewHolder(holder)
    }

    override fun getItemCount() = dirs.size

    override fun prepareActionMode(menu: Menu) {}

    override fun actionItemPressed(id: Int) {

    }

    override fun getSelectableItemCount() = dirs.size

    override fun getIsItemSelectable(position: Int) = true

    override fun getItemSelectionKey(position: Int) = dirs.getOrNull(position)?.path?.hashCode()

    override fun getItemKeyPosition(key: Int) = dirs.indexOfFirst { it.path.hashCode() == key }

    override fun onActionModeCreated() {}

    override fun onActionModeDestroyed() {}

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        if (!activity.isDestroyed) {
            Glide.with(activity).clear(holder.itemView.dir_thumbnail!!)
        }
    }

    private fun fillLockedFolders() {
        lockedFolderPaths.clear()
        dirs.map { it.path }.filter { config.isFolderProtected(it) }.forEach {
            lockedFolderPaths.add(it)
        }
    }

    fun updateDirs(newDirs: ArrayList<Directory>) {
        val directories = newDirs.clone() as ArrayList<Directory>
        if (directories.hashCode() != currentDirectoriesHash) {
            currentDirectoriesHash = directories.hashCode()
            dirs = directories
            fillLockedFolders()
            notifyDataSetChanged()
            finishActMode()
        }
    }

    fun updateAnimateGifs(animateGifs: Boolean) {
        this.animateGifs = animateGifs
        notifyDataSetChanged()
    }

    fun updateCropThumbnails(cropThumbnails: Boolean) {
        this.cropThumbnails = cropThumbnails
        notifyDataSetChanged()
    }

    fun updateShowMediaCount(showMediaCount: Boolean) {
        this.showMediaCount = showMediaCount
        notifyDataSetChanged()
    }

    private fun setupView(view: View, directory: Directory) {
        val isSelected = selectedKeys.contains(directory.path.hashCode())
        view.apply {
            dir_name.text = if (groupDirectSubfolders && directory.subfoldersCount > 1) "${directory.name} (${directory.subfoldersCount})" else directory.name
            dir_path?.text = "${directory.path.substringBeforeLast("/")}/"
            photo_cnt.text = directory.subfoldersMediaCount.toString()
            val thumbnailType = when {
                directory.tmb.isVideoFast() -> TYPE_VIDEOS
                directory.tmb.isGif() -> TYPE_GIFS
                directory.tmb.isRawFast() -> TYPE_RAWS
                directory.tmb.isSvg() -> TYPE_SVGS
                else -> TYPE_IMAGES
            }

            dir_check?.beVisibleIf(isSelected)
            if (isSelected) {
                dir_check.background?.applyColorFilter(primaryColor)
            }

            if (lockedFolderPaths.contains(directory.path)) {
                dir_lock.beVisible()
                dir_lock.background = ColorDrawable(config.backgroundColor)
                dir_lock.applyColorFilter(config.backgroundColor.getContrastColor())
            } else {
                dir_lock.beGone()
                activity.loadDirImage(directory, dir_thumbnail, cropThumbnails)
            }

            dir_location.beVisibleIf(directory.location != LOCATION_INTERNAL)
            if (dir_location.isVisible()) {
                dir_location.setImageResource(if (directory.location == LOCATION_SD) R.drawable.ic_sd_card_vector else R.drawable.ic_usb_vector)
            }

            photo_cnt.beVisibleIf(showMediaCount)

            if (isListViewType) {
                dir_name.setTextColor(textColor)
                dir_path.setTextColor(textColor)
                photo_cnt.setTextColor(textColor)
                dir_pin.applyColorFilter(textColor)
                dir_location.applyColorFilter(textColor)
            }
        }
    }
}
