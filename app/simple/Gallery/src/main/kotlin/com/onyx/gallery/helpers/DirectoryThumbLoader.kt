package com.onyx.gallery.helpers

import android.graphics.Bitmap
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import com.onyx.gallery.extensions.getImageRequestBuilder
import com.onyx.gallery.models.Directory
import com.simplemobiletools.commons.extensions.isImageFast
import java.io.File

/**
 * <pre>
 *     author : suicheng
 *     time   : 2020/5/12 17:18
 *     desc   :
 * </pre>
 */
class DirectoryThumbLoader() : ModelLoader<Directory, Bitmap> {

    override fun buildLoadData(model: Directory, width: Int, height: Int, options: Options): ModelLoader.LoadData<Bitmap>? {
        return ModelLoader.LoadData(ObjectKey(model), FileFetcher(File(model.path)))
    }

    private class FileFetcher(val file: File) : DataFetcher<Bitmap> {
        override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in Bitmap>) {
            val data: Bitmap?
            try {
                val files = file.listFiles { f: File -> f.isFile && f.absolutePath.isImageFast() }
                val bitmapList = arrayListOf<Bitmap>()
                for (i in 0 until Math.min(files?.size ?: 0, 4)) {
                    val bitmap = loadImageBitmap(files[i])
                    if (bitmap != null) {
                        bitmapList.add(bitmap)
                    }
                }
                data = CombineBitmapTools.combineBitmap(CombineBitmapTools.Companion.CombineConfig.DEFAULT,
                        bitmapList)
            } catch (e: Exception) {
                callback.onLoadFailed(e)
                return
            }
            callback.onDataReady(data)
        }

        override fun cleanup() {
        }

        override fun cancel() {
        }

        override fun getDataClass(): Class<Bitmap> {
            return Bitmap::class.java
        }

        override fun getDataSource(): DataSource {
            return DataSource.LOCAL
        }

        private fun loadImageBitmap(file: File): Bitmap? {
            try {
                return AppContext.getContext().getImageRequestBuilder(file.absolutePath).apply {
                    first.fitCenter().priority(Priority.HIGH)
                    second.apply(first)
                }
                        .second.load(file.absolutePath).submit().get()
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }

    override fun handles(model: Directory): Boolean {
        return true
    }

    class Factory : ModelLoaderFactory<Directory, Bitmap> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<Directory, Bitmap> {
            return DirectoryThumbLoader()
        }

        override fun teardown() {
        }
    }
}