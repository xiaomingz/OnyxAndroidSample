/**
 * 
 */
package com.onyx.android.sdk.api.device;

import android.content.Context;

import com.onyx.android.sdk.device.BaseDevice;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.device.RK31XXDevice;
import com.onyx.android.sdk.device.RK32XXDevice;

import java.util.List;

/**
 * @author Joy
 *
 */
public class FrontLightController
{

    public static int getBrightnessMinimum(Context context)
    {
        return Device.currentDevice().getFrontLightBrightnessMinimum(context);
    }
    
    public static int getBrightnessMaximum(Context context)
    {
        return Device.currentDevice().getFrontLightBrightnessMaximum(context);
    }

    public static boolean turnOn(Context context)
    {
        return Device.currentDevice().openFrontLight(context);
    }
    public static boolean turnOff(Context context)
    {
        return Device.currentDevice().closeFrontLight(context);
    }

    public static boolean isLightOn(Context context)
    {
        if (Device.currentDevice() instanceof RK31XXDevice ||
                Device.currentDevice() instanceof RK32XXDevice) {
            return Device.currentDevice().isBrightnessOn(context);
        }
        BaseDevice dev = Device.currentDevice();
        return dev.getFrontLightDeviceValue(context) > dev.getFrontLightBrightnessMinimum(context);
    }

    public static List<Integer> getFrontLightValueList(Context context) {
        if (!hasFLBrightness(context)) {
            return null;
        }
        return Device.currentDevice().getFrontLightValueList(context);
    }

    public static int getMaxFrontLightValue(final Context context) {
        if (!hasFLBrightness(context)) {
            return -1;
        }
        List<Integer> frontLightValue = getFrontLightValueList(context);
        return frontLightValue.get(frontLightValue.size() - 1);
    }

    public static int getMinFrontLightValue(final Context context) {
        if (!hasFLBrightness(context)) {
            return -1;
        }
        List<Integer> frontLightValue = getFrontLightValueList(context);
        return frontLightValue.get(0);
    }

    /**
     * value is valid only when light is on
     * 
     * @param context
     * @return
     */
    public static int getBrightness(Context context)
    {
        if (!hasFLBrightness(context)) {
            return -1;
        }
        return Device.currentDevice().getFrontLightConfigValue(context);
    }
    
    /**
     * after set brightness, front light will be turned on simultaneously.
     * 
     * @param context
     * @param level
     * @return
     */
    public static boolean setBrightness(Context context, int level)
    {
        if (!hasFLBrightness(context)) {
            return false;
        }
        BaseDevice dev = Device.currentDevice();
        if (dev.setFrontLightDeviceValue(context, level)) {
            return dev.setFrontLightConfigValue(context, level);
        }
        
        return false;
    }

    public static boolean setNaturalBrightness(Context context, int level)
    {
        if (!hasCTMBrightness(context)) {
            return false;
        }
        BaseDevice dev = Device.currentDevice();
        if (dev.setNaturalLightConfigValue(context, level)) {

            return dev.setNaturalLightConfigValue(context, level);
        }

        return false;
    }

    public static boolean hasCTMBrightness(Context context) {
        return Device.currentDevice().hasCTMBrightness(context);
    }

    public static boolean hasFLBrightness(Context context) {
        return Device.currentDevice().hasFLBrightness(context);
    }

    public static Integer[] getWarmLightValues(Context context) {
        return Device.currentDevice().getWarmLightValues(context);
    }

    public static Integer[] getColdLightValues(Context context) {
        return Device.currentDevice().getColdLightValues(context);
    }

    public static int getWarmLightConfigValue(Context context) {
        return Device.currentDevice().getWarmLightConfigValue(context);
    }

    public static int getColdLightConfigValue(Context context) {
        return Device.currentDevice().getColdLightConfigValue(context);
    }

    public static boolean setWarmLightDeviceValue(Context context, int value) {
        return Device.currentDevice().setWarmLightDeviceValue(context, value);
    }

    public static boolean setColdLightDeviceValue(Context context, int value) {
        return Device.currentDevice().setColdLightDeviceValue(context, value);
    }

    /**
     * Gradually increase brightness.
     *
     * @param context
     * @param colorTemp  1 is warm color light and 2 is cool color light
     * @return
     */
    public static boolean increaseBrightness(Context context, int colorTemp) {
        return Device.currentDevice().increaseBrightness(context, colorTemp);
    }

    /**
     * Gradually decrease brightness.
     *
     * @param context
     * @param colorTemp   1 is warm color light and 2 is cool color light
     * @return
     */
    public static boolean decreaseBrightness(Context context, int colorTemp) {
        return Device.currentDevice().decreaseBrightness(context, colorTemp);
    }
 }
