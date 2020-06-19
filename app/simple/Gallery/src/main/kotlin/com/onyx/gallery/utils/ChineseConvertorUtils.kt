package com.onyx.gallery.utils

import taobe.tec.jcc.JChineseConvertor
import java.io.IOException


/**
 * Created by Leung on 2020/6/19
 */
object ChineseConvertorUtils {
    @JvmStatic
    fun toTraditional(content: String?): String {
        try {
            val jChineseConvertor = JChineseConvertor.getInstance()
            return jChineseConvertor.s2t(content)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return content ?: ""
    }

    @JvmStatic
    fun toSimplified(content: String?): String {
        try {
            val jChineseConvertor = JChineseConvertor.getInstance()
            return jChineseConvertor.t2s(content)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return content ?: ""
    }

}