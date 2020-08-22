package com.onyx.gallery.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.onyx.gallery.R
import com.onyx.gallery.activities.ViewPagerActivity
import com.onyx.gallery.dialogs.DeleteWithRememberDialog
import com.onyx.gallery.extensions.*
import com.onyx.gallery.helpers.*
import com.onyx.gallery.interfaces.MediaOperationsListener
import com.onyx.gallery.models.Medium
import com.onyx.gallery.models.ThumbnailItem
import com.onyx.gallery.models.ThumbnailSection
import com.simplemobiletools.commons.activities.BaseSimpleActivity
import com.simplemobiletools.commons.adapters.MyRecyclerViewAdapter
import com.simplemobiletools.commons.dialogs.RenameDialog
import com.simplemobiletools.commons.dialogs.RenameItemDialog
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import com.simplemobiletools.commons.models.FileDirItem
import com.simplemobiletools.commons.views.FastScroller
import com.simplemobiletools.commons.views.MyRecyclerView
import kotlinx.android.synthetic.main.photo_video_item_grid.view.*
import kotlinx.android.synthetic.main.thumbnail_section.view.*
import java.util.*

class MediaAdapter(activity: BaseSimpleActivity, var media: MutableList<ThumbnailItem>, val listener: MediaOperationsListener?, val isAGetIntent: Boolean,
                   val allowMultiplePicks: Boolean, val path: String, recyclerView: MyRecyclerView, fastScroller: FastScroller? = null, itemClick: (Any) -> Unit) :
        MyRecyclerViewAdapter(activity, recyclerView, fastScroller, itemClick) {

    private val INSTANT_LOAD_DURATION = 2000L
    private val IMAGE_LOAD_DELAY = 100L
    private val ITEM_SECTION = 0
    private val ITEM_MEDIUM = 1

    private val config = activity.config
    private val viewType = config.getFolderViewType(if (config.showAll) SHOW_ALL else path)
    private val isListViewType = viewType == VIEW_TYPE_LIST
    private var visibleItemPaths = ArrayList<String>()
    private var rotatedImagePaths = ArrayList<String>()
    private var loadImageInstantly = false
    private var delayHandler = Handler(Looper.getMainLooper())
    private var currentMediaHash = media.hashCode()
    private val hasOTGConnected = activity.hasOTGConnected()

    private var scrollHorizontally = config.scrollHorizontally
    private var animateGifs = config.animateGifs
    private var cropThumbnails = config.cropThumbnails
    private var displayFilenames = config.displayFileNames
    private var showFileTypes = config.showThumbnailFileTypes

    init {
        setupDragListener(true)
        enableInstantLoad()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutType = if (viewType == ITEM_SECTION) {
            R.layout.thumbnail_section
        } else {
            if (isListViewType) {
                R.layout.photo_video_item_list
            } else {
                R.layout.photo_video_item_grid
            }
        }
        return createViewHolder(layoutType, parent)
    }

    override fun onBindViewHolder(holder: MyRecyclerViewAdapter.ViewHolder, position: Int) {
        val tmbItem = media.getOrNull(position) ?: return
        if (tmbItem is Medium) {
            visibleItemPaths.add(tmbItem.path)
        }

        val allowLongPress = (!isAGetIntent || allowMultiplePicks) && tmbItem is Medium
        holder.bindView(tmbItem, tmbItem is Medium, false) { itemView, adapterPosition ->
            if (tmbItem is Medium) {
                setupThumbnail(itemView, tmbItem)
            } else {
                setupSection(itemView, tmbItem as ThumbnailSection)
            }
        }
        bindViewHolder(holder)
    }

    override fun getItemCount() = media.size

    override fun getItemViewType(position: Int): Int {
        val tmbItem = media[position]
        return if (tmbItem is ThumbnailSection) {
            ITEM_SECTION
        } else {
            ITEM_MEDIUM
        }
    }

    override fun getActionMenuId(): Int  = -1

    override fun prepareActionMode(menu: Menu) {
        val selectedItems = getSelectedItems()
        if (selectedItems.isEmpty()) {
            return
        }
    }

    override fun actionItemPressed(id: Int) {

    }

    override fun getSelectableItemCount() = media.filter { it is Medium }.size

    override fun getIsItemSelectable(position: Int) = !isASectionTitle(position)

    override fun getItemSelectionKey(position: Int) = (media.getOrNull(position) as? Medium)?.path?.hashCode()

    override fun getItemKeyPosition(key: Int) = media.indexOfFirst { (it as? Medium)?.path?.hashCode() == key }

    override fun onActionModeCreated() {}

    override fun onActionModeDestroyed() {}

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        if (!activity.isDestroyed) {
            val itemView = holder.itemView
            visibleItemPaths.remove(itemView.medium_name?.tag)
            val tmb = itemView.medium_thumbnail
            if (tmb != null) {
                Glide.with(activity).clear(tmb)
            }
        }
    }

    fun isASectionTitle(position: Int) = media.getOrNull(position) is ThumbnailSection

    private fun getSelectedItems() = selectedKeys.mapNotNull { getItemWithKey(it) } as ArrayList<Medium>

    private fun getSelectedPaths() = getSelectedItems().map { it.path } as ArrayList<String>

    private fun getItemWithKey(key: Int): Medium? = media.firstOrNull { (it as? Medium)?.path?.hashCode() == key } as? Medium

    fun updateMedia(newMedia: ArrayList<ThumbnailItem>) {
        val thumbnailItems = newMedia.clone() as ArrayList<ThumbnailItem>
        if (thumbnailItems.hashCode() != currentMediaHash) {
            currentMediaHash = thumbnailItems.hashCode()
            Handler().postDelayed({
                media = thumbnailItems
                enableInstantLoad()
                notifyDataSetChanged()
                finishActMode()
            }, 100L)
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

    fun updateShowFileTypes(showFileTypes: Boolean) {
        this.showFileTypes = showFileTypes
        notifyDataSetChanged()
    }

    private fun enableInstantLoad() {
        loadImageInstantly = true
        delayHandler.postDelayed({
            loadImageInstantly = false
        }, INSTANT_LOAD_DURATION)
    }

    private fun setupThumbnail(view: View, medium: Medium) {
        val isSelected = selectedKeys.contains(medium.path.hashCode())
        view.apply {
            play_outline.beVisibleIf(medium.isVideo() || medium.isPortrait())
            if (medium.isVideo()) {
                play_outline.setImageResource(R.drawable.img_play_outline)
                play_outline.beVisible()
            } else if (medium.isPortrait()) {
                play_outline.setImageResource(R.drawable.ic_portrait_photo_vector)
                play_outline.beVisibleIf(showFileTypes)
            }

            if (showFileTypes && (medium.isGIF() || medium.isRaw() || medium.isSVG())) {
                file_type.setText(when (medium.type) {
                    TYPE_GIFS -> R.string.gif
                    TYPE_RAWS -> R.string.raw
                    else -> R.string.svg
                })
                file_type.beVisible()
            } else {
                file_type.beGone()
            }

            medium_name.beVisibleIf(displayFilenames || isListViewType)
            medium_name.text = medium.name
            medium_name.tag = medium.path

            val showVideoDuration = medium.isVideo() && config.showThumbnailVideoDuration
            if (showVideoDuration) {
                video_duration.text = medium.videoDuration.getFormattedDuration()
            }
            video_duration.beVisibleIf(showVideoDuration)

            medium_check?.beVisibleIf(isSelected)
            if (isSelected) {
                medium_check?.background?.applyColorFilter(primaryColor)
            }

            var path = medium.path
            if (hasOTGConnected && context.isPathOnOTG(path)) {
                path = path.getOTGPublicPath(context)
            }

            if (loadImageInstantly) {
                activity.loadImage(medium.type, path, medium_thumbnail, scrollHorizontally, animateGifs, cropThumbnails, rotatedImagePaths)
            } else {
                medium_thumbnail.setImageDrawable(null)
                medium_thumbnail.isHorizontalScrolling = scrollHorizontally
                delayHandler.postDelayed({
                    val isVisible = visibleItemPaths.contains(medium.path)
                    if (isVisible) {
                        activity.loadImage(medium.type, path, medium_thumbnail, scrollHorizontally, animateGifs, cropThumbnails, rotatedImagePaths)
                    }
                }, IMAGE_LOAD_DELAY)
            }

            if (isListViewType) {
                medium_name.setTextColor(textColor)
                play_outline.applyColorFilter(textColor)
            }
        }
    }

    private fun setupSection(view: View, section: ThumbnailSection) {
        view.apply {
            thumbnail_section.text = section.title
            thumbnail_section.setTextColor(textColor)
        }
    }
}
