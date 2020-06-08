package com.onyx.gallery.fragments

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.onyx.gallery.R
import com.onyx.gallery.databinding.FragmentEditMenuTextBinding
import com.onyx.gallery.viewmodel.TextMenuViewModel

/**
 * Created by Leung on 2020/5/6
 */
class TextMenuFragment : BaseMenuFragment<FragmentEditMenuTextBinding, TextMenuViewModel>() {
    override fun getLayoutId(): Int = R.layout.fragment_edit_menu_text

    override fun onInitView(binding: FragmentEditMenuTextBinding, contentView: View) {
    }

    override fun onInitViewModel(context: Context, binding: FragmentEditMenuTextBinding, rootView: View): TextMenuViewModel {
        return ViewModelProvider(requireActivity()).get(TextMenuViewModel::class.java)
    }

}