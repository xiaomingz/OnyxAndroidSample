package com.simplemobiletools.commons.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox
import com.simplemobiletools.commons.R

class StatusCheckBox : AppCompatCheckBox {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        updateButtonDrawable(enabled)
    }

    private fun updateButtonDrawable(enabled: Boolean) {
        if (enabled) {
            setButtonDrawable(R.drawable.onyx_custom_switch)
            return
        }
        if (isChecked) {
            setButtonDrawable(R.drawable.ic_setting_switch_on_disable)
        } else {
            setButtonDrawable(R.drawable.ic_setting_switch_off_disable)
        }
    }
}