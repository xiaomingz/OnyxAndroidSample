package com.onyx.gallery.dialogs

import androidx.lifecycle.MutableLiveData
import com.onyx.gallery.R
import com.onyx.gallery.databinding.DialogViewStyleChangeBinding
import com.onyx.gallery.viewmodel.BaseViewModel

/**
 * Created by Leung 2020/7/30 15:24
 **/
enum class ViewStyle {
    GRID, LIST
}

enum class ViewStyleChangeDialogType {
    WITH_GROUP_BY_DIRECTORY, WITH_USE_FOR_THIS_FOLDER
}

class ViewStyleChangeDialog(private val dialogType: ViewStyleChangeDialogType, viewStyle: ViewStyle, isGroupByDirectory: Boolean = false, isUseForThisFolder: Boolean = false,
                            private val onConfirmCallback: (viewStyle: ViewStyle, isGroupByDirectory: Boolean, isUseForThisFolder: Boolean) -> Unit) : BaseDialog<DialogViewStyleChangeBinding>() {

    private val viewModel = ViewStyleChangeDialogViewModel(viewStyle, isGroupByDirectory, isUseForThisFolder)

    override fun getLayoutRes(): Int = R.layout.dialog_view_style_change

    override fun initBinding(binding: DialogViewStyleChangeBinding) {
        binding.dialog = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        if (dialogType == ViewStyleChangeDialogType.WITH_GROUP_BY_DIRECTORY) {
            viewModel.showGroupByDirectory(true)
        } else {
            viewModel.showUseForThisFolder(true)
        }
    }

    fun onConfirmClick() {
        dialog.dismiss()
        onConfirmCallback(viewModel.viewStyle.value!!, viewModel.isGroupByDirectory.value!!, viewModel.isUseForThisFolder.value!!)
    }
}

class ViewStyleChangeDialogViewModel(viewStyle: ViewStyle, groupByDirectory: Boolean = false, isUseForThisFolder: Boolean = false) : BaseViewModel() {
    val viewStyle = MutableLiveData(viewStyle)
    val isGroupByDirectory = MutableLiveData(groupByDirectory)
    val isUseForThisFolder = MutableLiveData(isUseForThisFolder)
    val isShowGroupByDirectory = MutableLiveData(false)
    val isShowUseForThisFolder = MutableLiveData(false)

    fun showGroupByDirectory(isShowGroupByDirectory: Boolean) {
        this.isShowGroupByDirectory.value = isShowGroupByDirectory
    }

    fun showUseForThisFolder(isShowUseForThisFolder: Boolean) {
        this.isShowUseForThisFolder.value = isShowUseForThisFolder
    }

    fun onViewStyleChange(viewStyle: ViewStyle) {
        this.viewStyle.value = viewStyle
    }

    fun onGroupByDirectoryClick() {
        val groupByDirectory = isGroupByDirectory.value!!
        isGroupByDirectory.value = !groupByDirectory
    }

    fun onUseForThisFolderClick() {
        val useForThisFolder = isUseForThisFolder.value!!
        isUseForThisFolder.value = !useForThisFolder
    }
}
