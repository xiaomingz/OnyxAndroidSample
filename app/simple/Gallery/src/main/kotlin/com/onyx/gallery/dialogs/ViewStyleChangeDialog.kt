package com.onyx.gallery.dialogs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.onyx.gallery.R
import com.onyx.gallery.databinding.DialogViewStyleChangeBinding

/**
 * Created by Leung 2020/7/30 15:24
 **/
enum class ViewStyle {
    GRID, LIST
}

enum class ViewStyleChangeDialogType {
    WITH_GROUP_BY_DIRECTORY, WITH_USE_FOR_THIS_FOLDER
}

class ViewStyleChangeDialog : BaseDialog<DialogViewStyleChangeBinding>() {

    lateinit var dialogType: ViewStyleChangeDialogType
    lateinit var viewStyle: ViewStyle
    var isGroupByDirectory: Boolean = false
    var isUseForThisFolder: Boolean = false
    lateinit var onConfirmCallback: (viewStyle: ViewStyle, isGroupByDirectory: Boolean, isUseForThisFolder: Boolean) -> Unit

    private val viewModel by lazy { ViewStyleChangeDialogViewModel(viewStyle, isGroupByDirectory, isUseForThisFolder) }

    override fun getLayoutRes(): Int = R.layout.dialog_view_style_change

    override fun initBinding(binding: DialogViewStyleChangeBinding) {
        binding.dialog = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.showGroupByDirectory(false)
        viewModel.showUseForThisFolder(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dismiss()
    }

    fun onConfirmClick() {
        dialog.dismiss()
        onConfirmCallback(viewModel.viewStyle.value!!, viewModel.isGroupByDirectory.value!!, viewModel.isUseForThisFolder.value!!)
    }
}

class ViewStyleChangeDialogViewModel(viewStyle: ViewStyle, groupByDirectory: Boolean = false, isUseForThisFolder: Boolean = false) : ViewModel() {
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
