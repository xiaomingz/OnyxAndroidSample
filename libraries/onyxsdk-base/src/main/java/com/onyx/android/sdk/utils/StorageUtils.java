package com.onyx.android.sdk.utils;

import android.os.Build;
import android.os.StatFs;
import android.util.Log;

import com.onyx.android.sdk.device.Device;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StorageUtils {

    private static final String TAG = StorageUtils.class.getSimpleName();
    /** storage of standard    unit: G  for example 8G , 32G **/
    public static final Long[] STANDARD_STORAGE = new Long[] {8L, 16L, 32L ,64L ,128L, 256L, 512L, 1024L};

    /**
     * @return total storage amount in bytes
     */
    public static long getTotalStorageAmount() {
        List<String> pathList = new ArrayList<>();
        pathList.add("/system");
        pathList.add("/data");
        pathList.add("/cache");
        pathList.add(Device.currentDevice().getExternalStorageDirectory().getAbsolutePath());
        return getStorageAmountForPartitions(pathList);
    }

    public static long getStorageAmountForPartitions(List<String> pathList) {
        int len = pathList.size();
        long total = 0;
        for (int i = 0; i < len; i++) {
            StatFs stat = new StatFs(pathList.get(i));
            long bytesAvailable;
            if (CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.JELLY_BEAN_MR2)) {
                bytesAvailable = stat.getBlockSizeLong() * stat.getBlockCountLong();
            } else {
                bytesAvailable = (long) stat.getBlockSize() * (long) stat.getBlockCount();
            }
            total += bytesAvailable;
        }
        return total;
    }

    public static long getInternalStorageAmount() {
        List<String> list = new ArrayList<>(1);
        list.add(Device.currentDevice().getExternalStorageDirectory().getAbsolutePath());
        return getStorageAmountForPartitions(list);
    }

    public static long getExtsdStorageAmount() {
        List<String> list = new ArrayList<>(1);
        list.add(Device.currentDevice().getRemovableSDCardDirectory().getAbsolutePath());
        return getStorageAmountForPartitions(list);
    }

    public static long convertBytesToMB(long bytes) {
        return bytes / (1024 * 1024);
    }

    /**
     * @return total storage amount in mega bytes (base on 1024)
     */
    public static long getTotalStorageAmountInMB() {
        return convertBytesToMB(getTotalStorageAmount());
    }

    /**
     * @return the amount with GB only for display that targeting user (base on 1000)
     */
    public static long getDisplayGBForUser(double bytes) {
        return Math.round(bytes / 1000 / 1000 / 1000);
    }

    public static long getSDCardFreeBytes() {
        return getFreeBytes(Device.currentDevice.getExternalStorageDirectory().getAbsolutePath());
    }

    public static long getSDCardFreeMB() {
        return getSDCardFreeBytes() / (1024 * 1024);
    }

    public static boolean isSDCardFreeMBEnough(long limitMB) {
        return getSDCardFreeMB() > limitMB;
    }

    public static boolean isSDCardFreeByteEnough(long allocBytes, long limitBytes) {
        return getSDCardFreeBytes() - allocBytes > limitBytes;
    }

    public static long getFreeBytes(String path) {
        long amount = 0;
        if (!new File(path).exists()) {
            return amount;
        }
        StatFs sdPath = new StatFs(path);
        if (CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.JELLY_BEAN_MR2)) {
            amount = sdPath.getAvailableBytes();
        } else {
            amount = (long) sdPath.getAvailableBlocks() * (long) sdPath.getBlockSize();
        }
        Log.i(TAG, "blocks: " + sdPath.getAvailableBlocks() + " |block size:" + sdPath.getBlockSize()
                + " |amount: " + amount / 1024 / 1024 + "MB");
        return amount;
    }

    public static long getExtsdFreeBytes() {
        return getFreeBytes(Device.currentDevice().getRemovableSDCardDirectory().getAbsolutePath());
    }

    /**
     * @return get the ratio of free storage amount
     */
    public static int getFreeInternalStorageRatio() {
        return calculateRatio(getInternalStorageAmount(), getSDCardFreeBytes());
    }

    public static int calculateRatio(long total, long free) {
        Log.i(TAG, "total: " + total / 1024 / 1024);
        Log.i(TAG, "free: " + free / 1024 / 1024);
        if (total > 0) {
            return (int) (100 * free / total);
        }
        return 100;
    }

    public static int getFreeExternalStorageRatio() {
        return calculateRatio(getExtsdStorageAmount(), getExtsdFreeBytes());
    }

    public static BigDecimal getFreeStorageInGB() {
        BigDecimal d = new BigDecimal((double) getSDCardFreeBytes() / (double) (1024 * 1024 * 1024));
        d = d.setScale(2, BigDecimal.ROUND_HALF_UP);
        return d;
    }
}
