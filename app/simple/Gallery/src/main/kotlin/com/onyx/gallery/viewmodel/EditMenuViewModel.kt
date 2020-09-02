package com.onyx.gallery.viewmodel

import androidx.lifecycle.MutableLiveData
import com.onyx.gallery.bundle.EditBundle

/**
 * Created by Leung on 2020/5/6
 */
class EditMenuViewModel(editBundle: EditBundle) : BaseViewModel(editBundle) {

    enum class MenuStyle {
        NONE, GRAFFITI, TEXT, CROP, MOSAIC, ERASE
    }

    var currItemMenuStyle = MutableLiveData<MenuStyle>()

    fun onClickMenu(menuStyle: MenuStyle) = updateItemMenuLayout(menuStyle)

    private fun updateItemMenuLayout(menuStyle: MenuStyle) {
        if (isTouching()) {
            return
        }
        currItemMenuStyle.value = menuStyle
    }

    fun initMenu() {
        currItemMenuStyle.value = MenuStyle.GRAFFITI
    }

}