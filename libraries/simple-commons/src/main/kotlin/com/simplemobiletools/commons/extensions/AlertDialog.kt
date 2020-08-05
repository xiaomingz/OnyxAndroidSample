package com.simplemobiletools.commons.extensions

import android.app.AlertDialog
import android.view.Gravity
import android.view.WindowManager
import android.widget.EditText

fun AlertDialog.showKeyboard(editText: EditText) {
    window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    window!!.attributes.gravity = Gravity.TOP
    window!!.attributes.y = getWindowPosition()
    window!!.attributes = window!!.attributes
    editText.apply {
        requestFocus()
        onGlobalLayout {
            setSelection(text.toString().length)
        }
    }
}

fun AlertDialog.hideKeyboard() {
    window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
}

fun AlertDialog.getWindowPosition(): Int {
    return (context.realScreenSize.y / getWindowPositionScale()).toInt()
}

fun AlertDialog.getWindowPositionScale(): Float {
    return if (context.isPortraitOrientation()) 3f else 4f
}
