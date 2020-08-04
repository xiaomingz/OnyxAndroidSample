package com.onyx.gallery.viewmodel

import androidx.lifecycle.MutableLiveData

/**
 * Created by Leung on 2020/5/6
 */
class EditMenuViewModel : BaseViewModel() {

    enum class MenuStyle {
        NONE, SHARE, GRAFFITI, TEXT, CROP, MOSAIC
    }

    var currItemMenuStyle = MutableLiveData<MenuStyle>()
    var shareToCloud = MutableLiveData<Boolean>(false)

    fun onClickMenu(menuStyle: MenuStyle) = updateItemMenuLayout(menuStyle)

    private fun updateItemMenuLayout(menuStyle: MenuStyle) {
        shareToCloud.value = menuStyle == MenuStyle.SHARE
        if (shareToCloud.value!!) {
            return
        }
        currItemMenuStyle.value = menuStyle
    }

    fun initMenu() {
        currItemMenuStyle.value = MenuStyle.GRAFFITI
    }

}