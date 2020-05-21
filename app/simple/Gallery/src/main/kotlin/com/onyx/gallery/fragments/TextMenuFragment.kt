package com.onyx.gallery.fragments

import android.content.Context
import android.view.View
import com.onyx.gallery.R
import com.onyx.gallery.databinding.FragmentEditMenuGraffitiBinding
import com.onyx.gallery.databinding.FragmentEditMenuShareBinding
import com.onyx.gallery.databinding.FragmentEditMenuTextBinding

/**
 * Created by Leung on 2020/5/6
 */
class TextMenuFragment : BaseFragment<FragmentEditMenuTextBinding>() {
    override fun getLayoutId(): Int = R.layout.fragment_edit_menu_text

    override fun onInitView(binding: FragmentEditMenuTextBinding, contentView: View) {
    }

    override fun onInitViewModel(context: Context, binding: FragmentEditMenuTextBinding, rootView: View) {
    }

}