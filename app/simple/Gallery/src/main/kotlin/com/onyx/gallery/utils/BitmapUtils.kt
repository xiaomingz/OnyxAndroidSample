package com.onyx.gallery.utils

import android.content.Context
import android.graphics.Bitmap
import com.onyx.android.sdk.utils.FileUtils
import com.onyx.android.sdk.utils.MtpUtils
import java.io.File

/**
 * Created by Leung on 2020/6/30
 */
object BitmapUtils {

    fun saveBitmapToFile(context: Context, path: String, bitmap: Bitmap) {
        FileUtils.deleteFile(path)
        val file = File(path)
        FileUtils.saveBitmapToFile(bitmap, file, Bitmap.CompressFormat.PNG, 100)
        MtpUtils.updateMtpDb(context, file)
    }
}