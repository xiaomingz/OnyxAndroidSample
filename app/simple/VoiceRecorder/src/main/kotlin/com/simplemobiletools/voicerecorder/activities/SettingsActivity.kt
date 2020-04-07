package com.simplemobiletools.voicerecorder.activities

import android.os.Bundle
import com.simplemobiletools.commons.extensions.updateTextColors
import com.simplemobiletools.voicerecorder.R
import com.simplemobiletools.voicerecorder.extensions.config
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : SimpleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

    override fun onResume() {
        super.onResume()
        setupHideNotification()
        updateTextColors(settings_scrollview)
    }

    private fun setupHideNotification() {
        settings_hide_notification.isChecked = config.hideNotification
        settings_hide_notification_holder.setOnClickListener {
            settings_hide_notification.toggle()
            config.hideNotification = settings_hide_notification.isChecked
        }
    }
}
