package com.simplemobiletools.commons.extensions

import android.app.AlertDialog
import android.view.Gravity
import android.view.View
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

fun AlertDialog.adjustLayoutMaxHeight(contentView: View) {
    contentView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
        override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int,
                                    oldRight: Int, oldBottom: Int) {
            contentView.removeOnLayoutChangeListener(this)
            val maxHeight = context.usableScreenSize.y * 0.8f;
            if (v.height > maxHeight) {
                window!!.setLayout(window!!.attributes.width, maxHeight.toInt())
            }
        }
    })
}
