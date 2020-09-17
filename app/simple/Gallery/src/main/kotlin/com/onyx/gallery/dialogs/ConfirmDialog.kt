package com.onyx.gallery.dialogs

import com.onyx.gallery.R
import com.onyx.gallery.databinding.DialogConfirmBinding

/**
 * Created by Leung 2020/7/30 15:24
 **/
class ConfirmDialog : BaseDialog<DialogConfirmBinding>() {

    lateinit var message: String
    lateinit var onConfirmCallback: () -> Unit

    override fun getLayoutRes(): Int = R.layout.dialog_confirm

    override fun initBinding(binding: DialogConfirmBinding) {
        binding.dialog = this
        binding.message = message
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dismiss()
    }

    fun onConfirmClick() {
        dialog.dismiss()
        onConfirmCallback()
    }

}
