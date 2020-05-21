package com.onyx.gallery.fragments

import android.content.Context
import android.view.View
import com.onyx.gallery.R
import com.onyx.gallery.databinding.FragmentEditMenuShareBinding

/**
 * Created by Leung on 2020/5/6
 */
class ShareMenuFragment : BaseFragment<FragmentEditMenuShareBinding>() {
    override fun getLayoutId(): Int = R.layout.fragment_edit_menu_share

    override fun onInitView(binding: FragmentEditMenuShareBinding, contentView: View) {
    }

    override fun onInitViewModel(context: Context, binding: FragmentEditMenuShareBinding, rootView: View) {
    }

}