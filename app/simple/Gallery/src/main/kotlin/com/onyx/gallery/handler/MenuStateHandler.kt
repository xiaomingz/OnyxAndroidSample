package com.onyx.gallery.handler

import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.models.MenuState
import com.onyx.gallery.viewmodel.BaseViewModel

/**
 * Created by Leung 2020/9/17 16:47
 **/
class MenuStateHandler(val editBundle: EditBundle) {

    private val stateMap = mutableMapOf<Class<out BaseViewModel>, MenuState>()

    fun getMenuViewModelState(clazz: Class<out BaseViewModel>): MenuState {
        var menuViewModelState: MenuState? = null
        if (stateMap.containsKey(clazz)) {
            menuViewModelState = stateMap.get(clazz)
        } else {
            menuViewModelState = MenuState()
            stateMap[clazz] = menuViewModelState
        }
        return menuViewModelState!!
    }

    fun release() {
        stateMap.clear()
    }


}