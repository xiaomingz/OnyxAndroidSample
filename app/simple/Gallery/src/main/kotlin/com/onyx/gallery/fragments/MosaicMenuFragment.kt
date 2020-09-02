package com.onyx.gallery.fragments

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.onyx.gallery.R
import com.onyx.gallery.databinding.FragmentEditMenuMosaicBinding
import com.onyx.gallery.viewmodel.BaseViewModel
import com.onyx.gallery.viewmodel.MosaicMenuViewModel

/**
 * Created by Leung on 2020/5/6
 */
class MosaicMenuFragment : BaseMenuFragment<FragmentEditMenuMosaicBinding, MosaicMenuViewModel>() {
    override fun getLayoutId(): Int = R.layout.fragment_edit_menu_mosaic
    override fun onInitView(binding: FragmentEditMenuMosaicBinding, contentView: View) {
    }

    override fun onInitViewModel(context: Context, binding: FragmentEditMenuMosaicBinding, rootView: View): MosaicMenuViewModel {
        return ViewModelProvider(requireActivity(), BaseViewModel.ViewModeFactory(editBundle)).get(MosaicMenuViewModel::class.java).apply {
            binding.viewModel = this
            binding.lifecycleOwner = this@MosaicMenuFragment
        }
    }

}