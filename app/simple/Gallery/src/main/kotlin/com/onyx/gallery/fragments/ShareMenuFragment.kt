package com.onyx.gallery.fragments

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.onyx.gallery.R
import com.onyx.gallery.databinding.FragmentEditMenuShareBinding
import com.onyx.gallery.viewmodel.ShareMenuViewModel

/**
 * Created by Leung on 2020/5/6
 */
class ShareMenuFragment : BaseMenuFragment<FragmentEditMenuShareBinding, ShareMenuViewModel>() {
    override fun getLayoutId(): Int = R.layout.fragment_edit_menu_share

    override fun onInitView(binding: FragmentEditMenuShareBinding, contentView: View) {
    }

    override fun onInitViewModel(context: Context, binding: FragmentEditMenuShareBinding, rootView: View): ShareMenuViewModel {
        return ViewModelProvider(requireActivity()).get(ShareMenuViewModel::class.java)
    }

}