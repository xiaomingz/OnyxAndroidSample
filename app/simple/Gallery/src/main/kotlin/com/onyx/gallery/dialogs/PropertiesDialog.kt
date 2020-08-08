package com.onyx.gallery.dialogs

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.onyx.gallery.R
import com.onyx.gallery.databinding.DialogPropertiesBinding
import com.onyx.gallery.viewmodel.BaseViewModel
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.models.FileDirItem

/**
 * Created by Leung 2020/8/8 17:40
 **/
class PropertiesDialog(activity: AppCompatActivity, path: String) : BaseDialog<DialogPropertiesBinding>() {

    private val viewModel = PropertiesDialogViewModel()

    init {
        val fileDirItem = FileDirItem(path, path.getFilenameFromPath(), activity.getIsPathDirectory(path))
        viewModel.name.value = fileDirItem.name
        viewModel.path.value = fileDirItem.getParentPath()
        viewModel.size.value = fileDirItem.getProperSize(activity, false).formatSize()
        fileDirItem.getResolution(activity)?.let {
            viewModel.resolution.value = it.formatAsResolution()
        }
        viewModel.lastModified.value = fileDirItem.getLastModified(activity).formatDate(activity)
    }

    override fun getLayoutRes(): Int = R.layout.dialog_properties

    override fun initBinding(binding: DialogPropertiesBinding) {
        binding.dialog = this
        binding.viewModel = viewModel

    }

    fun onConfirmClick() {
        dialog.dismiss()
    }
}

class PropertiesDialogViewModel : BaseViewModel() {
    val name = MutableLiveData<String>()
    val path = MutableLiveData<String>()
    val size = MutableLiveData<String>()
    val resolution = MutableLiveData<String>()
    val lastModified = MutableLiveData<String>()
}