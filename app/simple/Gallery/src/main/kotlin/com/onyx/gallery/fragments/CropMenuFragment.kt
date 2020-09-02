package com.onyx.gallery.fragments

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.onyx.gallery.R
import com.onyx.gallery.databinding.FragmentEditMenuCropBinding
import com.onyx.gallery.event.result.SaveCropTransformResultEvent
import com.onyx.gallery.viewmodel.BaseViewModel
import com.onyx.gallery.viewmodel.CropMenuViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Leung on 2020/5/6
 */
class CropMenuFragment : BaseMenuFragment<FragmentEditMenuCropBinding, CropMenuViewModel>() {

    override fun getLayoutId(): Int = R.layout.fragment_edit_menu_crop

    override fun onInitView(binding: FragmentEditMenuCropBinding, contentView: View) {
    }

    override fun onInitViewModel(context: Context, binding: FragmentEditMenuCropBinding, rootView: View): CropMenuViewModel {
        val cropMenuViewModel = ViewModelProvider(requireActivity(), BaseViewModel.ViewModeFactory(editBundle)).get(CropMenuViewModel::class.java)
        binding.viewModel = cropMenuViewModel
        binding.lifecycleOwner = this
        return cropMenuViewModel
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSaveCropTransformResultEvent(event: SaveCropTransformResultEvent) {
        if (event.isSuccess()) {
            viewModel.resetCropState()
        }
    }

}