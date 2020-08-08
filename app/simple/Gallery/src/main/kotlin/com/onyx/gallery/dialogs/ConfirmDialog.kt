package com.onyx.gallery.dialogs

import com.onyx.gallery.R
import com.onyx.gallery.databinding.DialogConfirmBinding

/**
 * Created by Leung 2020/7/30 15:24
 **/
class ConfirmDialog(private val message: String, private val onConfirmCallback: () -> Unit) : BaseDialog<DialogConfirmBinding>() {
    override fun getLayoutRes(): Int = R.layout.dialog_confirm

    override fun initBinding(binding: DialogConfirmBinding) {
        binding.dialog = this
        binding.message = message
    }

    fun onConfirmClick() {
        dialog.dismiss()
        onConfirmCallback()
    }

}
