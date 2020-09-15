package com.onyx.gallery.helpers

import android.content.Context
import android.os.Build
import com.alibaba.fastjson.JSON
import com.onyx.android.sdk.utils.Debug
import com.onyx.gallery.utils.RawResourceUtil

/**
 * Created by Leung 2020/9/15 15:46
 **/
class DeviceConfig private constructor(val context: Context) {
    val hasCamera = false
    val deviceConfigInfo by lazy { parseDeviceConfig() }

    companion object {
        private var instance: DeviceConfig? = null
        fun sharedInstance(context: Context): DeviceConfig {
            if (instance == null) {
                instance = DeviceConfig(context)
            }
            return instance!!
        }
    }

    private fun parseDeviceConfig(): DeviceConfigInfo {
        val content = readConfig(context);
        return JSON.parseObject(content, DeviceConfigInfo::class.java)
    }

    private fun readConfig(context: Context): String {
        return contentFromRawResource(context, Build.MODEL);
    }

    private fun contentFromRawResource(context: Context, name: String): String {
        var content: String = ""
        try {
            val res = context.resources.getIdentifier(name.toLowerCase(), "raw", context.packageName)
            content = RawResourceUtil.contentOfRawResource(context, res)
        } catch (e: Exception) {
            Debug.w(javaClass, e)
        }
        return content
    }

    fun isSupportHandwriting(): Boolean {
        if (deviceConfigInfo == null) {
            return false
        }
        return deviceConfigInfo.isSupportHandwriting
    }

}

class DeviceConfigInfo {
    var isSupportHandwriting: Boolean = false
}