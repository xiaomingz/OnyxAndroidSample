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

fun Fragment.addFragment(replaceId: Int, fragment: Fragment) {
    val tag = fragment::class.java.simpleName
    childFragmentManager.beginTransaction()
            .add(replaceId, fragment, tag)
            .addToBackStack(tag)
            .commitNow()
}

fun Fragment.showFragment(fragment: Fragment) {
    childFragmentManager.beginTransaction()
            .show(fragment)
            .commitNow()
}
