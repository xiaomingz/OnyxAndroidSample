package com.onyx.gallery.event.result

/**
 * Created by Leung on 2020/5/11
 */
abstract class BaseResultEvent(private val throwable: Throwable?) {
    fun isSuccess() = throwable == null
}