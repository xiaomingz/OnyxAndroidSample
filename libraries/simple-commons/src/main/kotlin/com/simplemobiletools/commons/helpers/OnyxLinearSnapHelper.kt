package com.simplemobiletools.commons.helpers

import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.onyx.android.sdk.device.Device


/**
 * <pre>
 *     author : suicheng
 *     time   : 2020/4/28 19:15
 *     desc   :
 * </pre>
 */
class OnyxLinearSnapHelper : LinearSnapHelper() {

    private val scrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        var isA2Mode = false
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            when (newState) {
                RecyclerView.SCROLL_STATE_IDLE -> {
                    isA2Mode = false
                    applyFastMode(isA2Mode)
                }
                else -> if (!isA2Mode) {
                    isA2Mode = true
                    applyFastMode(isA2Mode)
                }
            }
        }
    }

    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        super.attachToRecyclerView(recyclerView)
        recyclerView?.addOnScrollListener(scrollListener)
    }

    private fun applyFastMode(enable: Boolean) {
        Device.currentDevice.applyApplicationFastMode(javaClass.simpleName, enable, false)
    }
}