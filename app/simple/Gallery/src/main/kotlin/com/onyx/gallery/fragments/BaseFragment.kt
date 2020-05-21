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
import com.onyx.gallery.bundle.GlobalEditBundle

/**
 * Created by Leung on 2020/5/6
 */
abstract class BaseFragment<T : ViewDataBinding> : Fragment() {

    protected lateinit var binding: T

    protected val globalEditBundle = GlobalEditBundle.instance

    protected val noteManager = globalEditBundle.noteManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (useEventBus()) {
            EventBusUtils.ensureRegister(globalEditBundle.eventBus, this)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), getLayoutId(), container, false)
        val rootView = binding.root
        arguments?.let {
            onHandleArguments(it)
        }
        onInitView(binding, rootView)
        onInitViewModel(requireContext(), binding, rootView)
        return rootView
    }

    override fun onDestroy() {
        super.onDestroy()
        if (useEventBus()) {
            EventBusUtils.ensureUnregister(globalEditBundle.eventBus, this)
        }
    }

    open fun useEventBus(): Boolean = false

    protected open fun onHandleArguments(bundle: Bundle) {

    }

    protected abstract fun getLayoutId(): Int

    protected abstract fun onInitView(binding: T, contentView: View)

    protected abstract fun onInitViewModel(context: Context, binding: T, rootView: View)

}
