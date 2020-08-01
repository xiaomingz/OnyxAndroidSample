package com.simplemobiletools.commons.dialogs

import android.app.AlertDialog
import com.simplemobiletools.commons.R
import com.simplemobiletools.commons.activities.BaseSimpleActivity
import com.simplemobiletools.commons.extensions.*
import kotlinx.android.synthetic.main.dialog_rename_items.*
import kotlinx.android.synthetic.main.dialog_rename_items.view.*
import java.util.*

class RenameItemsDialog(val activity: BaseSimpleActivity, val paths: ArrayList<String>, val callback: () -> Unit) {
    init {
        var ignoreClicks = false
        val view = activity.layoutInflater.inflate(R.layout.dialog_rename_items, null)

        AlertDialog.Builder(activity)
                .setPositiveButton(R.string.ok, null)
                .setNegativeButton(R.string.cancel, null)
                .create().apply {
                    activity.setupDialogStuff(view, this, R.string.rename) {
                        showKeyboard(view.rename_items_value)
                        getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                            if (ignoreClicks) {
                                return@setOnClickListener
                            }

                            val valueToAdd = view.rename_items_value.value

                            if (valueToAdd.isEmpty()) {
                                callback()
                                dismiss()
                                return@setOnClickListener
                            }

                            if (!valueToAdd.isAValidFilename()) {
                                activity.toast(R.string.invalid_name)
                                return@setOnClickListener
                            }

                            val validPaths = paths.filter { activity.getDoesFilePathExist(it) }
                            val sdFilePath = validPaths.firstOrNull { activity.isPathOnSD(it) } ?: validPaths.firstOrNull()
                            if (sdFilePath == null) {
                                activity.toast(R.string.unknown_error_occurred)
                                dismiss()
                                return@setOnClickListener
                            }

                            activity.handleSAFDialog(sdFilePath) {
                                if (!it) {
                                    return@handleSAFDialog
                                }

                                ignoreClicks = true
                                var pathsCnt = validPaths.size
                                var index = 1
                                val totalCnt = pathsCnt;
                                for (path in validPaths) {
                                    val fullName = path.getFilenameFromPath()
                                    var dotAt = fullName.lastIndexOf(".")
                                    if (dotAt == -1) {
                                        dotAt = fullName.length
                                    }

                                    val name = fullName.substring(0, dotAt)
                                    val extension = if (fullName.contains(".")) ".${fullName.getFilenameExtension()}" else ""
                                    var newName = "";
                                    when(view.rename_items_radio_group.checkedRadioButtonId) {
                                        rename_items_radio_full.id -> {
                                            if (totalCnt == 1) {
                                                newName = "$valueToAdd$extension"
                                            } else {
                                                do {
                                                    newName = "$valueToAdd($index)$extension"
                                                    val testPath = "${path.getParentPath()}/$newName"
                                                    index++
                                                } while (activity.getDoesFilePathExist(testPath))
                                            }
                                        }
                                        rename_items_radio_append.id -> newName = "$name$valueToAdd$extension"
                                        rename_items_radio_prepend.id -> newName = "$valueToAdd$fullName"
                                    }

                                    val newPath = "${path.getParentPath()}/$newName"

                                    if (activity.getDoesFilePathExist(newPath)) {
                                        continue
                                    }

                                    activity.renameFile(path, newPath) {
                                        if (it) {
                                            pathsCnt--
                                            if (pathsCnt == 0) {
                                                callback()
                                                dismiss()
                                            }
                                        } else {
                                            ignoreClicks = false
                                            activity.toast(R.string.unknown_error_occurred)
                                            dismiss()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
    }
}
