package com.onyx.gallery.fragments

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.onyx.gallery.R
import com.onyx.gallery.databinding.FragmentEditMenuEraseBinding
import com.onyx.gallery.viewmodel.BaseViewModel
import com.onyx.gallery.viewmodel.EraseMenuViewModel

/**
 * Created by Leung 2020/8/24 10:50
 **/
class EraseMenuFragment : BaseMenuFragment<FragmentEditMenuEraseBinding, EraseMenuViewModel>() {
    override fun getLayoutId(): Int = R.layout.fragment_edit_menu_erase
    override fun onInitView(binding: FragmentEditMenuEraseBinding, contentView: View) {
    }

    override fun onInitViewModel(context: Context, binding: FragmentEditMenuEraseBinding, rootView: View): EraseMenuViewModel {
        return ViewModelProvider(this, BaseViewModel.ViewModeFactory(editBundle)).get(EraseMenuViewModel::class.java).apply {
            binding.viewModel = this
            binding.lifecycleOwner = this@EraseMenuFragment
        }
    }
}