package com.simplemobiletools.musicplayer

import android.app.Application
import com.onyx.android.sdk.utils.ResManager
import com.simplemobiletools.commons.extensions.checkUseEnglish

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        checkUseEnglish()
        ResManager.init(this)
    }
}
