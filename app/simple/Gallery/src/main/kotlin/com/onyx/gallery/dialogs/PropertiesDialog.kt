package com.onyx.gallery.dialogs

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.onyx.android.sdk.utils.DateTimeUtil
import com.onyx.android.sdk.utils.DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMMSS
import com.onyx.gallery.R
import com.onyx.gallery.databinding.DialogPropertiesBinding
import com.onyx.gallery.utils.FormatRootFilePathUtils
import com.simplemobiletools.commons.extensions.formatAsResolution
import com.simplemobiletools.commons.extensions.formatSize
import com.simplemobiletools.commons.extensions.getFilenameFromPath
import com.simplemobiletools.commons.extensions.getIsPathDirectory
import com.simplemobiletools.commons.models.FileDirItem
import java.io.File
import java.util.*

/**
 * Created by Leung 2020/8/8 17:40
 **/
class PropertiesDialog(activity: AppCompatActivity, path: String) : BaseDialog<DialogPropertiesBinding>() {

    private val viewModel = PropertiesDialogViewModel()

    init {
        val fileDirItem = FileDirItem(path, path.getFilenameFromPath(), activity.getIsPathDirectory(path))
        viewModel.name.value = fileDirItem.name
        viewModel.path.value = FormatRootFilePathUtils.formatPath(File(path), activity)
        viewModel.size.value = fileDirItem.getProperSize(activity, false).formatSize()
        fileDirItem.getResolution(activity)?.let {
            viewModel.resolution.value = it.formatAsResolution()
        }
        viewModel.lastModified.value = DateTimeUtil.formatDate(Date(fileDirItem.getLastModified(activity)), DATE_FORMAT_YYYYMMDD_HHMMSS)
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

class PropertiesDialogViewModel : ViewModel() {
    val name = MutableLiveData<String>()
    val path = MutableLiveData<String>()
    val size = MutableLiveData<String>()
    val resolution = MutableLiveData<String>()
    val lastModified = MutableLiveData<String>()
}