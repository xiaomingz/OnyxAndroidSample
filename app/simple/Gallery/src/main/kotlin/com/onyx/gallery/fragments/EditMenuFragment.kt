package com.onyx.gallery.fragments

import android.content.Context
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.onyx.gallery.R
import com.onyx.gallery.databinding.FragmentEditMenuBinding
import com.onyx.gallery.extensions.replaceLoadFragment
import com.onyx.gallery.viewmodel.EditMenuViewModel
import java.util.*

/**
 * Created by Leung on 2020/4/30
 */
class EditMenuFragment : BaseFragment<FragmentEditMenuBinding>(), Observer<EditMenuViewModel.MenuStyle> {

    private lateinit var editMenuViewModel: EditMenuViewModel
    private val menuFragmentMap = HashMap<EditMenuViewModel.MenuStyle, BaseFragment<*>>()

    override fun getLayoutId(): Int = R.layout.fragment_edit_menu

    override fun onInitView(binding: FragmentEditMenuBinding, contentView: View) {
    }

    override fun onInitViewModel(context: Context, binding: FragmentEditMenuBinding, rootView: View) {
        editMenuViewModel = ViewModelProvider(this).get(EditMenuViewModel::class.java)
        binding.viewModel = editMenuViewModel
        binding.lifecycleOwner = this
        editMenuViewModel.currItemMenuStyle.observe(this, this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        editMenuViewModel.currItemMenuStyle.removeObservers(this);
    }

    override fun onChanged(menuStyle: EditMenuViewModel.MenuStyle?) {
        menuStyle?.apply { updateSubMenuFragment(this) }
    }

    private fun updateSubMenuFragment(menuStyle: EditMenuViewModel.MenuStyle) {
        var fragment = menuFragmentMap[menuStyle]
        if (fragment == null) {
            fragment = createSubMenuFragment(menuStyle)
            menuFragmentMap[menuStyle] = fragment
        }
        replaceLoadFragment(R.id.item_sub_menu_layout, fragment)
    }

    private fun createSubMenuFragment(menuStyle: EditMenuViewModel.MenuStyle): BaseFragment<*> = when (menuStyle) {
        EditMenuViewModel.MenuStyle.SHARE -> ShareMenuFragment()
        EditMenuViewModel.MenuStyle.GRAFFITI -> GraffitiMenuFragment()
        EditMenuViewModel.MenuStyle.TEXT -> TextMenuFragment()
        EditMenuViewModel.MenuStyle.CROP -> CropMenuFragment()
        EditMenuViewModel.MenuStyle.MOSAIC -> MosaicMenuFragment()
        else -> NoneMenuFragment()
    }

}
