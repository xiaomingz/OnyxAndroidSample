package com.onyx.gallery.helpers

import android.content.Context

/**
 * <pre>
 *     author : suicheng
 *     time   : 2020/5/13 10:36
 *     desc   :
 * </pre>
 */
class AppContext private constructor() {

    companion object {
        private lateinit var context: Context
        fun init(context: Context) {
            this.context = context
        }

        fun getContext(): Context {
            return context
        }
    }
}