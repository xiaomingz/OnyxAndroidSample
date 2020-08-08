package com.onyx.gallery.viewmodel

import androidx.lifecycle.MutableLiveData

/**
 * Created by Leung 2020/8/8 12:18
 **/
class ActionBarViewModel(private val onBlackClickCallback: () -> Unit) : BaseViewModel() {
    val title = MutableLiveData<String>()
    lateinit var onMoreClickCallback: () -> Unit

    fun onBlackClick() {
        onBlackClickCallback()
    }

    fun onMoreClick() {
        onMoreClickCallback()
    }
}