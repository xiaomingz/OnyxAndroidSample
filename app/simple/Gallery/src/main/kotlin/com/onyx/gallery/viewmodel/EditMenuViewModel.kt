package com.onyx.gallery.viewmodel

import androidx.lifecycle.MutableLiveData

/**ø
 * Created by Leung on 2020/5/6
 */
class EditMenuViewModel : BaseViewModel() {

    enum class MenuStyle {
        NONE, SHARE, GRAFFITI, TEXT, CROP, MOSAIC
    }

    var currItemMenuStyle = MutableLiveData<MenuStyle>()

    fun onClickMenu(menuStyle: MenuStyle) = updateItemMenuLayout(menuStyle)

    private fun updateItemMenuLayout(menuStyle: MenuStyle) {
        currItemMenuStyle.value = menuStyle
    }

    fun initMenu() {
        currItemMenuStyle.value = MenuStyle.GRAFFITI
    }

}