package com.onyx.gallery.extensions

import androidx.fragment.app.Fragment

/**
 * Created by Leung on 2020/5/6
 */
fun Fragment.replaceLoadFragment(replaceId: Int, fragment: Fragment) {
    childFragmentManager.beginTransaction()
            .replace(replaceId, fragment)
            .commitNow()
}