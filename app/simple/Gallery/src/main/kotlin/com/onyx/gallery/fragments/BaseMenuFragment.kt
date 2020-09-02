package com.onyx.gallery.fragments

import androidx.databinding.ViewDataBinding
import com.onyx.gallery.event.ui.UpdateTouchHandlerEvent
import com.onyx.gallery.viewmodel.BaseMenuViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Leung on 2020/5/6
 */
abstract class BaseMenuFragment<T : ViewDataBinding, V : BaseMenuViewModel> : BaseFragment<T, V>() {
    override fun useEventBus(): Boolean = true

    override fun onResume() {
        super.onResume()
        viewModel.updateTouchHandler()
    }

}
