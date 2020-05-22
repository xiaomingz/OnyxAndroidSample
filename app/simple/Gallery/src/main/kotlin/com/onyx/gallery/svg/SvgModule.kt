package com.onyx.gallery.svg

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.PictureDrawable

import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.caverock.androidsvg.SVG
import com.onyx.gallery.helpers.DirectoryThumbLoader
import com.onyx.gallery.models.Directory

import java.io.InputStream

@GlideModule
class SvgModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.register(SVG::class.java, PictureDrawable::class.java, SvgDrawableTranscoder())
                .append(InputStream::class.java, SVG::class.java, SvgDecoder())
        
        registry.append(Directory::class.java, Bitmap::class.java, DirectoryThumbLoader.Factory())
    }

    override fun isManifestParsingEnabled() = false
}
