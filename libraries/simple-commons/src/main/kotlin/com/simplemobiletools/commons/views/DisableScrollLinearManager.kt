package com.simplemobiletools.commons.views

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager

class DisableScrollLinearManager : LinearLayoutManager {
    private var canScroll = false

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {}

    fun setScrollEnable(enable: Boolean) {
        canScroll = enable
    }

    override fun canScrollVertically(): Boolean {
        return canScroll
    }

    override fun canScrollHorizontally(): Boolean {
        return canScroll
    }
}