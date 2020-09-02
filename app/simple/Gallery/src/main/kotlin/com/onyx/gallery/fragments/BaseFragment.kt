package com.onyx.gallery.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.onyx.android.sdk.utils.EventBusUtils
import com.onyx.gallery.activities.NewEditActivity
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.handler.DrawHandler
import com.onyx.gallery.viewmodel.BaseViewModel

/**
 * Created by Leung on 2020/5/6
 */
abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel> : Fragment() {

    protected lateinit var binding: T
    protected lateinit var viewModel: V
    protected val editBundle: EditBundle by lazy { NewEditActivity.globalEditBundle!! }
    protected lateinit var drawHandler: DrawHandler

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), getLayoutId(), container, false)
        val rootView = binding.root
        drawHandler = editBundle.drawHandler
        if (useEventBus()) {
            EventBusUtils.ensureRegister(editBundle.eventBus, this)
        }
        arguments?.let {
            onHandleArguments(it)
        }
        onInitView(binding, rootView)
        viewModel = onInitViewModel(requireContext(), binding, rootView)
        return rootView
    }

    override fun onDestroy() {
        super.onDestroy()
        if (useEventBus()) {
            editBundle.run { EventBusUtils.ensureUnregister(eventBus, this) }
        }
    }

    open fun useEventBus(): Boolean = false

    protected open fun onHandleArguments(bundle: Bundle) {

    }

    protected abstract fun getLayoutId(): Int

    protected abstract fun onInitView(binding: T, contentView: View)

    protected abstract fun onInitViewModel(context: Context, binding: T, rootView: View): V

    fun postEvent(event: Any) = editBundle.eventBus.post(event)

}
