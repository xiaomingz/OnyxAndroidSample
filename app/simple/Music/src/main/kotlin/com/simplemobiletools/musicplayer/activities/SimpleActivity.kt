package com.simplemobiletools.musicplayer.activities

import android.annotation.SuppressLint
import android.os.Bundle
import com.simplemobiletools.commons.activities.BaseSimpleActivity
import com.simplemobiletools.commons.extensions.baseConfig
import com.simplemobiletools.musicplayer.R

@SuppressLint("Registered")
open class SimpleActivity : BaseSimpleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        useDynamicTheme = false
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (!useDynamicTheme) {
            updateActionbarColor(getActionbarColor())
        }
    }

    override fun getAppIconIDs() = arrayListOf(
            R.drawable.ic_launcher
    )

    override fun getAppLauncherName() = getString(R.string.app_launcher_name)

    open fun getActionbarColor(): Int {
        return baseConfig.backgroundColor;
    }
}
