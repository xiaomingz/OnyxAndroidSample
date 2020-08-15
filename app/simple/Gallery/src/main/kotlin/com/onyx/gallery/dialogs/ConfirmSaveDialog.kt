package com.onyx.gallery.dialogs

import com.onyx.android.sdk.utils.ResManager
import com.onyx.gallery.R
import com.onyx.gallery.databinding.DialogConfirmSaveBinding

/**
 * Created by Leung 2020/7/30 15:24
 **/
class ConfirmSaveDialog(private val messageRes: Int, private val onConfirmCallback: (isSaveAs: Boolean) -> Unit) : BaseDialog<DialogConfirmSaveBinding>() {

    override fun getLayoutRes(): Int = R.layout.dialog_confirm_save

    override fun initBinding(binding: DialogConfirmSaveBinding) {
        binding.dialog = this
        binding.message = ResManager.getString(messageRes)
    }

    override fun onCancelClick() {
        super.onCancelClick()
        dialog.dismiss()
    }

    fun onSaveAsClick() {
        onConfirmClick(true)
    }

    fun onSaveClick() {
        onConfirmClick(false)
    }

    private fun onConfirmClick(isSaveAs: Boolean) {
        dialog.dismiss()
        onConfirmCallback(isSaveAs)
    }

}
