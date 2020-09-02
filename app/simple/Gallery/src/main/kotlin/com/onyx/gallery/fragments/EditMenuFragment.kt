package com.onyx.gallery.fragments

import android.content.Context
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.onyx.gallery.R
import com.onyx.gallery.databinding.FragmentEditMenuBinding
import com.onyx.gallery.event.ui.CloseCropEvent
import com.onyx.gallery.event.ui.InitMenuEvent
import com.onyx.gallery.event.ui.OpenCropEvent
import com.onyx.gallery.event.ui.UpdateTouchHandlerEvent
import com.onyx.gallery.extensions.replaceLoadFragment
import com.onyx.gallery.request.RestoreTransformRequest
import com.onyx.gallery.viewmodel.BaseMenuViewModel
import com.onyx.gallery.viewmodel.BaseViewModel
import com.onyx.gallery.viewmodel.EditMenuViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * Created by Leung on 2020/4/30
 */
class EditMenuFragment : BaseFragment<FragmentEditMenuBinding, EditMenuViewModel>(), Observer<EditMenuViewModel.MenuStyle> {

    private var activateFragment: BaseFragment<*, *>? = null

    override fun useEventBus(): Boolean = true

    private val menuFragmentMap = HashMap<EditMenuViewModel.MenuStyle, BaseFragment<*, *>>()

    override fun getLayoutId(): Int = R.layout.fragment_edit_menu

    override fun onInitView(binding: FragmentEditMenuBinding, contentView: View) {
    }

    override fun onInitViewModel(context: Context, binding: FragmentEditMenuBinding, rootView: View): EditMenuViewModel {
        val editMenuViewModel = ViewModelProvider(requireActivity(), BaseViewModel.ViewModeFactory(editBundle)).get(EditMenuViewModel::class.java)
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateTouchHandlerEvent(event: UpdateTouchHandlerEvent) {
        val activateFragment = activateFragment
        if (activateFragment is BaseMenuFragment) {
            val viewModel = activateFragment.viewModel
            if (viewModel is BaseMenuViewModel) {
                viewModel.updateTouchHandler()
            }
        }
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
        activateFragment = fragment
        handlerMenuChange(menuStyle)
        replaceLoadFragment(R.id.item_sub_menu_layout, fragment)
    }

    private fun createSubMenuFragment(menuStyle: EditMenuViewModel.MenuStyle): BaseFragment<*, *> = when (menuStyle) {
        EditMenuViewModel.MenuStyle.GRAFFITI -> GraffitiMenuFragment()
        EditMenuViewModel.MenuStyle.TEXT -> TextMenuFragment()
        EditMenuViewModel.MenuStyle.CROP -> CropMenuFragment()
        EditMenuViewModel.MenuStyle.MOSAIC -> MosaicMenuFragment()
        EditMenuViewModel.MenuStyle.ERASE -> EraseMenuFragment()
        else -> NoneMenuFragment()
    }

    private fun handlerMenuChange(menuStyle: EditMenuViewModel.MenuStyle) {
        when (menuStyle) {
            EditMenuViewModel.MenuStyle.GRAFFITI -> onGraffitiMenuClick()
            EditMenuViewModel.MenuStyle.TEXT -> onTextMenuClick()
            EditMenuViewModel.MenuStyle.CROP -> onCropMenuClick()
            EditMenuViewModel.MenuStyle.MOSAIC -> onMosaicMenuClick()
            EditMenuViewModel.MenuStyle.ERASE -> onEraseMenuClick()
        }
        editBundle.enqueue(RestoreTransformRequest(editBundle), null)
    }

    private fun onGraffitiMenuClick() {
        closeCropMenu()
        editBundle.supportZoom = true
        editBundle.canFingerTouch = true
    }

    private fun onTextMenuClick() {
        closeCropMenu()
        editBundle.supportZoom = false
        editBundle.canFingerTouch = false
    }

    private fun onCropMenuClick() {
        openCropMenu()
        editBundle.supportZoom = true
        editBundle.canFingerTouch = true
    }

    private fun onMosaicMenuClick() {
        closeCropMenu()
        editBundle.supportZoom = false
        editBundle.canFingerTouch = false
    }

    private fun onEraseMenuClick() {
        closeCropMenu()
        editBundle.supportZoom = false
        editBundle.canFingerTouch = false
    }

    private fun openCropMenu() {
        postEvent(OpenCropEvent())
    }

    private fun closeCropMenu() {
        postEvent(CloseCropEvent())
    }

}
