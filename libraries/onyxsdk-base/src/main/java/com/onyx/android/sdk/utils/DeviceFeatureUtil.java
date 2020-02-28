package com.onyx.android.sdk.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.input.InputManager;
import android.os.Build;

import com.onyx.android.sdk.device.Device;

import java.util.Arrays;
import java.util.List;

import static android.content.Context.INPUT_SERVICE;

/**
 * <pre>
 *     author : liao lin tao
 *     time   : 2018/7/19 11:01
 *     desc   :
 *     TODO:current solution still use device method, wait fw refactor to override system feature config.
 * </pre>
 */
public class DeviceFeatureUtil {
    private static final boolean USE_SYSTEM_FEATURE_DETECT = false;
    private static final boolean USE_SYSTEM_WIFI_SETTING_DIALOG = true;
    private static final List<String> STYLUS_INPUT_DEVICE_NAME_LIST = Arrays.asList("onyx_emp", "Wacom I2C Digitizer", "hanvon_tp");

    // TODO: 2016/12/16 api for 4.0-4.4?
    // TODO: 2018/5/8 add device.currentDevice.hasAudio() method
    public static boolean hasAudio(Context context) {
        return USE_SYSTEM_FEATURE_DETECT ? context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUDIO_OUTPUT) : Device.currentDevice().hasAudio(context);
    }

    public static boolean hasWifi(Context context) {
        return USE_SYSTEM_FEATURE_DETECT ? context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI) : Device.currentDevice().hasWifi(context);
    }

    public static boolean hasBluetooth(Context context) {
        return USE_SYSTEM_FEATURE_DETECT ? context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH) : Device.currentDevice().hasBluetooth(context);
    }

    public static boolean hasTouch(Context context) {
        return USE_SYSTEM_FEATURE_DETECT ? context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN) : Device.currentDevice().isTouchable(context);
    }

    public static boolean hasFLBrightness(Context context) {
        return Device.currentDevice().hasFLBrightness(context);
    }

    public static boolean hasCTMBrightness(Context context) {
        return Device.currentDevice().hasCTMBrightness(context);
    }

    public static boolean hasFrontLight(Context context) {
        return hasFLBrightness(context) || hasCTMBrightness(context);
    }

    public static boolean hasStylus(Context context) {
        if (CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.JELLY_BEAN)) {
            InputManager inputManager = (InputManager) context.getSystemService(INPUT_SERVICE);
            if (inputManager != null) {
                for (int id : inputManager.getInputDeviceIds()) {
                    if (isStylus(inputManager.getInputDevice(id).getName())) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        } else {
            //TODO:api 15 only can use json config.
            return false;
        }
    }

    private static boolean isStylus(String deviceName) {
        for (String stylusName : STYLUS_INPUT_DEVICE_NAME_LIST) {
            if (deviceName.contains(stylusName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean supportExternalSD(Context context) {
        return Device.currentDevice().supportExternalSD(context);
    }

    public static boolean hasFingerprint(Context context) {
        if (CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.M)) {
            return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT);
        } else {
            return false;
        }
    }

    public static boolean useSystemWifiSettingDialog() {
        return USE_SYSTEM_WIFI_SETTING_DIALOG;
    }
}
