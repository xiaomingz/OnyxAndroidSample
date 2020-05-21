package com.onyx.gallery.fragments

import android.content.Context
import android.view.View
import com.onyx.gallery.R
import com.onyx.gallery.databinding.FragmentEditMenuNoneBinding

/**
 * Created by Leung on 2020/5/6
 */
class NoneMenuFragment : BaseFragment<FragmentEditMenuNoneBinding>() {
    override fun getLayoutId(): Int = R.layout.fragment_edit_menu_none

    override fun onInitView(binding: FragmentEditMenuNoneBinding, contentView: View) {
    }

    override fun onInitViewModel(context: Context, binding: FragmentEditMenuNoneBinding, rootView: View) {
    }

}