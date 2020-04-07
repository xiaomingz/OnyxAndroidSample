package com.onyx.gallery.action

import com.onyx.gallery.BuildConfig
import com.onyx.gallery.R
import com.simplemobiletools.commons.activities.BaseSimpleActivity
import com.simplemobiletools.commons.extensions.checkWhatsNew
import com.simplemobiletools.commons.models.Release

/**
 * <pre>
 *     author : suicheng
 *     time   : 2020/4/1 16:55
 *     desc   :
 * </pre>
 */
class ShowWhatsNewDialogAction(val show: Boolean = false) {

    fun execute(activity: BaseSimpleActivity) {
        if (!show) {
            return
        }
        arrayListOf<Release>().apply {
            add(Release(213, R.string.release_213))
            add(Release(217, R.string.release_217))
            add(Release(220, R.string.release_220))
            add(Release(221, R.string.release_221))
            add(Release(225, R.string.release_225))
            add(Release(258, R.string.release_258))
            add(Release(277, R.string.release_277))
            add(Release(295, R.string.release_295))
            activity.checkWhatsNew(this, BuildConfig.VERSION_CODE)
        }
    }
}