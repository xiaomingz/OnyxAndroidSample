package com.onyx.gallery.fragments

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.onyx.gallery.R
import com.onyx.gallery.databinding.FragmentEditMenuGraffitiBinding
import com.onyx.gallery.viewmodel.GraffitiMenuViewModel

/**
 * Created by Leung on 2020/5/6
 */
class GraffitiMenuFragment : BaseMenuFragment<FragmentEditMenuGraffitiBinding, GraffitiMenuViewModel>() {

    override fun getLayoutId(): Int = R.layout.fragment_edit_menu_graffiti

    override fun onInitView(binding: FragmentEditMenuGraffitiBinding, contentView: View) {
    }

    override fun onInitViewModel(context: Context, binding: FragmentEditMenuGraffitiBinding, rootView: View): GraffitiMenuViewModel {
        val graffitiMenuViewModel = ViewModelProvider(requireActivity()).get(GraffitiMenuViewModel::class.java)
        binding.viewModel = graffitiMenuViewModel
        binding.lifecycleOwner = this
        return graffitiMenuViewModel
    }

}