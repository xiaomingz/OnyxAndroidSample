package com.onyx.gallery.viewmodel

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import com.onyx.android.sdk.data.FontInfo
import com.onyx.android.sdk.data.GPaginator
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.utils.DeviceUtils
import com.onyx.android.sdk.utils.ResManager
import com.onyx.android.sdk.utils.StringUtils
import com.onyx.gallery.R
import com.onyx.gallery.action.GetFontsAction
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.event.ui.DismissFontSelectMenuEvent
import com.onyx.gallery.fragments.FontSelectAdapter
import com.onyx.gallery.models.ItemTextStyleOptionChoiceViewModel
import com.onyx.gallery.models.MenuAction
import com.onyx.gallery.models.MenuState
import com.onyx.gallery.request.GetFontsRequest

/**
 * Created by Leung on 2020/6/13
 */

class FontSelectViewModel(editBundle: EditBundle) : BaseMenuViewModel(editBundle) {
    companion object {
        const val TAB_FONT_CN = 0
        const val TAB_FONT_EN = 1
        const val TAB_FONT_CUSTOMIZE = 2
    }

    val handler = Handler()
    var isOnyxSystemFontExist = false
    lateinit var gPaginator: GPaginator
    lateinit var fontAdapter: FontSelectAdapter
    val totalCount = MutableLiveData<String>()
    val pageIndicator = MutableLiveData<String>()
    val nextPage = MutableLiveData(0)
    val prevPage = MutableLiveData(0)
    val currentTab = MutableLiveData<Int>(getFontTabIndex())

    private var cnFonts = listOf<FontInfo>()
    private var enFonts = listOf<FontInfo>()
    private var customizeFonts = listOf<FontInfo>()

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onSaveMenuState(menuState: MenuState) {
        menuState.fontTabIndex = currentTab.value!!
    }

    override fun onUpdateMenuState(menuState: MenuState) {
        currentTab.value = menuState.fontTabIndex
    }

    fun onTypeChecked(type: Int) {
        if (currentTab.value === type) {
            return
        }
        currentTab.value = type
        bindFontListData()
    }

    override fun onHandleMenu(action: MenuAction): Boolean {
        when (action) {
            MenuAction.NEXT_PAGE -> onNextPage()
            MenuAction.PREV_PAGE -> onPrevPage()
            MenuAction.CANCEL -> onDismiss()
        }
        return true
    }

    private fun onNextPage() {
        nextPage.value = nextPage.value!!.plus(1)
    }

    private fun onPrevPage() {
        prevPage.value = prevPage.value!!.plus(1)
    }

    private fun onDismiss() = postEvent(DismissFontSelectMenuEvent())

    fun loadFontList() = GetFontsAction(editBundle).setCurrentFont(getCurrFontName()).execute(onGetFontsResult())

    private fun getCurrFontName() = editBundle.insertTextHandler.insertTextConfig.fontFace

    private fun onGetFontsResult(): RxCallback<GetFontsRequest> = object : RxCallback<GetFontsRequest>() {
        override fun onNext(getFontsRequest: GetFontsRequest) {
            cnFonts = getFontsRequest.chineseFontList
            enFonts = getFontsRequest.englishFontList
            customizeFonts = getFontsRequest.customizeFonts
            isOnyxSystemFontExist = getFontsRequest.isOnyxSystemFontExist
            bindFontListData()
        }
    }

    private fun bindFontListData() {
        fontAdapter.run {
            data.apply { clear() }.apply { addAll(getFontsByTab(currentTab.value!!)) }
            notifyDataSetChanged()
            resizeGPaginator(data.size)
        }
        totalCount.value = ResManager.getString(R.string.total_count, fontAdapter.data.size)
        handler.postDelayed({ updatePageIndicator() }, 300L)
    }

    private fun resizeGPaginator(size: Int) {
        gPaginator.currentPage = 0
        gPaginator.resize(size)
    }

    private fun getFontsByTab(tab: Int): MutableList<ItemTextStyleOptionChoiceViewModel> = when (tab) {
        TAB_FONT_CN -> getCnFonts()
        TAB_FONT_EN -> getEnFonts()
        else -> getCustomizeFonts()
    }

    private fun getCnFonts(): MutableList<ItemTextStyleOptionChoiceViewModel> = convertFontInfo(cnFonts)

    private fun getEnFonts(): MutableList<ItemTextStyleOptionChoiceViewModel> = convertFontInfo(enFonts)

    private fun getCustomizeFonts(): MutableList<ItemTextStyleOptionChoiceViewModel> = convertFontInfo(customizeFonts)

    fun updatePageIndicator() {
        pageIndicator.value = String.format(ResManager.getString(R.string.page_index), Math.max(1, gPaginator.currentPage + 1), Math.max(1, gPaginator.pages()))
    }

    private fun convertFontInfo(fonts: List<FontInfo>): MutableList<ItemTextStyleOptionChoiceViewModel> {
        val itemFontList = mutableListOf<ItemTextStyleOptionChoiceViewModel>()
        var currentItem: ItemTextStyleOptionChoiceViewModel? = null
        for (fontInfo in fonts) {
            if (StringUtils.isEquals(getCurrFontName(), fontInfo.name)) {
                currentItem = ItemTextStyleOptionChoiceViewModel(fontInfo.name, true).setTypeface(fontInfo.typeface).setFontInfo(fontInfo)
                continue
            }
            if (StringUtils.isNullOrEmpty(getCurrFontName()) && isOnyxSystemFontExist && StringUtils.isEquals(DeviceUtils.ONYX_SYSTEM_DEFAULT_SYSTEM_FONT_ID, fontInfo.id)) {
                currentItem = ItemTextStyleOptionChoiceViewModel(fontInfo.name, true).setTypeface(fontInfo.typeface).setFontInfo(fontInfo)
                continue
            }
            itemFontList.add(ItemTextStyleOptionChoiceViewModel(fontInfo.name, false).setTypeface(fontInfo.typeface).setFontInfo(fontInfo))
        }
        if (currentItem != null) {
            itemFontList.add(0, currentItem)
        }
        return itemFontList
    }

}
