package com.simplemobiletools.commons.views

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager

class DisableScrollGridManager : GridLayoutManager {
    private var canScroll = false

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {}
    constructor(context: Context?) : super(context, 1) {}
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(context, 1, orientation, reverseLayout) {}
    constructor(context: Context?, spanCount: Int) : super(context, spanCount) {}
    constructor(context: Context?, spanCount: Int, orientation: Int,
                reverseLayout: Boolean) : super(context, spanCount, orientation, reverseLayout) {
    }

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