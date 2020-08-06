package com.onyx.gallery.fragments

import android.content.Context
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.onyx.gallery.R
import com.onyx.gallery.databinding.FragmentEditMenuBinding
import com.onyx.gallery.dialogs.DialogShare
import com.onyx.gallery.event.ui.CloseCropEvent
import com.onyx.gallery.event.ui.InitMenuEvent
import com.onyx.gallery.event.ui.OpenCropEvent
import com.onyx.gallery.extensions.replaceLoadFragment
import com.onyx.gallery.request.RestoreTransformRequest
import com.onyx.gallery.utils.ToastUtils
import com.onyx.gallery.viewmodel.EditMenuViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * Created by Leung on 2020/4/30
 */
class EditMenuFragment : BaseFragment<FragmentEditMenuBinding, EditMenuViewModel>(), Observer<EditMenuViewModel.MenuStyle> {

    override fun useEventBus(): Boolean = true

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onInitMenuEvent(event: InitMenuEvent) {
        viewModel.initMenu()
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
        EditMenuViewModel.MenuStyle.GRAFFITI -> GraffitiMenuFragment()
        EditMenuViewModel.MenuStyle.TEXT -> TextMenuFragment()
        EditMenuViewModel.MenuStyle.CROP -> CropMenuFragment()
        EditMenuViewModel.MenuStyle.MOSAIC -> MosaicMenuFragment()
        else -> NoneMenuFragment()
    }

    private fun handlerMenuChange(menuStyle: EditMenuViewModel.MenuStyle) {
        when (menuStyle) {
            EditMenuViewModel.MenuStyle.GRAFFITI -> onGraffitiMenuClick()
            EditMenuViewModel.MenuStyle.TEXT -> onTextMenuClick()
            EditMenuViewModel.MenuStyle.CROP -> onCropMenuClick()
            EditMenuViewModel.MenuStyle.MOSAIC -> onMosaicMenuClick()
        }
        globalEditBundle.enqueue(RestoreTransformRequest(), null)
    }

    private fun onGraffitiMenuClick() {
        closeCropMenu()
        globalEditBundle.supportZoom = true
        globalEditBundle.canFingerTouch = true
    }

    private fun onTextMenuClick() {
        closeCropMenu()
        globalEditBundle.supportZoom = false
        globalEditBundle.canFingerTouch = false
    }

    private fun onCropMenuClick() {
        openCropMenu()
        globalEditBundle.supportZoom = true
        globalEditBundle.canFingerTouch = true
    }

    private fun onMosaicMenuClick() {
        closeCropMenu()
        globalEditBundle.supportZoom = false
        globalEditBundle.canFingerTouch = false
    }

    private fun openCropMenu() {
        postEvent(OpenCropEvent())
    }

    private fun closeCropMenu() {
        postEvent(CloseCropEvent())
    }

}
