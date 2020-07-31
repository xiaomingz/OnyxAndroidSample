package com.onyx.gallery.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.onyx.gallery.R
import com.onyx.gallery.databinding.DialogConfirmSaveBinding
import com.simplemobiletools.commons.extensions.setupDialogStuff

/**
 * Created by Leung 2020/7/30 15:24
 **/
class ConfirmSaveDialog(val onConfirmCallback: (isSaveAs: Boolean) -> Unit) : DialogFragment() {
    private lateinit var dialog: AlertDialog
    private lateinit var binding: DialogConfirmSaveBinding
    lateinit var onCancelCallback: () -> Unit

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(requireActivity().layoutInflater, R.layout.dialog_confirm_save, null, false)
        binding.dialog = this
        binding.lifecycleOwner = this
        val builder = AlertDialog.Builder(requireActivity())
        dialog = builder.create().apply {
            requireActivity().setupDialogStuff(binding.root, this)
            setCanceledOnTouchOutside(false)
        }
        return dialog
    }

    fun onCancelClick() {
        dialog.dismiss()
        onCancelCallback()
    }

    fun onSaveAsClick() {
        onConfirmClick(true)
    }

    fun onSaveClick() {
        onConfirmClick(false)
    }

    fun onConfirmClick(isSaveAs: Boolean) {
        dialog.dismiss()
        onConfirmCallback(isSaveAs)
    }
}
