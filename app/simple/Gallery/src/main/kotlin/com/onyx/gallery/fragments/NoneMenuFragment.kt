package com.onyx.gallery.fragments

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.onyx.gallery.R
import com.onyx.gallery.databinding.FragmentEditMenuNoneBinding
import com.onyx.gallery.viewmodel.BaseViewModel
import com.onyx.gallery.viewmodel.NoneMenuViewModel

/**
 * Created by Leung on 2020/5/6
 */
class NoneMenuFragment : BaseMenuFragment<FragmentEditMenuNoneBinding, NoneMenuViewModel>() {
    override fun getLayoutId(): Int = R.layout.fragment_edit_menu_none

    override fun onInitView(binding: FragmentEditMenuNoneBinding, contentView: View) {
    }

    override fun onInitViewModel(context: Context, binding: FragmentEditMenuNoneBinding, rootView: View): NoneMenuViewModel {
        return ViewModelProvider(requireActivity(), BaseViewModel.ViewModeFactory(editBundle)).get(NoneMenuViewModel::class.java)
    }

}