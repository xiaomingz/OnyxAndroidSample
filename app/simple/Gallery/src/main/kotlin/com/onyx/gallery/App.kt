package com.onyx.gallery

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.github.ajalt.reprint.core.Reprint
import com.onyx.android.sdk.utils.ResManager
import com.simplemobiletools.commons.extensions.checkUseEnglish

class App : MultiDexApplication() {

    companion object {
        lateinit var instance: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        ResManager.init(this)
        checkUseEnglish()
        Reprint.initialize(this)
    }
}
