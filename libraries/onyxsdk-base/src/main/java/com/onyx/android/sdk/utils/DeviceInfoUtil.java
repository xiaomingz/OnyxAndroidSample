package com.onyx.android.sdk.utils;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.device.IMX6Device;
import com.onyx.android.sdk.device.RK3026Device;
import com.onyx.android.sdk.device.RK31XXDevice;
import com.onyx.android.sdk.device.RK32XXDevice;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by suicheng on 2017/3/10.
 */
public class DeviceInfoUtil {
    private static final String FILENAME_PROC_VERSION = "/proc/version";
    private static final String SPLIT_FLAG = "-";
    private static final String TAG = DeviceInfoUtil.class.getSimpleName();
    private static final String UNKNOWN = "unknown";

    public static File getExternalStorageDirectory() {
        return Device.currentDevice.getExternalStorageDirectory();
    }

    public static File getRemovableSDCardDirectory() {
        return Device.currentDevice.getRemovableSDCardDirectory();
    }

    public static Point getScreenResolution(final Context context) {
        WindowManager w = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;

        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
                heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Exception ignored) {
            }
        }
        if (Build.VERSION.SDK_INT >= 17) {
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                widthPixels = realSize.x;
                heightPixels = realSize.y;
            } catch (Exception ignored) {
            }
        }
        return new Point(widthPixels, heightPixels);
    }

    public static String deviceInfo() {
        String s = "";
        s += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
        s += "\n OS API Level: " + Build.VERSION.SDK_INT;
        s += "\n Device: " + android.os.Build.DEVICE;
        s += "\n Model (and Product): " + android.os.Build.MODEL + " (" + android.os.Build.PRODUCT + ")";
        return s;
    }

    public static String getDeviceKernelInfo() {
        try {
            return formatKernelVersion(StringUtils.readLine(FILENAME_PROC_VERSION));
        } catch (IOException e) {
            Log.e(TAG, "IO Exception when getting kernel version for Device Info screen", e);
            return "Unavailable";
        }
    }

    private static String formatKernelVersion(String rawKernelVersion) {
        // Example (see tests for more):
        // Linux version 3.0.31-g6fb96c9 (android-build@xxx.xxx.xxx.xxx.com) \
        //     (gcc version 4.6.x-xxx 20120106 (prerelease) (GCC) ) #1 SMP PREEMPT \
        //     Thu Jun 28 11:02:39 PDT 2012

        final String PROC_VERSION_REGEX =
                "Linux version (\\S+) " + /* group 1: "3.0.31-g6fb96c9" */
                        "\\((\\S+?)\\) " +        /* group 2: "x@y.com" (kernel builder) */
                        "(?:\\(gcc.+? \\)) " +    /* ignore: GCC version information */
                        "(#\\d+) " +              /* group 3: "#1" */
                        "(?:.*?)?" +              /* ignore: optional SMP, PREEMPT, and any CONFIG_FLAGS */
                        "((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)"; /* group 4: "Thu Jun 28 11:02:39 PDT 2012" */

        Matcher m = Pattern.compile(PROC_VERSION_REGEX).matcher(rawKernelVersion);
        if (!m.matches()) {
            Log.e(TAG, "Regex did not match on /proc/version: " + rawKernelVersion);
            return "Unavailable";
        } else if (m.groupCount() < 4) {
            Log.e(TAG, "Regex match on /proc/version only returned " + m.groupCount()
                    + " groups");
            return "Unavailable";
        }
        return m.group(1) + "\n" +                 // 3.0.31-g6fb96c9
                m.group(2) + " " + m.group(3) + "\n" + // x@y.com #1
                m.group(4);                            // Thu Jun 28 11:02:39 PDT 2012
    }

    static public String getEMTPInfo() {
       String emtpInfo = FileUtils.readContentOfFile("/sys/onyx_misc/stylus_fwver");
       emtpInfo = getSplitStr(emtpInfo);
       if (StringUtils.isNullOrEmpty(emtpInfo) || emtpInfo.equals(UNKNOWN)) {
           emtpInfo = OnyxSystemProperties.get("sys.onyx.emtp", "");
       }
        return emtpInfo;
    }

    private static String getSplitStr(String emtpInfo) {
        String[] splitStr;
        if (StringUtils.isNotBlank(emtpInfo) && emtpInfo.contains(SPLIT_FLAG)) {
            splitStr = emtpInfo.split(SPLIT_FLAG);
            return splitStr[splitStr.length - 1];
        }
        return emtpInfo;
    }

    static public String getVComInfo(Context context) {
        String vcomInfo = null;
        int vcom = Device.currentDevice.getVCom(context, getVComEndPoint());
        if (vcom != -1) {
            vcomInfo = (double)vcom / 100 + " V";
        }
        return vcomInfo;
    }

    static private String getVComEndPoint() {
        if (Device.currentDevice() instanceof RK3026Device) {
            return "/sys/devices/platform/onyx_misc.0/vcom_value";
        } else if (Device.currentDevice() instanceof IMX6Device) {
            return "/sys/class/hwmon/hwmon0/device/vcom_value";
        } else if (Device.currentDevice() instanceof RK32XXDevice) {
            return "/sys/class/hwmon/hwmon1/device/vcom_value";
        } else if (Device.currentDevice() instanceof RK31XXDevice) {
            return "/sys/class/hwmon/hwmon0/device/vcom_value";
        }
        return null;
    }
}
