package com.onyx.gallery.fragments

import androidx.databinding.ViewDataBinding
import com.onyx.gallery.viewmodel.BaseMenuViewModel

/**
 * Created by Leung on 2020/5/6
 */
abstract class BaseMenuFragment<T : ViewDataBinding, V : BaseMenuViewModel> : BaseFragment<T, V>() {

    override fun onResume() {
        super.onResume()
        viewModel.updateTouchHandler()
    }

}
