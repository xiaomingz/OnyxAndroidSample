package com.simplemobiletools.musicplayer.adapters

import android.content.Intent
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.simplemobiletools.commons.adapters.MyRecyclerViewAdapter
import com.simplemobiletools.commons.dialogs.ConfirmationDialog
import com.simplemobiletools.commons.dialogs.PropertiesDialog
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import com.simplemobiletools.commons.models.FileDirItem
import com.simplemobiletools.commons.views.FastScroller
import com.simplemobiletools.commons.views.MyRecyclerView
import com.simplemobiletools.musicplayer.BuildConfig
import com.simplemobiletools.musicplayer.R
import com.simplemobiletools.musicplayer.activities.SimpleActivity
import com.simplemobiletools.musicplayer.dialogs.EditDialog
import com.simplemobiletools.musicplayer.extensions.config
import com.simplemobiletools.musicplayer.extensions.sendIntent
import com.simplemobiletools.musicplayer.extensions.songsDAO
import com.simplemobiletools.musicplayer.helpers.*
import com.simplemobiletools.musicplayer.interfaces.SongListListener
import com.simplemobiletools.musicplayer.models.Song
import com.simplemobiletools.musicplayer.services.MusicService
import kotlinx.android.synthetic.main.item_song.view.*

class SongAdapter(activity: SimpleActivity, var songs: ArrayList<Song>, val listener: SongListListener, recyclerView: MyRecyclerView,
                  fastScroller: FastScroller?, itemClick: (Any) -> Unit) : MyRecyclerViewAdapter(activity, recyclerView, fastScroller, itemClick) {

    private var currentSongIndex = 0
    private var songsHashCode = songs.hashCode()
    private var currentSong: Song? = null
    private var textToHighlight = ""

    var isThirdPartyIntent = false
    private var adjustedPrimaryColor = activity.getAdjustedPrimaryColor()

    init {
        setupDragListener(true)
        positionOffset = LIST_HEADERS_COUNT
    }

    override fun getActionMenuId() = R.menu.cab

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return createViewHolder(R.layout.item_song, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs.getOrNull(position - LIST_HEADERS_COUNT) ?: return
        holder.bindView(song, true, true) { itemView, layoutPosition ->
            setupView(itemView, song, layoutPosition)
        }
        bindViewHolder(holder)
    }

    override fun getItemCount() = songs.size + LIST_HEADERS_COUNT

    private fun getItemWithKey(key: Int): Song? = songs.firstOrNull { it.path.hashCode() == key }

    override fun prepareActionMode(menu: Menu) {
        menu.apply {
            findItem(R.id.cab_rename).isVisible = isOneItemSelected()
        }
    }

    override fun actionItemPressed(id: Int) {
        if (selectedKeys.isEmpty()) {
            return
        }

        when (id) {
            R.id.cab_properties -> showProperties()
            R.id.cab_rename -> displayEditDialog()
            R.id.cab_share -> shareItems()
            R.id.cab_select_all -> selectAll()
            R.id.cab_remove_from_playlist -> removeFromPlaylist()
            R.id.cab_delete -> askConfirmDelete()
        }
    }

    override fun getSelectableItemCount() = songs.size

    override fun getIsItemSelectable(position: Int) = position >= 0

    override fun getItemSelectionKey(position: Int) = songs.getOrNull(position)?.path?.hashCode()

    override fun getItemKeyPosition(key: Int) = songs.indexOfFirst { it.path.hashCode() == key }

    override fun onActionModeCreated() {}

    override fun onActionModeDestroyed() {}

    fun searchOpened() {
    }

    fun searchClosed() {
        notifyDataSetChanged()
    }

    private fun showProperties() {
        if (selectedKeys.size <= 1) {
            PropertiesDialog(activity, getFirstSelectedItemPath())
        } else {
            val paths = getSelectedSongs().map { it.path }
            PropertiesDialog(activity, paths)
        }
    }

    private fun displayEditDialog() {
        EditDialog(activity, getSelectedSongs().first()) {
            if (it == MusicService.mCurrSong) {
                Intent(activity, MusicService::class.java).apply {
                    putExtra(EDITED_SONG, it)
                    action = EDIT
                    activity.startService(this)
                }
            }

            activity.sendIntent(REFRESH_LIST)
            activity.runOnUiThread {
                finishActMode()
            }
        }
    }

    private fun shareItems() {
        val paths = getSelectedSongs().map { it.path } as ArrayList<String>
        activity.sharePathsIntent(paths, BuildConfig.APPLICATION_ID)
    }

    private fun askConfirmDelete() {
        ConfirmationDialog(activity) {
            ensureBackgroundThread {
                deleteSongs()
                activity.runOnUiThread {
                    finishActMode()
                }
            }
        }
    }

    private fun deleteSongs() {
        if (selectedKeys.isEmpty()) {
            return
        }

        val SAFPath = getFirstSelectedItemPath()
        activity.handleSAFDialog(SAFPath) {
            val files = ArrayList<FileDirItem>(selectedKeys.size)
            val removeSongs = ArrayList<Song>(selectedKeys.size)
            val positions = ArrayList<Int>()

            for (key in selectedKeys) {
                val song = getItemWithKey(key) ?: continue

                val position = songs.indexOfFirst { it.path.hashCode() == key }
                if (position != -1) {
                    positions.add(position + positionOffset)
                    files.add(FileDirItem(song.path))
                    removeSongs.add(song)
                    activity.songsDAO.removeSongPath(song.path)
                    if (song == MusicService.mCurrSong) {
                        activity.sendIntent(RESET)
                    }
                }
            }

            positions.sortDescending()
            activity.runOnUiThread {
                removeSelectedItems(positions)
            }
            activity.deleteFiles(files)

            val songIds = removeSongs.map { it.path.hashCode() } as ArrayList<Int>
            Intent(activity, MusicService::class.java).apply {
                putExtra(SONG_IDS, songIds)
                action = REMOVE_SONG_IDS
                activity.startService(this)
            }

            songs.removeAll(removeSongs)
            if (songs.isEmpty()) {
                listener.refreshItems()
            }
        }
    }

    private fun removeFromPlaylist() {
        if (selectedKeys.isEmpty()) {
            return
        }

        // remove the songs from playlist asap, so they dont get played at Next, if the currently playing song is removed from playlist
        val songIds = ArrayList<Int>(selectedKeys.size)
        for (key in selectedKeys) {
            val song = getItemWithKey(key) ?: continue
            songIds.add(song.path.hashCode())
        }

        Intent(activity, MusicService::class.java).apply {
            putExtra(SONG_IDS, songIds)
            action = REMOVE_SONG_IDS
            activity.startService(this)
        }

        val removeSongs = ArrayList<Song>(selectedKeys.size)
        val positions = ArrayList<Int>()

        for (key in selectedKeys) {
            val song = getItemWithKey(key) ?: continue

            val position = songs.indexOfFirst { it.path.hashCode() == key }
            if (position != -1) {
                positions.add(position + positionOffset)
                removeSongs.add(song)
                if (song == MusicService.mCurrSong) {
                    if (songs.size == removeSongs.size) {
                        activity.sendIntent(REMOVE_CURRENT_SONG)
                    } else {
                        activity.sendIntent(NEXT)
                    }
                }
            }
        }

        val removePaths = removeSongs.map { it.path } as ArrayList<String>
        activity.config.addIgnoredPaths(removePaths)
        songs.removeAll(removeSongs)
        positions.sortDescending()
        removeSelectedItems(positions)
        ensureBackgroundThread {
            activity.songsDAO.removeSongsFromPlaylists(removeSongs)

            if (songs.isEmpty()) {
                listener.refreshItems()
            }
        }
    }

    private fun getFirstSelectedItemPath() = getSelectedSongs().firstOrNull()?.path ?: ""

    private fun getSelectedSongs(): ArrayList<Song> {
        val selectedSongs = ArrayList<Song>(selectedKeys.size)
        selectedKeys.forEach {
            getItemWithKey(it)?.apply {
                selectedSongs.add(this)
            }
        }
        return selectedSongs
    }

    fun updateSongs(newSongs: ArrayList<Song>, highlightText: String = "") {
        val newHashCode = newSongs.hashCode()
        if (newHashCode != songsHashCode) {
            songsHashCode = newHashCode
            textToHighlight = highlightText
            songs = newSongs
            currentSongIndex = -1
            notifyDataSetChanged()
        } else if (textToHighlight != highlightText) {
            textToHighlight = highlightText
            notifyDataSetChanged()
        }
        fastScroller?.measureRecyclerView()
    }

    fun updateCurrentSongIndex(index: Int) {
        val correctIndex = index + LIST_HEADERS_COUNT
        val prevIndex = currentSongIndex
        currentSongIndex = -1
        notifyItemChanged(prevIndex)

        currentSongIndex = correctIndex
        if (index >= 0) {
            notifyItemChanged(correctIndex)
        }
    }

    fun updateSong(song: Song?) {
        currentSong = song
    }

    fun removeCurrentSongFromPlaylist() {
        if (currentSong != null) {
            selectedKeys.clear()
            selectedKeys.add(currentSong!!.path.hashCode())
            removeFromPlaylist()
            selectedKeys.clear()
        }
    }

    fun deleteCurrentSong() {
        ConfirmationDialog(activity) {
            selectedKeys.clear()
            if (songs.isNotEmpty() && currentSong != null) {
                selectedKeys.add(currentSong!!.path.hashCode())
                activity.sendIntent(NEXT)
                ensureBackgroundThread {
                    deleteSongs()
                    selectedKeys.clear()
                }
            }
        }
    }

    fun updateColors() {
        val config = activity.config
        textColor = config.textColor
        primaryColor = config.primaryColor
        backgroundColor = config.backgroundColor
        adjustedPrimaryColor = activity.getAdjustedPrimaryColor()
    }

    private fun setupView(view: View, song: Song, layoutPosition: Int) {
        view.apply {
            song_frame?.isSelected = selectedKeys.contains(song.path.hashCode())
            song_title.text = if (textToHighlight.isEmpty()) song.title else song.title.highlightTextPart(textToHighlight, adjustedPrimaryColor)
            song_title.setTextColor(textColor)

            song_artist.text = if (textToHighlight.isEmpty()) song.artist else song.artist.highlightTextPart(textToHighlight, adjustedPrimaryColor)
            song_artist.setTextColor(textColor)

            song_note_image.beInvisibleIf(currentSongIndex != layoutPosition)
            if (currentSongIndex == layoutPosition) {
                song_note_image.applyColorFilter(textColor)
            }
        }
    }
}
