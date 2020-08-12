package com.onyx.gallery.utils;

import android.content.Context;

import com.onyx.android.sdk.data.model.ExtensionMap;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.File;

/**
 * Create at 2018/10/15 by anypwx
 **/
public class FormatRootFilePathUtils {
    public static String formatPath(File f, Context context) {
        if(f == null) {
            return "";
        }
       return FileUtils.formatFileAbsolutePath(f, ExtensionMap.getRootDirectoryAliasName(FileUtils.getRootDirectory(f)));
    }

    public static String formatPath(String path, Context context) {
        if (path == null) {
            return "";
        }
        return FileUtils.formatFileAbsolutePath(path, ExtensionMap.getRootDirectoryAliasName(path));
    }
}
