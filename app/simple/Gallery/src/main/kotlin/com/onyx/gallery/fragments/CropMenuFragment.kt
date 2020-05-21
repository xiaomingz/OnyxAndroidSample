package com.onyx.gallery.fragments

import android.content.Context
import android.view.View
import com.onyx.gallery.R
import com.onyx.gallery.databinding.FragmentEditMenuCropBinding

/**
 * Created by Leung on 2020/5/6
 */
class CropMenuFragment : BaseFragment<FragmentEditMenuCropBinding>() {
    override fun getLayoutId(): Int = R.layout.fragment_edit_menu_crop

    override fun onInitView(binding: FragmentEditMenuCropBinding, contentView: View) {
    }

    override fun onInitViewModel(context: Context, binding: FragmentEditMenuCropBinding, rootView: View) {
    }

}