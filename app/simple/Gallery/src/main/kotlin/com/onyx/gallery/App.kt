package com.onyx.gallery

import androidx.multidex.MultiDexApplication
import com.github.ajalt.reprint.core.Reprint
import com.onyx.gallery.helpers.AppContext
import com.simplemobiletools.commons.extensions.checkUseEnglish

class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        checkUseEnglish()
        Reprint.initialize(this)
        AppContext.init(this)
    }
}
