package com.onyx.android.sdk.api.device.screensaver;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * Created by suicheng on 2017/6/5.
 */
public class ScreenSaverManager {
    public static int SCREEN_SAVER_COUNT_LIMIT = 3;

    private static ScreenSaverManager globalManager;
    private ScreenSaverConfig globalConfig;

    private ScreenSaverManager() {
    }

    public static ScreenSaverManager init(ScreenSaverConfig config) {
        globalManager = new ScreenSaverManager();
        globalManager.globalConfig = config;
        return globalManager;
    }

    public static ScreenSaverManager getInstance() {
        return globalManager;
    }

    public static ScreenSaverConfig getScreenSaverConfig() {
        return globalManager.globalConfig;
    }

    public static String getSourcePicPath(ScreenSaverConfig config, String targetFileName) {
        File file = new File(config.sourcePicPathString);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (file.isDirectory()) {
            return new File(config.sourcePicPathString, targetFileName).getAbsolutePath();
        } else if (file.isFile()) {
            if (TextUtils.isEmpty(targetFileName)) {
                return config.sourcePicPathString;
            }
            return new File(FileUtils.getParent(config.sourcePicPathString), targetFileName).getAbsolutePath();
        }
        return config.sourcePicPathString;
    }

    public static void setAllScreenSaver(final Context context, ScreenSaverConfig config) {
        for (int i = config.screenSaverInitialNumber; i < config.screenSaverInitialNumber + SCREEN_SAVER_COUNT_LIMIT; i++) {
            ScreenSaverConfig newConfig = config.copy();
            newConfig.targetPicPathString = newConfig.createTargetPicPath(i);
            saveScreenFile(context, newConfig);
        }
    }

    private static void saveScreenFile(final Context context, ScreenSaverConfig config) {
        int fullScreenPhysicalHeight = config.fullScreenPhysicalHeight;
        int fullScreenPhysicalWidth = config.fullScreenPhysicalWidth;
        Bitmap temp = BitmapFactory.decodeFile(config.sourcePicPathString);
        if (temp.getHeight() > temp.getWidth()) {
            temp = BitmapUtils.rotateBmp(temp, config.picRotateDegrees);
        }
        if ((temp.getWidth() != fullScreenPhysicalHeight) ||
                temp.getHeight() != fullScreenPhysicalWidth) {
            temp = Bitmap.createScaledBitmap(temp, fullScreenPhysicalHeight, fullScreenPhysicalWidth, true);
        }
        if (config.convertToGrayScale) {
            temp = BitmapUtils.convertToBlackWhite(BitmapUtils.fillColorAsBackground(temp, Color.WHITE));
        }
        boolean success = false;
        if (config.targetFormat.contains("bmp")) {
            success = BitmapUtils.saveBitmapToFile(temp, config.targetDir, config.targetPicPathString, true);
        } else if (config.targetFormat.contains("png")) {
            success = BitmapUtils.savePngToFile(temp, config.targetDir, config.targetPicPathString, true);
        }
        if (success) {
            Log.i("screenSaver", "success");
            Intent intent = new Intent("update_standby_pic");
            context.sendBroadcast(intent);
        }
    }
}
