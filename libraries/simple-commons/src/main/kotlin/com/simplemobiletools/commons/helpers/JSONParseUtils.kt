package com.simplemobiletools.commons.helpers

import com.alibaba.fastjson.JSON
import java.lang.Exception

/**
 * <pre>
 *     author : suicheng
 *     time   : 2020/5/9 16:01
 *     desc   :
 * </pre>
 */
class JSONParseUtils private constructor() {
    companion object {
        fun <T> parseObjectSafely(content: String?, classOfT: Class<T>): T? {
            try {
                return JSON.parseObject(content, classOfT)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }
}