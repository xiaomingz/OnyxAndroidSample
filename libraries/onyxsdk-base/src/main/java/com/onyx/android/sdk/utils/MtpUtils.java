package com.onyx.android.sdk.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.onyx.android.sdk.device.Device;

import java.io.File;

/**
 * <pre>
 *     author : liao lin tao
 *     time   : 2019/3/28 12:16
 *     desc   :
 * </pre>
 */
public class MtpUtils {
    private static final String ACTION_MEDIA_SCANNER_SERVICE = "android.media.IMediaScannerService";
    private static final String PKG_NAME_MEDIA_PROVIDER = "com.android.providers.media";
    private static final String CLS_NAME_MEDIA_SCANNER_SERVICE = "com.android.providers.media.MediaScannerService";
    private static final String KEY_VOLUME = "volume";
    private static final String VOLUME_EXTERNAL = "external";
    private static final String VOLUME_INTERNAL = "internal";

    public static boolean saveBitmapToFile(Context context , Bitmap bitmap, File file) {
        boolean saveBitmapToFile = FileUtils.saveBitmapToFile(bitmap, file, Bitmap.CompressFormat.PNG, 100);
        if (saveBitmapToFile) {
            updateMtpDb(context, file);
        }
        return saveBitmapToFile;
    }

    public static boolean appendContentToFile(Context context, String content, File fileForSave) {
        boolean appendContentToFile = FileUtils.appendContentToFile(content, fileForSave);
        if (appendContentToFile) {
            updateMtpDb(context, fileForSave);
        }
        return appendContentToFile;
    }

    public static boolean saveContentToFile(Context context, String content, File fileForSave) {
        boolean saveContentToFile = FileUtils.saveContentToFile(content, fileForSave);
        if (saveContentToFile) {
            updateMtpDb(context, fileForSave);
        }
        return saveContentToFile;
    }

    public static void updateMtpDb(Context context, File file) {
        Device.currentDevice.updateMtpDb(context, file);
    }

    public static void updateMtpDb(Context context, String filePath) {
        updateMtpDb(context, new File(filePath));
    }

    public static boolean deleteFile(Context context, String path) {
        return deleteFile(context, new File(path));
    }

    public static boolean deleteFile(Context context, File file) {
        boolean deleteFile = FileUtils.deleteFile(file);
        if (deleteFile) {
            updateMtpDb(context, file);
        }
        return deleteFile;
    }


    public static void scanExternalFiles(Context context) {
        scanVolumeFiles(context, VOLUME_EXTERNAL);
    }

    private static void scanVolumeFiles(Context context, String volume) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(PKG_NAME_MEDIA_PROVIDER, CLS_NAME_MEDIA_SCANNER_SERVICE));
        intent.putExtra(KEY_VOLUME, volume);
        context.startService(intent);
    }
}
