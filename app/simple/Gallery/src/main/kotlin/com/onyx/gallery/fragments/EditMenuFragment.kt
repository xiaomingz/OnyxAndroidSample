package com.onyx.gallery.fragments

import android.content.Context
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.onyx.gallery.R
import com.onyx.gallery.databinding.FragmentEditMenuBinding
import com.onyx.gallery.event.ui.CloseCropEvent
import com.onyx.gallery.event.ui.OpenCropEvent
import com.onyx.gallery.extensions.replaceLoadFragment
import com.onyx.gallery.request.RestoreTransformRequest
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
        val editMenuViewModel = ViewModelProvider(requireActivity()).get(EditMenuViewModel::class.java)
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
        menuStyle?.apply { updateSubMenuFragment(this) }
    }

    private fun updateSubMenuFragment(menuStyle: EditMenuViewModel.MenuStyle) {
        var fragment = menuFragmentMap[menuStyle]
        if (fragment == null) {
            fragment = createSubMenuFragment(menuStyle)
            menuFragmentMap[menuStyle] = fragment
        }
        handlerMenuChange(menuStyle)
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

    private fun handlerMenuChange(menuStyle: EditMenuViewModel.MenuStyle) {
        when (menuStyle) {
            EditMenuViewModel.MenuStyle.GRAFFITI -> {
                onGraffitiMenuClick()
            }
            EditMenuViewModel.MenuStyle.TEXT -> {
                onTextMenuClick()
            }
            EditMenuViewModel.MenuStyle.CROP -> {
                onCropMenuClick()
            }
        }
        globalEditBundle.enqueue(RestoreTransformRequest(), null)
    }

    private fun onGraffitiMenuClick() {
        postEvent(CloseCropEvent())
        globalEditBundle.supportZoom = true
        globalEditBundle.canFingerTouch = true
    }

    private fun onTextMenuClick() {
        postEvent(CloseCropEvent())
        globalEditBundle.supportZoom = false
        globalEditBundle.canFingerTouch = false
    }

    private fun onCropMenuClick() {
        postEvent(OpenCropEvent())
        globalEditBundle.supportZoom = true
        globalEditBundle.canFingerTouch = true
    }

}
