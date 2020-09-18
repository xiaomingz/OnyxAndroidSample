package com.onyx.gallery.viewmodel

import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.models.MenuState

/**
 * Created by Leung on 2020/6/8
 */
class NoneMenuViewModel(editBundle: EditBundle) : BaseMenuViewModel(editBundle) {

    override fun supportTouchHandler() = false

    override fun onSaveMenuState(menuState: MenuState) {

    }

    override fun onUpdateMenuState(menuState: MenuState) {

    }
}