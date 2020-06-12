package com.onyx.gallery.helpers

import android.text.Editable
import android.text.TextWatcher

/**
 * Created by Leung on 2020/6/9
 */
abstract class TextWatcherAdapter : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

}