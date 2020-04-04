package com.simplemobiletools.voicerecorder.activities

import android.annotation.SuppressLint
import android.os.Bundle
import com.simplemobiletools.commons.activities.BaseSimpleActivity
import com.simplemobiletools.commons.extensions.baseConfig
import com.simplemobiletools.voicerecorder.R

@SuppressLint("Registered")
open class SimpleActivity : BaseSimpleActivity() {
    override fun getAppIconIDs() = arrayListOf(
        R.mipmap.ic_launcher
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        useDynamicTheme = false
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (!useDynamicTheme) {
            updateActionbarColor(getCustomActionBarColor())
        }
    }

    override fun getAppLauncherName() = getString(R.string.app_launcher_name)

    open fun getCustomActionBarColor(): Int {
        return baseConfig.backgroundColor
    }
}
