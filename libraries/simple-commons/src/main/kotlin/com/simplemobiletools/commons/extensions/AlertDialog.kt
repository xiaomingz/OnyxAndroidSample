package com.simplemobiletools.commons.extensions

import android.app.AlertDialog
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import com.simplemobiletools.commons.R

fun AlertDialog.showKeyboard(editText: EditText) {
    showKeyboard(editText, true)
}

fun AlertDialog.showKeyboard(editText: EditText, adjustWindowPosition : Boolean) {
    window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    if (adjustWindowPosition) {
        window!!.attributes.gravity = Gravity.TOP
        window!!.attributes.y = getWindowPosition()
        window!!.attributes = window!!.attributes
    }
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
    return context.resources.getFraction(R.fraction.dialog_gravity_top_y, context.realScreenSize.y, context.realScreenSize.y).toInt()
}

fun AlertDialog.getWindowPositionScale(): Float {
    return if (context.isPortraitOrientation()) 3f else 4f
}

fun AlertDialog.adjustLayoutMaxHeight(contentView: View) {
    contentView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
        override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int,
                                    oldRight: Int, oldBottom: Int) {
            contentView.removeOnLayoutChangeListener(this)
            val maxHeight = context.resources.getFraction(R.fraction.dialog_layout_max_height, context.usableScreenSize.y, context.usableScreenSize.y)
            if (v.height > maxHeight) {
                window!!.setLayout(window!!.attributes.width, maxHeight.toInt())
            }
        }
    })
}

fun AlertDialog.setWindowYPosOffset(offset: Int) {
    window?.attributes?.y = offset
    window?.setAttributes(window?.attributes)
}
