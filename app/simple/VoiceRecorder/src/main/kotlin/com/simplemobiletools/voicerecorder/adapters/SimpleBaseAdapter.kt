package com.simplemobiletools.voicerecorder.adapters

import android.view.Menu
import androidx.databinding.ViewDataBinding
import com.simplemobiletools.commons.activities.BaseSimpleActivity
import com.simplemobiletools.voicerecorder.view.MyPageRecyclerViewAdapter
import com.simplemobiletools.voicerecorder.view.PageRecyclerView

abstract class SimpleBaseAdapter<T : ViewDataBinding>(activity: BaseSimpleActivity, recyclerView: PageRecyclerView, itemClick: (Any) -> Unit) :
        MyPageRecyclerViewAdapter<T>(activity, recyclerView, null, itemClick) {

    override fun prepareActionMode(menu: Menu) {}

    override fun onActionModeCreated() {}

    override fun onActionModeDestroyed() {}

    override fun getSelectableItemCount() = itemCount

    override fun getIsItemSelectable(position: Int) = true
}
