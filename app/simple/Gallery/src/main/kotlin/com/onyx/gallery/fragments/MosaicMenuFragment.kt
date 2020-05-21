package com.onyx.gallery.fragments

import android.content.Context
import android.view.View
import com.onyx.gallery.R
import com.onyx.gallery.databinding.FragmentEditMenuMosaicBinding

/**
 * Created by Leung on 2020/5/6
 */
class MosaicMenuFragment : BaseFragment<FragmentEditMenuMosaicBinding>() {
    override fun getLayoutId(): Int = R.layout.fragment_edit_menu_mosaic
    override fun onInitView(binding: FragmentEditMenuMosaicBinding, contentView: View) {
    }

    override fun onInitViewModel(context: Context, binding: FragmentEditMenuMosaicBinding, rootView: View) {
    }

}