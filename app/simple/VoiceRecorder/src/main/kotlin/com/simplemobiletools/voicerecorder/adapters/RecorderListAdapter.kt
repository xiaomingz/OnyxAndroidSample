package com.simplemobiletools.voicerecorder.adapters

import android.app.AlertDialog
import android.view.View
import android.view.ViewGroup
import com.simplemobiletools.commons.activities.BaseSimpleActivity
import com.simplemobiletools.commons.dialogs.RenameItemsDialog
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.DATE_FORMAT_FOUR
import com.simplemobiletools.commons.helpers.TIME_FORMAT_24
import com.simplemobiletools.commons.views.MyRecyclerView
import com.simplemobiletools.voicerecorder.R
import com.simplemobiletools.voicerecorder.actions.FilesLoadAction
import kotlinx.android.synthetic.main.item_recorder.view.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RecorderListAdapter(activity: BaseSimpleActivity, recyclerView: MyRecyclerView, itemClick: (Any) -> Unit) :
        SimpleBaseAdapter(activity, recyclerView, itemClick) {

    private val itemList = ArrayList<File>()
    private val dateFormat = SimpleDateFormat(DATE_FORMAT_FOUR + " " + TIME_FORMAT_24, Locale.getDefault());

    fun addItems(files: List<File>, clear: Boolean = false) {
        if (clear) {
            itemList.clear()
        }
        itemList.addAll(files)
        notifyDataSetChanged()
    }

    fun getPrevItem(file: File): File? {
        return itemList.getOrElse(itemList.indexOf(file) - 1) {
            itemList.firstOrNull()
        };
    }

    fun getNextItem(file: File): File? {
        return itemList.getOrElse(itemList.indexOf(file) + 1) {
            itemList.lastOrNull()
        };
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return createViewHolder(R.layout.item_recorder, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val file = itemList.getOrNull(position) ?: return
        holder.bindView(file, true, true) { itemView, adapterPosition ->
            setupView(itemView, file)
        }
        bindViewHolder(holder)
    }

    override fun getItemCount() = itemList.size

    override fun getActionMenuId() = R.menu.recorder_list_menu

    override fun actionItemPressed(id: Int) {
        if (selectedKeys.isEmpty()) {
            return
        }
        when (id) {
            R.id.cab_select_all -> selectAll()
            R.id.cab_delete -> deleteFiles()
            R.id.cab_share -> shareFiles()
            R.id.cab_rename -> renameFiles()
        }
    }

    override fun getItemSelectionKey(position: Int) = itemList.getOrNull(position)?.absolutePath?.hashCode()

    override fun getItemKeyPosition(key: Int) = itemList.indexOfFirst { it.absolutePath.hashCode() == key }

    private fun getSelectedItems() = selectedKeys.mapNotNull { getItemWithKey(it) } as ArrayList<File>

    private fun getSelectedPaths() = getSelectedItems().map { it.absolutePath } as ArrayList<String>

    private fun getFirstSelectedItemPath() = getItemWithKey(selectedKeys.first())?.absolutePath

    private fun getItemWithKey(key: Int): File? = itemList.firstOrNull { it.absolutePath.hashCode() == key }

    private fun setupView(view: View, file: File) {
        val isSelected = selectedKeys.contains(file.absolutePath.hashCode())
        view.apply {
            view.name?.text = file.name
            view.timestamp?.text = dateFormat.format(Date(file.lastModified()));

            view.select_check?.beVisibleIf(isSelected)
            if (isSelected) {
                view.select_check.background?.applyColorFilter(primaryColor)
            }
        }
    }

    private fun deleteFiles() {
        if (selectedKeys.isEmpty()) {
            return
        }
        AlertDialog.Builder(activity)
                .setMessage(R.string.are_you_sure_delete)
                .setPositiveButton(R.string.ok) { dialog, which ->
                    getSelectedItems().forEach {
                        if (it.delete()) {
                            val index = itemList.indexOf(it);
                            itemList.remove(it)
                            removeSelectedItems(arrayListOf(index));
                        }
                    }
                }.show()
    }

    private fun shareFiles() {
        when (selectedKeys.size) {
            0 -> activity.toast(R.string.no_files_selected)
            1 -> if (selectedKeys.first() != -1) activity.sharePathIntent(getFirstSelectedItemPath()!!, activity.packageName)
            else -> activity.sharePathsIntent((getSelectedPaths()), activity.packageName)
        }
    }

    private fun renameFiles() {
        when (selectedKeys.size) {
            0 -> activity.toast(R.string.no_files_selected)
            else -> {
                RenameItemsDialog(activity, getSelectedPaths()) {
                    FilesLoadAction().execute(activity) {
                        addItems(it, true)
                        finishActMode();
                    }
                }
            }
        }
    }
}
