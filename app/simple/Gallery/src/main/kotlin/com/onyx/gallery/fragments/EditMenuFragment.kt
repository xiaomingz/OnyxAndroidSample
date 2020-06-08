package com.onyx.gallery.fragments

import android.content.Context
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.onyx.gallery.R
import com.onyx.gallery.databinding.FragmentEditMenuBinding
import com.onyx.gallery.extensions.replaceLoadFragment
import com.onyx.gallery.handler.touch.TouchHandlerType
import com.onyx.gallery.viewmodel.EditMenuViewModel
import java.util.*

/**
 * Created by Leung on 2020/4/30
 */
class EditMenuFragment : BaseFragment<FragmentEditMenuBinding, EditMenuViewModel>(), Observer<EditMenuViewModel.MenuStyle> {

    private val menuFragmentMap = HashMap<EditMenuViewModel.MenuStyle, BaseFragment<*, *>>()

    override fun getLayoutId(): Int = R.layout.fragment_edit_menu

    override fun onInitView(binding: FragmentEditMenuBinding, contentView: View) {
    }

    override fun onInitViewModel(context: Context, binding: FragmentEditMenuBinding, rootView: View): EditMenuViewModel {
        val editMenuViewModel = ViewModelProvider(this).get(EditMenuViewModel::class.java)
        binding.viewModel = editMenuViewModel
        binding.lifecycleOwner = this
        editMenuViewModel.currItemMenuStyle.observe(this, this)
        return editMenuViewModel
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.currItemMenuStyle.removeObservers(this)
        menuFragmentMap.clear()
    }

    override fun onChanged(menuStyle: EditMenuViewModel.MenuStyle?) {
        menuStyle?.apply {
            updateTouchHandler(this)
            updateSubMenuFragment(this)
        }
    }

    private fun updateTouchHandler(menuStyle: EditMenuViewModel.MenuStyle) {
        val touchHandlerManager = globalEditBundle.touchHandlerManager
        val touchHandlerType = when (menuStyle) {
            EditMenuViewModel.MenuStyle.GRAFFITI -> TouchHandlerType.SCRIBBLE
            EditMenuViewModel.MenuStyle.TEXT -> TouchHandlerType.TEXT_INSERTION
            else -> TouchHandlerType.SCRIBBLE
        }
        touchHandlerManager.activateHandler(touchHandlerType)
    }

    private fun updateSubMenuFragment(menuStyle: EditMenuViewModel.MenuStyle) {
        var fragment = menuFragmentMap[menuStyle]
        if (fragment == null) {
            fragment = createSubMenuFragment(menuStyle)
            menuFragmentMap[menuStyle] = fragment
        }
        replaceLoadFragment(R.id.item_sub_menu_layout, fragment)
    }

    private fun createSubMenuFragment(menuStyle: EditMenuViewModel.MenuStyle): BaseFragment<*, *> = when (menuStyle) {
        EditMenuViewModel.MenuStyle.SHARE -> ShareMenuFragment()
        EditMenuViewModel.MenuStyle.GRAFFITI -> GraffitiMenuFragment()
        EditMenuViewModel.MenuStyle.TEXT -> TextMenuFragment()
        EditMenuViewModel.MenuStyle.CROP -> CropMenuFragment()
        EditMenuViewModel.MenuStyle.MOSAIC -> MosaicMenuFragment()
        else -> NoneMenuFragment()
    }

}
