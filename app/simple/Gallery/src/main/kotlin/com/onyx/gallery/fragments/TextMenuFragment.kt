package com.onyx.gallery.fragments

import android.content.Context
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.onyx.gallery.R
import com.onyx.gallery.databinding.FragmentEditMenuTextBinding
import com.onyx.gallery.event.ui.DismissFontSelectMenuEvent
import com.onyx.gallery.event.ui.FontChangeEvent
import com.onyx.gallery.event.ui.ShowFontSelectMenuEvent
import com.onyx.gallery.extensions.addFragment
import com.onyx.gallery.extensions.showFragment
import com.onyx.gallery.viewmodel.TextMenuViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Leung on 2020/5/6
 */
class TextMenuFragment : BaseMenuFragment<FragmentEditMenuTextBinding, TextMenuViewModel>() {

    private val fontSelectFragment: FontSelectFragment by lazy { FontSelectFragment() }

    override fun useEventBus(): Boolean = true

    override fun getLayoutId(): Int = R.layout.fragment_edit_menu_text

    override fun onInitView(binding: FragmentEditMenuTextBinding, contentView: View) {
    }

    override fun onInitViewModel(context: Context, binding: FragmentEditMenuTextBinding, rootView: View): TextMenuViewModel {
        val textMenuViewModel = ViewModelProvider(requireActivity()).get(TextMenuViewModel::class.java)
        binding.viewModel = textMenuViewModel
        binding.lifecycleOwner = this
        return textMenuViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.showContent()
        globalEditBundle.insertTextHandler.saveTextShape(true)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onShowFontSelectMenuEvent(event: ShowFontSelectMenuEvent) {
        val fragment = childFragmentManager.findFragmentByTag(FontSelectFragment::class.java.simpleName)
        if (fragment != null) {
            showFragment(fragment)
            return
        }
        addFragment(R.id.sub_menu_content, fontSelectFragment)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDismissFontSelectMenuEvent(event: DismissFontSelectMenuEvent? = null) {
        viewModel.showContent()
        childFragmentManager.popBackStack()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFontChangeEvent(event: FontChangeEvent) {
        viewModel.onFontChange(event.fontInfo)
    }

}