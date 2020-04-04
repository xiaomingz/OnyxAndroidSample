package com.simplemobiletools.voicerecorder.adapters

import android.view.Menu
import com.simplemobiletools.commons.activities.BaseSimpleActivity
import com.simplemobiletools.commons.adapters.MyRecyclerViewAdapter
import com.simplemobiletools.commons.views.MyRecyclerView

abstract class SimpleBaseAdapter(activity: BaseSimpleActivity, recyclerView: MyRecyclerView, itemClick: (Any) -> Unit) :
        MyRecyclerViewAdapter(activity, recyclerView, null, itemClick) {

    override fun prepareActionMode(menu: Menu) {}

    override fun onActionModeCreated() {}

    override fun onActionModeDestroyed() {}

    override fun getSelectableItemCount() = itemCount

    override fun getIsItemSelectable(position: Int) = true
}
