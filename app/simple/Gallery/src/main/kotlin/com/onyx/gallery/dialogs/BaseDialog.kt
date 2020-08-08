package com.onyx.gallery.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.simplemobiletools.commons.extensions.setupDialogStuff

/**
 * Created by Leung 2020/7/31 10:47
 **/
abstract class BaseDialog<T : ViewDataBinding> : DialogFragment() {
    protected lateinit var dialog: AlertDialog
    protected lateinit var binding: T
    var onCancelCallback: (() -> Unit)? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(requireActivity().layoutInflater, getLayoutRes(), null, false)
        initBinding(binding)
        binding.lifecycleOwner = this
        val builder = AlertDialog.Builder(requireActivity())
        dialog = builder.create().apply {
            requireActivity().setupDialogStuff(binding.root, this)
            setCanceledOnTouchOutside(false)
        }
        afterDialogCreated(dialog)
        return dialog
    }

    abstract fun getLayoutRes(): Int

    abstract fun initBinding(binding: T)

    protected open fun afterDialogCreated(dialog: AlertDialog) {
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    open fun onCancelClick() {
        dialog.dismiss()
        onCancelCallback?.let { it() }
    }

}