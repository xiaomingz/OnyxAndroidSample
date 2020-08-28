package com.onyx.android.sdk.kui.helper

import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.onyx.android.sdk.api.device.epd.EpdController
import com.onyx.android.sdk.device.Device
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class OnyxLinearSnapHelper : LinearSnapHelper() {
    private var fullscreenRefreshSubject = PublishSubject.create<Boolean>()
    private val FULLSCREEN_REFRESH_DEBOUNCE_TIMEOUT = 1100L
    private var isFastMode = false

    private val scrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            when (newState) {
                RecyclerView.SCROLL_STATE_IDLE -> {
                    fullscreenRefreshSubject.onNext(true)
                }
                else -> {
                    if (!isFastMode) {
                        isFastMode = true
                        applyFastMode(isFastMode)
                    }
                    fullscreenRefreshSubject.onNext(false)
                }
            }
        }
    }

    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        super.attachToRecyclerView(recyclerView)
        recyclerView?.addOnScrollListener(scrollListener)
        initFullscreenRefreshSubject()
    }

    private fun applyFastMode(enable: Boolean) {
        Device.currentDevice.applyApplicationFastMode(javaClass.simpleName, enable, false)
    }

    private fun initFullscreenRefreshSubject() {
        fullscreenRefreshSubject.debounce(FULLSCREEN_REFRESH_DEBOUNCE_TIMEOUT, TimeUnit.MILLISECONDS)
                .subscribe {
                    if (it) {
                        isFastMode = false
                        applyFastMode(isFastMode)
                        EpdController.applyGCOnce()
                    }
                }
    }
}