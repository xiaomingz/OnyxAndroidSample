package com.onyx.android.sdk.device;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import com.onyx.android.sdk.api.device.epd.EPDMode;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.api.device.epd.UpdatePolicy;
import com.onyx.android.sdk.api.device.epd.UpdateScheme;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.ReflectUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wangxu on 17-4-6.
 */

public class RK33XXDevice extends BaseDevice {
    private static final String TAG = RK33XXDevice.class.getSimpleName();

    private static RK33XXDevice sInstance = null;

    private static int sPolicyAutomatic = 0;
    private static int sPolicyGUIntervally = 0;

    private static int sModeDW = 0;
    private static int sModeGU = 0;
    private static int sModeGC = 0;
    private static int sModeAnimation = 0;
    private static int sModeAnimationQuality = 0;
    private static int sModeGC4 = 0;
    private static int sModeRegal = 0;
    private static int sModeRegalD = 0;
    private static int sModeDefault = 0;

    private static final int sSchemeSNAPSHOT = 0;
    private static final int sSchemeQUEUE = 1;
    private static final int sSchemeQUEUE_AND_MERGE = 2;


    private static final String mColdLightFile = "/sys/class/backlight/white/brightness";
    private static final String mWarmLightFile = "/sys/class/backlight/warm/brightness";

    /**
     * View.refreshScreen(int updateMode)
     */
    private static Method sMethodRefreshScreen = null;
    private static Method sMethodRefreshScreenRegion = null;
    private static Method sMethodScreenshot = null;
    private static Method sMethodSupportRegal = null;
    private static Method sMethodEnableRegal = null;

    private static Method sMethodMoveTo = null;
    private static Method sMethodMoveToView = null;
    private static Method sMethodSetStrokeColor = null;
    private static Method sMethodSetStrokeStyle = null;
    private static Method sMethodSetStrokeWidth = null;
    private static Method sMethodSetPainterStyle = null;
    private static Method sMethodLineTo = null;
    private static Method sMethodLineToView = null;
    private static Method sMethodQuadTo = null;
    private static Method sMethodQuadToView = null;
    private static Method sMethodGetTouchWidth = null;
    private static Method sMethodGetTouchHeight = null;
    private static Method sMethodGetMaxTouchPressure = null;
    private static Method sMethodGetEpdWidth = null;
    private static Method sMethodGetEpdHeight = null;
    private static Method sMethodMapToView = null;
    private static Method sMethodMapToEpd = null;
    private static Method sMethodMapFromRawTouchPoint = null;
    private static Method sMethodMapToRawTouchPoint = null;
    private static Method sMethodEnablePost = null;
    private static Method sMethodResetEpdPost = null;
    private static Method sMethodIsValidPenState = null;
    private static Method sMethodGetPenState = null;
    private static Method sMethodSetScreenHandWritingPenState = null;
    private static Method sMethodSetScreenHandWritingRegionMode = null;
    private static Method sMethodSetScreenHandWritingRegionLimit = null;
    private static Method sMethodSetScreenHandWritingRegionExclude = null;
    private static Method sMethodApplyGammaCorrection = null;
    private static Method sMethodApplyColorFilter = null;
    private static Method sMethodStartStroke = null;
    private static Method sMethodAddStrokePoint = null;
    private static Method sMethodFinishStroke = null;
    private static Method sMethodSetUpdListSize = null;
    private static Method sMethodInSystemFastMode = null;
    private static Method sMethodByPass = null;

    private static Method sMethodEnableA2;
    private static Method sMethodDisableA2;
    private static Method sMethodGetStorageRootDirectory;
    private static Method sMethodGetRemovableSDCardDirectory;

    /**
     * View.postInvalidate(int updateMode)
     */
    private static Method sMethodPostInvalidate = null;
    /**
     * View.invalidate(int updateMode)
     */
    private static Method sMethodInvalidate = null;
    private static Method sMethodInvalidateRect = null;


    /**
     * View.getDefaultUpdateMode()
     */
    private static Method sMethodGetViewDefaultUpdateMode = null;

    /**
     * View.resetUpdateMode()
     */
    private static Method sMethodResetViewUpdateMode = null;

    /**
     * View.setDefaultUpdateMode(int updateMode)
     */
    private static Method sMethodSetViewDefaultUpdateMode = null;

    /**
     * View.getGlobalUpdateMode()
     */
    private static Method sMethodGetSystemDefaultUpdateMode = null;

    /**
     * View.setGlobalUpdateMode(int updateMode)
     */
    private static Method sMethodSetSystemDefaultUpdateMode = null;

    /**
     * View.setFirstDrawUpdateMode(int updateMode)
     */
    private static Method sMethodSetFirstDrawUpdateMode = null;

    /**
     * View.setWaveformAndScheme(int mode, int scheme)
     */
    private static Method sMethodSetSystemUpdateModeAndScheme = null;

    private static Method sMethodApplyApplicationFastMode = null;
    private static Method sMethodApplyApplicationFastModeWithRepeat = null;
    private static Method sMethodClearApplicationFastMode = null;

    /**
     * View.resetWaveformAndScheme()
     */
    private static Method sMethodClearSystemUpdateModeAndScheme = null;
    private static Method sMethodEnableScreenUpdate = null;
    private static Method sMethodSetDisplayScheme = null;
    private static Method sMethodWaitForUpdateFinished = null;

    /**
     * View.repaintEveryThing()
     */
    private static Method sMethodRepaintEveryThing = null;
    private static Method sMethodApplySystemFastMode= null;
    private static Method sMethodApplySFDebug = null;


    private static Method sMethodOpenFrontLight;
    private static Method sMethodCloseFrontLight;
    private static Method sMethodGetFrontLightValue;
    private static Method sMethodSetFrontLightValue;
    private static Method sMethodGetFrontLightConfigValue;
    private static Method sMethodSetFrontLightConfigValue;
    private static Method sMethodLed;
    private static Method sMethodSetLedColor;
    private static Method sMethodSetVCom;
    private static Method sMethodGetVCom;
    private static Method sMethodSetWaveform;
    private static Method sMethodReadSystemConfig;
    private static Method sMethodSaveSystemConfig;
    private static Method sMethodUpdateMetadataDB;
    private static Method sMethodGotoSleep;

    private static Method sMethodUseBigPen;
    private static Method sMethodStopTpd;
    private static Method sMethodStartTpd;
    private static Method sMethodEnableTpd;

    private static Method sMethodSetQRShowConfig;
    private static Method sMethodSetInfoShowConfig;

    private static Method sMethodPowerCTP;
    private static Method sMethodPowerEMTP;
    private static Method sMethodIsCTPPowerOn;
    private static Method sMethodIsEMTPPowerOn;
    private static Method sMethodSetPwm1Output;

    private static Method sMethodSetAppCTPDisableRegion;
    private static Method sMethodAppResetCTPRegion;
    private static Method sMethodIsCTPRegion;

    private static Method sMethodWebViewSetCssInjectEnabled;
    private static Method sMethodApplyGcOnce;

    private static Method sMethodSwitchToA2Mode;
    private static Method sMethodRemoveAppConfig;
    private static Method sMethodSetAppConfig;
    private static Method sMethodSetEACServiceConfig;
    private static Method sMethodForceStopApp;

    private static Method sMethodIsTouchable;
    private static Method sMethodGetTouchType;
    private static Method sMethodHasWifi;
    private static Method sMethodHasAudio;
    private static Method sMethodHasTTS;
    private static Method sMethodHasFLBrightness;
    private static Method sMethodHasBluetooth;
    private static Method sMethodHasCTMBrightness;
    private static Method sMethodSupportExternalSD;

    private static Method sMethodSetTrigger;

    private static Method sMethodGetIPAddress;
    private static Method sMethodIsPowerSavedMode;
    private static Method sMethodEnablePowerSavedMode;
    private static Method sMethodIsHallControlEnable;
    private static Method sMethodEnableHallControl;

    private static Method sMethodFreezeApp;
    private static Method sMethodUnFreezeApp;
    private static Method sMethodFreezeGooglePlay;
    private static Method sMethodUnFreezeGooglePlay;
    private static Method sMethodIsGooglePlayEnabled;
    private static Method sMethodIsGoogleAppsExists;

    private static Method sMethodWriteValueToFile;

    private static Method sMethodLoadCACertificate;
    private static Method sMethodLoadUserCertificate;

    private static Method sMethodIsEMTPDisabled;
    private static Method sMethodIsKeyboardDisabled;
    private static Method sMethodSetEMTPDisabled;
    private static Method sMethodSetKeyboardDisabled;

    private static Method sMethodSetColdLightDeviceValue;
    private static Method sMethodSetWarmLightDeviceValue;
    private static Method sMethodGetBrightnessConfig;
    private static Method sMethodGetNaturalLightValues;
    private static Method sMethodIncreaseBrightness;
    private static Method sMethodDecreaseBrightness;
    private static Method sMethodIsBrightnessOn;

    private static Method sMethodGetCurrentTopComponent;
    private static Method sMethodGetEACAppConfigStringByPkgName;

    private RK33XXDevice() {
    }

    @Override
    public File getStorageRootDirectory() {
        return (File) ReflectUtil.invokeMethodSafely(sMethodGetStorageRootDirectory, null);
    }

    @Override
    public File getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory();
    }

    @Override
    public File getRemovableSDCardDirectory() {
        // if system has an emulated SD card(/mnt/sdcard) provided by device's NAND flash,
        // then real SD card will be mounted as a child directory(/mnt/sdcard/extsd) in it, which names "extsd" here
        //final String SDCARD_MOUNTED_FOLDER = "extsd";
        return (File) ReflectUtil.invokeMethodSafely(sMethodGetRemovableSDCardDirectory, null);
    }

    @Override
    public boolean isFileOnRemovableSDCard(File file) {
        return file.getAbsolutePath().startsWith(getRemovableSDCardDirectory().getAbsolutePath());
    }

    @Override
    public EPDMode getEpdMode() {
        return EPDMode.AUTO;
    }

    @Override
    public boolean setEpdMode(Context context, EPDMode mode) {
        setSystemUpdateModeAndScheme(getEpdMode(mode), UpdateScheme.QUEUE_AND_MERGE, Integer.MAX_VALUE);
        return false;
    }

    @Override
    public void invalidate(View view, UpdateMode mode) {
        int dst_mode_value = getUpdateMode(mode);

        try {
            assert (sMethodInvalidate != null);
            sMethodInvalidate.invoke(view, dst_mode_value);
            return;
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
    }

    @Override
    public void invalidate(View view, int left, int top, int right, int bottom, UpdateMode mode) {
        int dst_mode_value = getUpdateMode(mode);

        try {
            assert (sMethodInvalidateRect != null);
            sMethodInvalidateRect.invoke(view, left, top, right, bottom, dst_mode_value);
            return;
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
    }

    @Override
    public void postInvalidate(View view, UpdateMode mode) {
        int dst_mode_value = getUpdateMode(mode);

        try {
            assert (sMethodPostInvalidate != null);
            Log.d(TAG, "dst mode: " + dst_mode_value);
            sMethodPostInvalidate.invoke(view, dst_mode_value);
            return;
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
    }

    @Override
    public void refreshScreen(View view, UpdateMode mode) {
        int dst_mode_value = getUpdateMode(mode);
        try {
            assert (sMethodRefreshScreen != null);
            sMethodRefreshScreen.invoke(view, dst_mode_value);
            return;
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
    }

    @Override
    public void byPass(int count) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodByPass, null, count);
        } catch (Exception e) {
        }
    }

    public void refreshScreenRegion(View view, int left, int top, int width, int height, UpdateMode mode) {
        int dst_mode_value = getUpdateMode(mode);
        try {
            assert (sMethodRefreshScreenRegion != null);
            sMethodRefreshScreenRegion.invoke(view, left, top, width, height, dst_mode_value);
            return;
        } catch (Exception e) {
        }
    }

    public void screenshot(View view, int rotation, final String path) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodScreenshot, view, rotation, path);
            return;
        } catch (Exception e) {
        }
    }

    @Override
    public void setStrokeColor(int color) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodSetStrokeColor, null, color);
        } catch (Exception e) {
        }
    }

    public void setStrokeStyle(int style) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodSetStrokeStyle, null, style);
        } catch (Exception e) {
        }
    }

    public void setPainterStyle(boolean antiAlias, Paint.Style strokeStyle, Paint.Join joinStyle, Paint.Cap capStyle) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodSetPainterStyle, null, antiAlias, strokeStyle, joinStyle, capStyle);
        } catch (Exception e) {
        }
    }

    public void setStrokeWidth(float width) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodSetStrokeWidth, null, width);
        } catch (Exception e) {
        }
    }

    public void moveTo(float x, float y, float width) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodMoveTo, null, x, y, width);
        } catch (Exception e) {
        }
    }

    public void moveTo(View view, float x, float y, float width) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodMoveToView, null, view, x, y, width);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean supportDFB() {
        return (sMethodLineTo != null);
    }

    public boolean supportRegal() {
        if (sMethodSupportRegal == null) {
            return false;
        }

        Boolean value = (Boolean)ReflectUtil.invokeMethodSafely(sMethodSupportRegal, null);
        if (value == null) {
            return false;
        }
        return value.booleanValue();
    }

    @Override
    public void enableRegal(boolean enable) {
        ReflectUtil.invokeMethodSafely(sMethodEnableRegal, null, enable);
    }

    public void lineTo(float x, float y, UpdateMode mode) {
        int value = getUpdateMode(mode);
        try {
            ReflectUtil.invokeMethodSafely(sMethodLineTo, null, x, y, value);
        } catch (Exception e) {
        }
    }

    public void lineTo(View view, float x, float y, UpdateMode mode) {
        int value = getUpdateMode(mode);
        try {
            ReflectUtil.invokeMethodSafely(sMethodLineToView, null, view, x, y, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void quadTo(float x, float y, UpdateMode mode) {
        int value = getUpdateMode(mode);
        try {
            ReflectUtil.invokeMethodSafely(sMethodQuadTo, null, x, y, value);
        } catch (Exception e) {
        }
    }

    public void quadTo(View view, float x, float y, UpdateMode mode) {
        int value = getUpdateMode(mode);
        try {
            ReflectUtil.invokeMethodSafely(sMethodQuadToView, null, view, x, y, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float getTouchWidth() {
        try {
            Float value = (Float)ReflectUtil.invokeMethodSafely(sMethodGetTouchWidth, null);
            return value.floatValue();
        } catch (Exception e) {
        }
        return 0;
    }

    @Override
    public float getEpdHeight() {
        try {
            Float value = (Float)ReflectUtil.invokeMethodSafely(sMethodGetEpdHeight, null);
            return value.floatValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public float getEpdWidth() {
        try {
            Float value = (Float)ReflectUtil.invokeMethodSafely(sMethodGetEpdWidth, null);
            return value.floatValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void mapToEpd(View view, float[] src, float[] dst) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodMapToEpd, null, view, src, dst);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mapFromRawTouchPoint(View view, float[] src, float[] dst) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodMapFromRawTouchPoint, null, view, src, dst);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mapToRawTouchPoint(View view, float[] src, float[] dst) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodMapToRawTouchPoint, null, view, src, dst);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void mapToView(View view, float[] src, float[] dst) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodMapToView, null, view, src, dst);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public float getTouchHeight() {
        try {
            Float value = (Float)ReflectUtil.invokeMethodSafely(sMethodGetTouchHeight, null);
            return value.floatValue();
        } catch (Exception e) {
        }
        return 0;
    }

    public float getMaxTouchPressure() {
        try {
            Float value = (Float)ReflectUtil.invokeMethodSafely(sMethodGetMaxTouchPressure, null);
            return value.floatValue();
        } catch (Exception e) {
        }
        return 2048.0f;
    }

    public float startStroke(float baseWidth, float x, float y, float pressure, float size, float time) {
        try {
            Float value = (Float) ReflectUtil.invokeMethodSafely(sMethodStartStroke, null, baseWidth, x, y, pressure, size, time);
            return value.floatValue();
        } catch (Exception e) {
        }
        return baseWidth;
    }

    public float addStrokePoint(float baseWidth, float x, float y, float pressure, float size, float time) {
        try {
            Float value = (Float) ReflectUtil.invokeMethodSafely(sMethodAddStrokePoint, null, baseWidth, x, y, pressure, size, time);
            return value.floatValue();
        } catch (Exception e) {
        }
        return baseWidth;
    }

    public float finishStroke(float baseWidth, float x, float y, float pressure, float size, float time) {
        try {
            Float value = (Float) ReflectUtil.invokeMethodSafely(sMethodFinishStroke, null, baseWidth, x, y, pressure, size, time);
            return value.floatValue();
        } catch (Exception e) {
        }
        return baseWidth;
    }

    public void enterScribbleMode(View view) {
        enablePost(view, 0);
    }

    public void leaveScribbleMode(View view) {
        enablePost(view, 1);
    }

    public void enablePost(View view, int enable) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodEnablePost, view, enable);
        } catch (Exception e) {
        }
    }

    public void enablePost(int enable) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodEnablePost, null, enable);
        } catch (Exception e) {
        }
    }

    public void enterScribbleMode() {
        enablePost(0);
    }

    public void leaveScribbleMode() {
        enablePost(1);
    }

    @Override
    public void resetEpdPost() {
        try {
            ReflectUtil.invokeMethodSafely(sMethodResetEpdPost, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isValidPenState() {
        try {
            Boolean valid = (Boolean) ReflectUtil.invokeMethodSafely(sMethodIsValidPenState, null);
            return valid;
        } catch (Exception e) {

        }
        return false;
    }

    @Override
    public int getPenState() {
        try {
            Integer value = (Integer) ReflectUtil.invokeMethodSafely(sMethodGetPenState, null);
            return value;
        } catch (Exception e) {}
        return 0;
    }

    public boolean supportScreenHandWriting() {
        return (sMethodSetScreenHandWritingPenState != null);
    }

    public void setScreenHandWritingPenState(View view, int penState) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodSetScreenHandWritingPenState, view, penState);
        } catch (Exception e) {
        }
    }

    public void setScreenHandWritingRegionLimit(View view) {
        if (view == null) {
            return;
        }
        setScreenHandWritingRegionLimit(view, 0, 0, view.getRight(), view.getBottom());
    }

    public void setScreenHandWritingRegionLimit(View view, int left, int top, int right, int bottom) {
        setScreenHandWritingRegionLimit(view, new int[] { left, top, right, bottom });
    }

    public void setScreenHandWritingRegionLimit(View view, int[] array) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodSetScreenHandWritingRegionLimit, view, view, array);
        } catch (Exception e) {
        }
    }

    public void setScreenHandWritingRegionMode(View view, int mode) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodSetScreenHandWritingRegionMode, view, mode);
        } catch (Exception e) {
        }
    }

    @Override
    public void setScreenHandWritingRegionLimit(View view, Rect[] regions) {
        int array[] = new int[regions.length * 4];
        for (int i = 0; i < regions.length; i++) {
            Rect region = regions[i];

            int left = Math.min(region.left, region.right);
            int top = Math.min(region.top, region.bottom);
            int right = Math.max(region.left, region.right);
            int bottom = Math.max(region.top, region.bottom);

            array[4 * i] = left;
            array[4 * i + 1] = top;
            array[4 * i + 2] = right;
            array[4 * i + 3] = bottom;
        }
        setScreenHandWritingRegionLimit(view, array);
    }

    @Override
    public void setScreenHandWritingRegionExclude(View view, int[] array) {
        try {
            ReflectUtil.invokeMethodSafely(sMethodSetScreenHandWritingRegionExclude, view, view, array);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setScreenHandWritingRegionExclude(View view, Rect[] regions) {
        int array[] = new int[regions.length * 4];
        for (int i = 0; i < regions.length; i++) {
            Rect region = regions[i];
            int left = Math.min(region.left, region.right);
            int top = Math.min(region.top, region.bottom);
            int right = Math.max(region.left, region.right);
            int bottom = Math.max(region.top, region.bottom);
            array[4 * i] = left;
            array[4 * i + 1] = top;
            array[4 * i + 2] = right;
            array[4 * i + 3] = bottom;
        }
        setScreenHandWritingRegionExclude(view, array);
    }

    @Override
    public boolean enableScreenUpdate(View view, boolean enable) {
        try {
            sMethodEnableScreenUpdate.invoke(view, enable);
        } catch (Exception exception) {
        }
        return true;
    }

    public boolean setDisplayScheme(int scheme) {
        ReflectUtil.invokeMethodSafely(sMethodSetDisplayScheme, null, scheme);
        return true;
    }

    public void waitForUpdateFinished() {
        ReflectUtil.invokeMethodSafely(sMethodWaitForUpdateFinished, null);
    }

    public static RK33XXDevice createDevice() {
        if (sInstance == null) {
            sInstance = new RK33XXDevice();

            Class<View> cls = View.class;

            Class<?> viewUpdateHelperClass = ReflectUtil.classForName("android.onyx.ViewUpdateHelper");
            int value_policy_automic = ReflectUtil.getStaticIntFieldSafely(viewUpdateHelperClass, "EINK_ONYX_AUTO_MASK");
            int value_policy_gu_intervally = ReflectUtil.getStaticIntFieldSafely(viewUpdateHelperClass, "EINK_ONYX_GC_MASK");
            int value_mode_regional = ReflectUtil.getStaticIntFieldSafely(viewUpdateHelperClass, "EINK_AUTO_MODE_REGIONAL");
            int value_mode_nowait = ReflectUtil.getStaticIntFieldSafely(viewUpdateHelperClass, "EINK_WAIT_MODE_NOWAIT");
            int value_mode_wait = ReflectUtil.getStaticIntFieldSafely(viewUpdateHelperClass, "EINK_WAIT_MODE_WAIT");
            int value_mode_waveform_du = ReflectUtil.getStaticIntFieldSafely(viewUpdateHelperClass, "EINK_WAVEFORM_MODE_DU");
            int value_mode_waveform_animation = ReflectUtil.getStaticIntFieldSafely(viewUpdateHelperClass, "EINK_WAVEFORM_MODE_ANIM");
            int value_mode_waveform_gc4 = ReflectUtil.getStaticIntFieldSafely(viewUpdateHelperClass, "EINK_WAVEFORM_MODE_GC4");
            int value_mode_waveform_gc16 = ReflectUtil.getStaticIntFieldSafely(viewUpdateHelperClass, "EINK_WAVEFORM_MODE_GC16");
            int value_mode_waveform_regal = ReflectUtil.getStaticIntFieldSafely(viewUpdateHelperClass, "EINK_WAVEFORM_MODE_REAGL");
            int value_mode_waveform_regalD = ReflectUtil.getStaticIntFieldSafely(viewUpdateHelperClass, "EINK_REAGL_MODE_REAGLD");
            int value_mode_update_partial = ReflectUtil.getStaticIntFieldSafely(viewUpdateHelperClass, "EINK_UPDATE_MODE_PARTIAL");
            int value_mode_update_full = ReflectUtil.getStaticIntFieldSafely(viewUpdateHelperClass, "EINK_UPDATE_MODE_FULL");
            int value_mode_default = ReflectUtil.getStaticIntFieldSafely(viewUpdateHelperClass, "UI_DEFAULT_MODE");

            sPolicyAutomatic = value_policy_automic;
            sPolicyGUIntervally = value_policy_gu_intervally;

            sModeDW = value_mode_regional | value_mode_nowait | value_mode_waveform_du | value_mode_update_partial;
            sModeGU = value_mode_regional | value_mode_nowait | value_mode_waveform_gc16 | value_mode_update_partial;
            sModeGC = value_mode_regional | value_mode_wait | value_mode_waveform_gc16 | value_mode_update_full;
            sModeAnimation = value_mode_regional | value_mode_nowait | value_mode_waveform_animation | value_mode_update_partial;
            sModeAnimationQuality = ReflectUtil.getStaticIntFieldSafely(viewUpdateHelperClass, "UI_A2_QUALITY_MODE");
            sModeGC4 = value_mode_regional | value_mode_nowait | value_mode_waveform_gc4 | value_mode_update_partial;
            sModeRegal = value_mode_regional | value_mode_nowait | value_mode_waveform_regal | value_mode_update_partial;
            sModeRegalD = value_mode_regional | value_mode_nowait | value_mode_waveform_regalD | value_mode_waveform_regal | value_mode_update_partial;
            sModeDefault = value_mode_default;

            Class<?> deviceControllerClass = ReflectUtil.classForName("android.onyx.hardware.DeviceController");

            // new added methods, separating for compatibility
            sMethodOpenFrontLight = ReflectUtil.getMethodSafely(deviceControllerClass, "openFrontLight", Context.class);
            sMethodCloseFrontLight = ReflectUtil.getMethodSafely(deviceControllerClass, "closeFrontLight", Context.class);
            sMethodGetFrontLightValue = ReflectUtil.getMethodSafely(deviceControllerClass, "getFrontLightValue", Context.class);
            sMethodSetFrontLightValue = ReflectUtil.getMethodSafely(deviceControllerClass, "setFrontLightValue", Context.class, int.class);
            sMethodGetFrontLightConfigValue = ReflectUtil.getMethodSafely(deviceControllerClass, "getFrontLightConfigValue", Context.class);
            sMethodSetFrontLightConfigValue = ReflectUtil.getMethodSafely(deviceControllerClass, "setFrontLightConfigValue", Context.class, int.class);
            sMethodUseBigPen = ReflectUtil.getMethodSafely(deviceControllerClass, "useBigPen", boolean.class);
            sMethodStopTpd = ReflectUtil.getMethodSafely(deviceControllerClass, "stopTpd");
            sMethodStartTpd = ReflectUtil.getMethodSafely(deviceControllerClass, "startTpd");
            sMethodGotoSleep = ReflectUtil.getMethodSafely(deviceControllerClass, "gotoSleep", Context.class, long.class);

            sMethodIsTouchable = ReflectUtil.getMethodSafely(deviceControllerClass, "isTouchable", Context.class);
            sMethodGetTouchType = ReflectUtil.getMethodSafely(deviceControllerClass, "getTouchType", Context.class);
            sMethodHasWifi = ReflectUtil.getMethodSafely(deviceControllerClass, "hasWifi", Context.class);
            sMethodHasAudio = ReflectUtil.getMethodSafely(deviceControllerClass, "hasAudio", Context.class);
            sMethodHasTTS = ReflectUtil.getMethodSafely(deviceControllerClass, "hasTTS");
            sMethodHasFLBrightness = ReflectUtil.getMethodSafely(deviceControllerClass, "hasFLBrightness", Context.class);
            sMethodHasBluetooth = ReflectUtil.getMethodSafely(deviceControllerClass, "hasBluetooth", Context.class);
            sMethodHasCTMBrightness = ReflectUtil.getMethodSafely(deviceControllerClass, "hasCTMBrightness", Context.class);
            sMethodSupportExternalSD = ReflectUtil.getMethodSafely(deviceControllerClass, "supportExternalSD", Context.class);
            sMethodWriteValueToFile = ReflectUtil.getDeclaredMethodSafely(deviceControllerClass, "writeValueToFile", String.class, int.class);

            sMethodEnableTpd = ReflectUtil.getMethodSafely(cls, "enableOnyxTpd", int.class);

            sMethodLed = ReflectUtil.getMethodSafely(deviceControllerClass, "led", boolean.class);
            sMethodSetLedColor = ReflectUtil.getMethodSafely(deviceControllerClass, "setLedColor", String.class, int.class);

            // signature of "public void postInvalidate(int updateMode)"
            sMethodPostInvalidate = ReflectUtil.getMethodSafely(cls, "postInvalidate", int.class);
            // signature of "public void refreshScreen(int updateMode)"
            sMethodRefreshScreen = ReflectUtil.getMethodSafely(cls, "refreshScreen", int.class);
            sMethodRefreshScreenRegion = ReflectUtil.getMethodSafely(cls, "refreshScreen", int.class, int.class, int.class, int.class, int.class);
            sMethodScreenshot = ReflectUtil.getMethodSafely(cls, "screenshot", int.class, String.class);
            sMethodByPass = ReflectUtil.getMethodSafely(cls, "byPass", int.class);

            sMethodSetStrokeColor = ReflectUtil.getMethodSafely(cls, "setStrokeColor", int.class);
            sMethodSetStrokeStyle = ReflectUtil.getMethodSafely(cls, "setStrokeStyle", int.class);
            sMethodSetStrokeWidth = ReflectUtil.getMethodSafely(cls, "setLineWidth", float.class);
            sMethodSetPainterStyle = ReflectUtil.getMethodSafely(cls, "setPainterStyle", boolean.class, Paint.Style.class, Paint.Join.class, Paint.Cap.class);
            sMethodSupportRegal = ReflectUtil.getMethodSafely(cls, "supportRegal");
            sMethodEnableRegal = ReflectUtil.getMethodSafely(cls, "enableRegal", boolean.class);
            sMethodMoveTo = ReflectUtil.getMethodSafely(cls, "moveTo", float.class, float.class, float.class);
            sMethodLineTo = ReflectUtil.getMethodSafely(cls, "lineTo", float.class, float.class, int.class);
            sMethodQuadTo = ReflectUtil.getMethodSafely(cls, "quadTo", float.class, float.class, int.class);
            sMethodMoveToView = ReflectUtil.getMethodSafely(cls, "moveTo", View.class, float.class, float.class, float.class);
            sMethodLineToView = ReflectUtil.getMethodSafely(cls, "lineTo", View.class, float.class, float.class, int.class);
            sMethodQuadToView = ReflectUtil.getMethodSafely(cls, "quadTo", View.class, float.class, float.class, int.class);
            sMethodGetTouchWidth = ReflectUtil.getMethodSafely(cls, "getTouchWidth");
            sMethodGetTouchHeight = ReflectUtil.getMethodSafely(cls, "getTouchHeight");
            sMethodGetMaxTouchPressure = ReflectUtil.getMethodSafely(cls, "getMaxTouchPressure");
            sMethodGetEpdWidth = ReflectUtil.getMethodSafely(cls, "getEpdWidth");
            sMethodGetEpdHeight = ReflectUtil.getMethodSafely(cls, "getEpdHeight");
            sMethodMapToView = ReflectUtil.getMethodSafely(cls, "mapToView", View.class, float[].class, float[].class);
            sMethodMapToEpd = ReflectUtil.getMethodSafely(cls, "mapToEpd", View.class, float[].class, float[].class);
            sMethodMapFromRawTouchPoint = ReflectUtil.getMethodSafely(cls, "mapFromRawTouchPoint", View.class, float[].class, float[].class);
            sMethodMapToRawTouchPoint = ReflectUtil.getMethodSafely(cls, "mapToRawTouchPoint", View.class, float[].class, float[].class);
            sMethodEnablePost = ReflectUtil.getMethodSafely(cls, "enablePost", int.class);
            sMethodResetEpdPost = ReflectUtil.getMethodSafely(cls, "resetEpdPost");
            sMethodIsValidPenState = ReflectUtil.getMethodSafely(cls, "isValidPenState");
            sMethodGetPenState = ReflectUtil.getMethodSafely(cls, "getPenState");
            sMethodSetScreenHandWritingPenState = ReflectUtil.getMethodSafely(cls, "setScreenHandWritingPenState", int.class);
            sMethodSetScreenHandWritingRegionLimit = ReflectUtil.getMethodSafely(cls, "setScreenHandWritingRegionLimit", View.class, int[].class);
            sMethodSetScreenHandWritingRegionMode = ReflectUtil.getMethodSafely(cls, "setScreenHandWritingRegionMode", int.class);
            sMethodSetScreenHandWritingRegionExclude = ReflectUtil.getMethodSafely(cls, "setScreenHandWritingRegionExclude", View.class, int[].class);
            sMethodApplyGammaCorrection = ReflectUtil.getMethodSafely(cls, "applyGammaCorrection", boolean.class, int.class);
            sMethodApplyColorFilter = ReflectUtil.getMethodSafely(cls, "applyColorFilter", int.class);

            sMethodStartStroke = ReflectUtil.getMethodSafely(cls, "startStroke", float.class, float.class, float.class, float.class, float.class, float.class);
            sMethodAddStrokePoint = ReflectUtil.getMethodSafely(cls, "addStrokePoint", float.class, float.class, float.class, float.class, float.class, float.class);
            sMethodFinishStroke = ReflectUtil.getMethodSafely(cls, "finishStroke", float.class, float.class, float.class, float.class, float.class, float.class);

            // signature of "public void invalidate(int updateMode)"
            sMethodInvalidate = ReflectUtil.getMethodSafely(cls, "invalidate", int.class);
            sMethodInvalidateRect = ReflectUtil.getMethodSafely(cls, "invalidate", int.class, int.class, int.class, int.class, int.class);
            // signature of "public void invalidate(int updateMode)"
            sMethodSetViewDefaultUpdateMode = ReflectUtil.getMethodSafely(cls, "setDefaultUpdateMode", int.class);
            // signature of "public void invalidate(int updateMode)"
            sMethodGetViewDefaultUpdateMode = ReflectUtil.getMethodSafely(cls, "getDefaultUpdateMode");
            sMethodResetViewUpdateMode = ReflectUtil.getMethodSafely(cls, "resetUpdateMode");

            // signature of "public void invalidate(int updateMode)"
            sMethodGetSystemDefaultUpdateMode = ReflectUtil.getMethodSafely(cls, "getGlobalUpdateMode");
            // signature of "public void invalidate(int updateMode)"
            sMethodSetSystemDefaultUpdateMode = ReflectUtil.getMethodSafely(cls, "setGlobalUpdateMode", int.class);
            // signature of "public void setFirstDrawUpdateMode(int updateMode)"
            sMethodSetFirstDrawUpdateMode = ReflectUtil.getMethodSafely(cls, "setFirstDrawUpdateMode", int.class);
            // signature of "public void setWaveformAndScheme(int mode, int scheme)"
            sMethodSetSystemUpdateModeAndScheme = ReflectUtil.getMethodSafely(cls, "setWaveformAndScheme", int.class, int.class, int.class);
            // signature of "public void resetWaveformAndScheme()"
            sMethodClearSystemUpdateModeAndScheme = ReflectUtil.getMethodSafely(cls, "resetWaveformAndScheme");
            sMethodApplyApplicationFastMode = ReflectUtil.getMethodSafely(cls, "applyApplicationFastMode", String.class, boolean.class, boolean.class);
            sMethodApplyApplicationFastModeWithRepeat = ReflectUtil.getMethodSafely(cls, "applyApplicationFastMode", String.class, boolean.class, boolean.class, int.class, int.class);
            sMethodClearApplicationFastMode = ReflectUtil.getMethodSafely(cls, "clearApplicationFastMode");
            sMethodEnableScreenUpdate = ReflectUtil.getMethodSafely(cls, "enableScreenUpdate", boolean.class);
            sMethodSetDisplayScheme = ReflectUtil.getMethodSafely(cls, "setDisplayScheme", int.class);
            sMethodWaitForUpdateFinished = ReflectUtil.getMethodSafely(cls, "waitForUpdateFinished");
            sMethodSetUpdListSize = ReflectUtil.getMethodSafely(cls, "setUpdListSize", int.class);
            sMethodInSystemFastMode = ReflectUtil.getMethodSafely(cls, "inSystemFastMode");
            sMethodSetQRShowConfig = ReflectUtil.getMethodSafely(cls,"setQRShowConfig",int.class,int.class,int.class);
            sMethodSetInfoShowConfig = ReflectUtil.getMethodSafely(cls,"setInfoShowConfig",int.class,int.class,int.class);
            sMethodRepaintEveryThing = ReflectUtil.getMethodSafely(cls, "repaintEverything", int.class);
            sMethodApplySystemFastMode = ReflectUtil.getMethodSafely(cls, "switchToA2Mode");
            sMethodApplySFDebug = ReflectUtil.getMethodSafely(cls, "applySFDebug", boolean.class);

            sMethodSetVCom = ReflectUtil.getMethodSafely(deviceControllerClass, "setVCom", Context.class, int.class, String.class);
            sMethodGetVCom = ReflectUtil.getMethodSafely(deviceControllerClass, "getVCom", String.class);
            sMethodSetWaveform = ReflectUtil.getMethodSafely(deviceControllerClass, "updateWaveform", String.class, String.class);
            sMethodReadSystemConfig = ReflectUtil.getMethodSafely(deviceControllerClass, "readSystemConfig", String.class);
            sMethodSaveSystemConfig = ReflectUtil.getMethodSafely(deviceControllerClass, "saveSystemConfig", String.class, String.class);
            sMethodUpdateMetadataDB = ReflectUtil.getMethodSafely(deviceControllerClass, "updateMetadataDB", String.class, String.class);
            sMethodPowerCTP = ReflectUtil.getMethodSafely(deviceControllerClass, "powerCTP", boolean.class);
            sMethodPowerEMTP = ReflectUtil.getMethodSafely(deviceControllerClass, "powerEMTP", boolean.class);
            sMethodIsCTPPowerOn = ReflectUtil.getMethodSafely(deviceControllerClass, "isCTPPowerOn");
            sMethodIsEMTPPowerOn = ReflectUtil.getMethodSafely(deviceControllerClass, "isEMTPPowerOn");
            sMethodSetPwm1Output = ReflectUtil.getMethodSafely(deviceControllerClass, "setPwm1Output", int.class);

            sMethodGetNaturalLightValues = ReflectUtil.getMethodSafely(deviceControllerClass, "getCTMBrightnessValues", Context.class);
            sMethodSetWarmLightDeviceValue= ReflectUtil.getMethodSafely(deviceControllerClass, "setWarmLightDeviceValue", Context.class, int.class);
            sMethodSetColdLightDeviceValue= ReflectUtil.getMethodSafely(deviceControllerClass, "setColdLightDeviceValue", Context.class, int.class);
            sMethodGetBrightnessConfig = ReflectUtil.getMethodSafely(deviceControllerClass, "getBrightnessConfig", Context.class, int.class);
            sMethodIncreaseBrightness = ReflectUtil.getMethodSafely(deviceControllerClass, "increaseBrightness", Context.class, int.class);
            sMethodDecreaseBrightness = ReflectUtil.getMethodSafely(deviceControllerClass, "decreaseBrightness", Context.class, int.class);
            sMethodIsBrightnessOn  = ReflectUtil.getMethodSafely(deviceControllerClass, "isBrightnessOn", Context.class);

            Class<?> brightnessController = ReflectUtil.classForName("android.onyx.brightness.BrightnessController");
            sMethodIsBrightnessOn = ReflectUtil.getMethodSafely(brightnessController, "isBrightnessOn", Context.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                sMethodSetAppCTPDisableRegion = ReflectUtil.getMethodSafely(InputManager.class, "setAppCTPDisableRegion", int[].class, int[].class);
                sMethodIsCTPRegion = ReflectUtil.getMethodSafely(InputManager.class, "isCTPDisableRegion");
                sMethodAppResetCTPRegion = ReflectUtil.getMethodSafely(InputManager.class, "appResetCTPDisableRegion");
                sMethodIsEMTPDisabled = ReflectUtil.getMethodSafely(InputManager.class, "isEMTPDisabled");
                sMethodIsKeyboardDisabled = ReflectUtil.getMethodSafely(InputManager.class, "isKeyboardDisabled");
                sMethodSetEMTPDisabled = ReflectUtil.getMethodSafely(InputManager.class, "setEMTPDisabled", boolean.class);
                sMethodSetKeyboardDisabled = ReflectUtil.getMethodSafely(InputManager.class, "setKeyboardDisabled", boolean.class);
            }

            // signature of "public void enableA2()"
            sMethodEnableA2 = ReflectUtil.getMethodSafely(cls, "enableA2");
            // signature of "public void disableA2()"
            sMethodDisableA2 = ReflectUtil.getMethodSafely(cls, "disableA2");

            sMethodSwitchToA2Mode = ReflectUtil.getMethodSafely(cls, "switchToA2Mode");

            sMethodGetStorageRootDirectory = ReflectUtil.getMethodSafely(Environment.class,"getStorageRootDirectory");
            sMethodGetRemovableSDCardDirectory = ReflectUtil.getMethodSafely(Environment.class,"getRemovableSDCardDirectory");

            sMethodWebViewSetCssInjectEnabled = ReflectUtil.getMethodSafely(WebView.class, "setCssInjectEnabled", boolean.class);
            sMethodApplyGcOnce = ReflectUtil.getMethodSafely(cls, "applyGCOnce");
            sMethodSetTrigger = ReflectUtil.getMethodSafely(cls, "setTrigger", int.class);

            Class<?> eInkHelperClass = ReflectUtil.classForName("android.onyx.optimization.EInkHelper");
            sMethodRemoveAppConfig = ReflectUtil.getMethodSafely(eInkHelperClass, "removeAppConfig", String.class);
            sMethodSetAppConfig = ReflectUtil.getMethodSafely(eInkHelperClass, "setAppConfig", String.class, String.class);
            sMethodSetEACServiceConfig = ReflectUtil.getMethodSafely(eInkHelperClass, "setEACServiceConfig", boolean.class, boolean.class);
            sMethodGetEACAppConfigStringByPkgName = ReflectUtil.getMethodSafely(eInkHelperClass,
                    "getEACAppConfigStringByPkgName", String.class);

            sMethodForceStopApp = ReflectUtil.getMethodSafely(ActivityManager.class, "forceStopPackageWithoutPermissionCheck", String.class);

            Class<?> androidSettingHelperClass = ReflectUtil.classForName("android.onyx.AndroidSettingsHelper");
            sMethodGetIPAddress = ReflectUtil.getMethodSafely(androidSettingHelperClass, "getDefaultIpAddresses", Context.class);
            sMethodIsPowerSavedMode = ReflectUtil.getMethodSafely(androidSettingHelperClass, "isPowerSavedMode", Context.class);
            sMethodEnablePowerSavedMode = ReflectUtil.getMethodSafely(androidSettingHelperClass, "enablePowerSavedMode", Context.class, boolean.class);
            sMethodLoadCACertificate = ReflectUtil.getMethodSafely(androidSettingHelperClass, "loadCACertificate");
            sMethodLoadUserCertificate = ReflectUtil.getMethodSafely(androidSettingHelperClass, "loadUserCertificate");

            sMethodIsHallControlEnable = ReflectUtil.getMethodSafely(deviceControllerClass, "isHallControlEnable");
            sMethodEnableHallControl = ReflectUtil.getMethodSafely(deviceControllerClass, "enableHallControl", boolean.class);

            Class<?> appFreezeHelperClass = ReflectUtil.classForName("android.onyx.utils.ApplicationFreezeHelper");
            sMethodFreezeApp = ReflectUtil.getMethodSafely(appFreezeHelperClass, "disableAppByPkgName", Context.class, String.class);
            sMethodUnFreezeApp = ReflectUtil.getMethodSafely(appFreezeHelperClass, "enableAppByPkgName", Context.class, String.class);
            sMethodFreezeGooglePlay = ReflectUtil.getMethodSafely(appFreezeHelperClass, "disableGooglePlay", Context.class);
            sMethodUnFreezeGooglePlay = ReflectUtil.getMethodSafely(appFreezeHelperClass, "enableGooglePlay", Context.class);
            sMethodIsGooglePlayEnabled = ReflectUtil.getMethodSafely(appFreezeHelperClass, "isGoogleAppsEnabled", Context.class);
            sMethodIsGoogleAppsExists = ReflectUtil.getMethodSafely(appFreezeHelperClass, "isGoogleAppsExists", Context.class);

            Class<?> activityManagerHelperClass = ReflectUtil.classForName("android.onyx.utils.ActivityManagerHelper");
            sMethodGetCurrentTopComponent = ReflectUtil.getMethodSafely(activityManagerHelperClass, "getCurrentTopComponent", Context.class);

            Debug.d(RK33XXDevice.class, "init device finished.");
            return sInstance;
        }
        return sInstance;
    }

    public void useBigPen(boolean use) {
        invokeDeviceControllerMethod(null, sMethodUseBigPen, use);
    }

    public void stopTpd() {
        invokeDeviceControllerMethod(null, sMethodStopTpd);
    }

    public void startTpd() {
        invokeDeviceControllerMethod(null, sMethodStartTpd);
    }

    public void enableTpd(boolean enable) {
        ReflectUtil.invokeMethodSafely(sMethodEnableTpd, null, enable ? 1 : 0);
    }

    @Override
    public UpdateMode getViewDefaultUpdateMode(View view) {
        Integer res = (Integer) ReflectUtil.invokeMethodSafely(sMethodGetViewDefaultUpdateMode, view);
        if (res == null) {
            return UpdateMode.GU;
        }

        return this.updateModeFromValue(res.intValue());
    }

    public void resetViewUpdateMode(View view) {
        ReflectUtil.invokeMethodSafely(sMethodResetViewUpdateMode, view);
    }

    @Override
    public boolean setViewDefaultUpdateMode(View view, UpdateMode mode) {
        Object res = ReflectUtil.invokeMethodSafely(sMethodSetViewDefaultUpdateMode, view, getUpdateMode(mode));
        return res != null;
    }

    @Override
    public UpdateMode getSystemDefaultUpdateMode() {
        Integer res = (Integer) ReflectUtil.invokeMethodSafely(sMethodGetSystemDefaultUpdateMode, null);
        if (res == null) {
            return UpdateMode.GU;
        }

        return this.updateModeFromValue(res.intValue());
    }

    @Override
    public boolean setSystemDefaultUpdateMode(UpdateMode mode) {
        Object res = ReflectUtil.invokeMethodSafely(sMethodSetSystemDefaultUpdateMode, null, getUpdateMode(mode));
        return res != null;
    }

    public boolean setFirstDrawUpdateMode(UpdateMode mode) {
        Object res = ReflectUtil.invokeMethodSafely(sMethodSetFirstDrawUpdateMode, null, getUpdateMode(mode));
        return res != null;
    }

    @Override
    public boolean setSystemUpdateModeAndScheme(UpdateMode mode, UpdateScheme scheme, int count) {
        Object res = ReflectUtil.invokeMethodSafely(sMethodSetSystemUpdateModeAndScheme, null, getUpdateMode(mode), getUpdateScheme(scheme), count);
        return res != null;
    }

    @Override
    public boolean clearSystemUpdateModeAndScheme() {
        Object res = ReflectUtil.invokeMethodSafely(sMethodClearSystemUpdateModeAndScheme, null);
        return res != null;
    }

    public boolean applyApplicationFastMode(final String application, boolean enable, boolean clear) {
        Object res = ReflectUtil.invokeMethodSafely(sMethodApplyApplicationFastMode, null, application, enable, clear);
        return res != null;
    }

    @Override
    public boolean applyApplicationFastMode(String application, boolean enable, boolean clear, UpdateMode repeatMode, int repeatLimit) {
        Object res = ReflectUtil.invokeMethodSafely(sMethodApplyApplicationFastModeWithRepeat, null, application, enable, clear, getUpdateMode(repeatMode), repeatLimit);
        return res != null;
    }

    public boolean clearApplicationFastMode() {
        Object res = ReflectUtil.invokeMethodSafely(sMethodClearApplicationFastMode, null);
        return res != null;
    }

    @Override
    public int getFrontLightBrightnessMinimum(Context context) {
        return 0;
    }

    @Override
    public int getFrontLightBrightnessMaximum(Context context) {
        return getColdLightValues(context).length - 1;
    }

    @Override
    public boolean openFrontLight(Context context) {
        Boolean succ = (Boolean) this.invokeDeviceControllerMethod(context, sMethodOpenFrontLight, context);
        if (succ == null) {
            return false;
        }
        return succ.booleanValue();
    }

    @Override
    public boolean closeFrontLight(Context context) {
        Boolean succ = (Boolean) this.invokeDeviceControllerMethod(context, sMethodCloseFrontLight, context);
        if (succ == null) {
            return false;
        }

        return succ.booleanValue();
    }

    @Override
    public int getFrontLightDeviceValue(Context context) {
        Integer value = (Integer) this.invokeDeviceControllerMethod(context, sMethodGetFrontLightValue, context);
        if (value == null) {
            return 0;
        }
        return value.intValue();
    }

    @Override
    public boolean setFrontLightDeviceValue(Context context, int value) {
        Object res = this.invokeDeviceControllerMethod(context, sMethodSetFrontLightValue, context, Integer.valueOf(value));
        return res != null;
    }

    @Override
    public int getFrontLightConfigValue(Context context) {
        Integer res = (Integer) this.invokeDeviceControllerMethod(context, sMethodGetFrontLightConfigValue, context);
        return res.intValue();
    }

    @Override
    public boolean setFrontLightConfigValue(Context context, int value) {
        this.invokeDeviceControllerMethod(context, sMethodSetFrontLightConfigValue, context, Integer.valueOf(value));
        return true;
    }

    @Override
    public List<Integer> getFrontLightValueList(Context context) {
        Integer intValues[] = getColdLightValues(context);
        return Arrays.asList(intValues);
    }

    @Override
    public void led(Context context, boolean on) {
        this.invokeDeviceControllerMethod(context, sMethodLed, on);
    }

    public boolean setLedColor(final String color, final int on) {
        invokeDeviceControllerMethod(null, sMethodSetLedColor, color, on);
        return true;
    }

    @Override
    public void setVCom(Context context, int value, String path) {
        this.invokeDeviceControllerMethod(context, sMethodSetVCom, context, value, path);
        //return res != null;
    }

    @Override
    public int getVCom(Context context, String path) {
        Integer value = (Integer) this.invokeDeviceControllerMethod(context, sMethodGetVCom, path);
        if (value == null) {
            return 0;
        }
        return value.intValue();
    }

    @Override
    public void updateWaveform(Context context, String path, String target) {
        this.invokeDeviceControllerMethod(context, sMethodSetWaveform, path, target);
        //return res != null;
    }

    @Override
    public String readSystemConfig(Context context, String key) {
        Object result = this.invokeDeviceControllerMethod(context, sMethodReadSystemConfig, key);
        if (result == null || result.equals("")) {
            return "";
        }
        return result.toString();
    }

    @Override
    public boolean saveSystemConfig(Context context, String key, String value) {
        return (Boolean) this.invokeDeviceControllerMethod(context, sMethodSaveSystemConfig, key, value);
    }

    @Override
    public void updateMetadataDB(Context context, String path, String target) {
        this.invokeDeviceControllerMethod(context, sMethodUpdateMetadataDB, path, target);
    }

    /**
     * helper method to do trivial argument and exception check, return null if failed
     *
     * @param context
     * @param method
     * @return
     */
    private Object invokeDeviceControllerMethod(Context context, Method method, Object... args) {
        if (method == null) {
            return null;
        }

        return ReflectUtil.invokeMethodSafely(method, null, args);
    }

    UpdateMode getEpdMode(EPDMode mode) {
        switch (mode) {
            case FULL:
                return UpdateMode.GC;
            case AUTO:
            case TEXT:
            case AUTO_PART:
                return UpdateMode.GU;
            default:
                return UpdateMode.DU;
        }
    }

    int getUpdateMode(UpdateMode mode) {
        // default use GC update mode
        int dst_mode = sModeGC;

        switch (mode) {
            case GU_FAST:
            case DU:
                dst_mode = sModeDW;
                break;
            case GU:
                dst_mode = sModeGU;
                break;
            case GC:
                dst_mode = sModeGC;
                break;
            case ANIMATION:
                dst_mode = sModeAnimation;
                break;
            case ANIMATION_QUALITY:
                dst_mode = sModeAnimationQuality;
                break;
            case GC4:
                dst_mode = sModeGC4;
                break;
            case REGAL:
                dst_mode = sModeRegal != 0 ? sModeRegal : sModeGU;
                break;
            case REGAL_D:
                dst_mode = sModeRegalD != 0  ? sModeRegalD : sModeGU;
                break;
            default:
                dst_mode = sModeDefault;
                break;
        }
        return dst_mode;
    }

    private int getUpdateScheme(UpdateScheme scheme) {
        int dst_scheme = sSchemeQUEUE;
        switch (scheme) {
            case SNAPSHOT:
                dst_scheme = sSchemeSNAPSHOT;
                break;
            case QUEUE:
                dst_scheme = sSchemeQUEUE;
                break;
            case QUEUE_AND_MERGE:
                dst_scheme = sSchemeQUEUE_AND_MERGE;
                break;
            default:
                assert (false);
                break;
        }
        return dst_scheme;

    }

    private UpdateMode updateModeFromValue(int value) {
        if (value == sModeDW) {
            return UpdateMode.DU;
        } else if (value == sModeGU) {
            return UpdateMode.GU;
        } else if (value == sModeGC) {
            return UpdateMode.GC;
        }
        return UpdateMode.GC;
    }

    private static int getPolicyValue(UpdatePolicy policy) {
        int dst_value = sModeGU;
        switch (policy) {
            case Automatic:
                dst_value |= sPolicyAutomatic;
                break;
            case GUIntervally:
                dst_value |= sPolicyGUIntervally;
                break;
            default:
                assert (false);
                break;
        }

        return dst_value;
    }

    @Override
    public void disableA2ForSpecificView(View view) {
        ReflectUtil.invokeMethodSafely(sMethodDisableA2, view);
    }

    @Override
    public void enableA2ForSpecificView(View view) {
        ReflectUtil.invokeMethodSafely(sMethodEnableA2, view);
    }

    @Override
    public void setWebViewContrastOptimize(WebView view, boolean enabled) {
        ReflectUtil.invokeMethodSafely(sMethodWebViewSetCssInjectEnabled, view, enabled);
    }

    public void gotoSleep(final Context context) {
        long value = System.currentTimeMillis();
        ReflectUtil.invokeMethodSafely(sMethodGotoSleep, context, value);
    }

    @Override
    public void setUpdListSize(int size) {
        ReflectUtil.invokeMethodSafely(sMethodSetUpdListSize, null, size);
    }

    @Override
    public boolean inSystemFastMode() {
        Boolean value = (Boolean)ReflectUtil.invokeMethodSafely(sMethodInSystemFastMode, null);
        if (value == null) {
            return false;
        }
        return value.booleanValue();
    }

    @Override
    public void setQRShowConfig(int orientation, int startX, int startY) {
        ReflectUtil.invokeMethodSafely(sMethodSetQRShowConfig, null, orientation, startX, startY);
    }

    @Override
    public void setInfoShowConfig(int orientation, int startX, int startY) {
        ReflectUtil.invokeMethodSafely(sMethodSetInfoShowConfig, null, orientation, startX, startY);
    }

    @Override
    public String getUpgradePackageName() {
        return "update.upx";
    }

    @Override
    public boolean shouldVerifyUpdateModel() {
        return false;
    }

    @Override
    public void powerCTP(boolean on) {
        invokeDeviceControllerMethod(null, sMethodPowerCTP, on);
    }

    @Override
    public void powerEMTP(boolean on) {
        invokeDeviceControllerMethod(null, sMethodPowerEMTP, on);
    }

    @Override
    public boolean isCTPPowerOn() {
        Boolean value = (Boolean)ReflectUtil.invokeMethodSafely(sMethodIsCTPPowerOn, null);
        return value == null ? false : value;
    }

    @Override
    public boolean isEMTPPowerOn() {
        Boolean value = (Boolean)ReflectUtil.invokeMethodSafely(sMethodIsEMTPPowerOn, null);
        return value == null ? false : value;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Object getInputManager(Context context) {
        return context.getSystemService(Context.INPUT_SERVICE);
    }

    @Override
    public boolean isCTPDisableRegion(Context context) {
        Boolean value = (Boolean) ReflectUtil.invokeMethodSafely(sMethodIsCTPRegion, getInputManager(context));
        if (value == null) {
            return false;
        }
        return value;
    }

    @Override
    public void appResetCTPDisableRegion(Context context) {
        ReflectUtil.invokeMethodSafely(sMethodAppResetCTPRegion, getInputManager(context));
    }

    @Override
    public void setAppCTPDisableRegion(Context context, int[] disableRegionArray, @Nullable int[] excludeRegionArray) {
        ReflectUtil.invokeMethodSafely(sMethodSetAppCTPDisableRegion, getInputManager(context), disableRegionArray, excludeRegionArray);
    }

    @Override
    public void setAppCTPDisableRegion(Context context, Rect[] disableRegions, @Nullable Rect[] excludeRegions) {
        setAppCTPDisableRegion(context, convertRectArrayToIntArray(disableRegions), convertRectArrayToIntArray(excludeRegions));
    }

    @Override
    public void updateMtpDb(Context context, String filePath) {
        DeviceFileUtils.updateMtpDb(context, new File(filePath));
    }

    @Override
    public void updateMtpDb(Context context, File file) {
        DeviceFileUtils.updateMtpDb(context, file);
    }

    @Override
    public void removeAppConfig(String pkgName){
        ReflectUtil.invokeMethodSafely(sMethodRemoveAppConfig, null, pkgName);
    }

    @Override
    public void setEACServiceConfig(boolean enable, boolean debug) {
        ReflectUtil.invokeMethodSafely(sMethodSetEACServiceConfig, null, enable, debug);
    }

    @Override
    public void setEACAppConfig(String pkgName, String jsonString) {
        ReflectUtil.invokeMethodSafely(sMethodSetAppConfig, null, pkgName, jsonString);
    }

    @Override
    public void switchToA2Mode(){
        ReflectUtil.invokeMethodSafely(sMethodSwitchToA2Mode,null);
    }

    @Override
    public void applyGammaCorrection(boolean apply, int value) {
        ReflectUtil.invokeMethodSafely(sMethodApplyGammaCorrection, null, apply, value);
    }

    public void applyColorFilter(int value) {
        ReflectUtil.invokeMethodSafely(sMethodApplyColorFilter, null, value);
    }

    @Override
    public void applyGCOnce() {
        ReflectUtil.invokeMethodSafely(sMethodApplyGcOnce, null);
    }

    @Override
    public String getCTPInfo() {
        return DeviceFileUtils.readContentOfFile(new File("/sys/onyx_misc/captp_fwver"));
    }

    @Override
    public boolean hasWifi(Context context) {
        Boolean result = (Boolean) this.invokeDeviceControllerMethod(context, sMethodHasWifi, context);
        return result == null ? false : result;
    }

    @Override
    public boolean hasAudio(Context context) {
        Boolean result = (Boolean) this.invokeDeviceControllerMethod(context, sMethodHasAudio, context);
        return result == null ? false : result;
    }

    @Override
    public boolean hasFLBrightness(Context context) {
        Boolean result = (Boolean) this.invokeDeviceControllerMethod(context, sMethodHasFLBrightness, context);
        return result == null ? false : result;
    }

    @Override
    public boolean hasBluetooth(Context context) {
        Boolean result = (Boolean) this.invokeDeviceControllerMethod(context, sMethodHasBluetooth, context);
        return result == null ? false : result;
    }

    @Override
    public boolean hasCTMBrightness(Context context) {
        Boolean result = (Boolean) this.invokeDeviceControllerMethod(context, sMethodHasCTMBrightness, context);
        return result == null ? false : result;
    }

    /**
     * 3288 default is no tf support.
     * @param context
     * @return
     */
    @Override
    public boolean supportExternalSD(Context context) {
        Boolean result = (Boolean) this.invokeDeviceControllerMethod(context, sMethodSupportExternalSD, context);
        return result == null ? false : result;
    }

    @Override
    public void setTrigger(int count) {
        ReflectUtil.invokeMethodSafely(sMethodSetTrigger, null, count);
    }

    private Object getActivityManager(Context context) {
        return context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    @Override
    public void forceStopApplication(Context context, String pkgName) {
        ReflectUtil.invokeMethodSafely(sMethodForceStopApp, getActivityManager(context), pkgName);
    }

    @Override
    @Nullable
    public String getIPAddress(Context context) {
        Object object = ReflectUtil.invokeMethodSafely(sMethodGetIPAddress, null, context);
        if (object instanceof String) {
            return (String)object;
        }
        return null;
    }

    @Override
    public boolean isPowerSavedMode(Context context) {
        Boolean result = (Boolean) ReflectUtil.invokeMethodSafely(sMethodIsPowerSavedMode, null, context);
        return result == null ? false : result;
    }

    @Override
    public void enablePowerSavedMode(Context context, boolean enable) {
        ReflectUtil.invokeMethodSafely(sMethodEnablePowerSavedMode, null, context, enable);
    }

    @Override
    public boolean isHallControlEnable(Context context) {
        return (Boolean) this.invokeDeviceControllerMethod(context, sMethodIsHallControlEnable);
    }

    @Override
    public void enableHallControl(Context context, boolean enable) {
        this.invokeDeviceControllerMethod(context, sMethodEnableHallControl, enable);
    }

    @Override
    public boolean isGooglePlayEnabled(Context context) {
        Boolean result = (Boolean) ReflectUtil.invokeMethodSafely(sMethodIsGooglePlayEnabled, null, context);
        return result == null ? false : result;
    }

    @Override
    public boolean isGoogleAppsExists(Context context) {
        Boolean result = (Boolean) ReflectUtil.invokeMethodSafely(sMethodIsGoogleAppsExists, null, context);
        return result == null ? false : result;
    }

    @Override
    public void freezeApplication(Context context, String pkgName) {
        ReflectUtil.invokeMethodSafely(sMethodFreezeApp, null, context, pkgName);
    }

    @Override
    public void unfreezeApplication(Context context, String pkgName) {
        ReflectUtil.invokeMethodSafely(sMethodUnFreezeApp, null, context, pkgName);
    }

    @Override
    public void freezeGooglePlay(Context context) {
        ReflectUtil.invokeMethodSafely(sMethodFreezeGooglePlay, null, context);
    }

    @Override
    public void unfreezeGooglePlay(Context context) {
        ReflectUtil.invokeMethodSafely(sMethodUnFreezeGooglePlay, null, context);
    }

    @Override
    public void repaintEveryThing(UpdateMode mode) {
        ReflectUtil.invokeMethodSafely(sMethodRepaintEveryThing, null, getUpdateMode(mode));
    }

    @Override
    public void applySystemFastMode(boolean enable) {
        if (enable) {
            ReflectUtil.invokeMethodSafely(sMethodApplySystemFastMode, null);
        } else {
            clearSystemUpdateModeAndScheme();
        }
    }

    /**
     * type 0 as cold,type 1 as warm.
     *
     * @param type
     * @param value
     */
    @Override
    public void setCTMBrightnessValue(int type, int value) {
        switch (type) {
            case 0:
                ReflectUtil.invokeMethodSafely(sMethodWriteValueToFile, null, mColdLightFile, value);
                break;
            case 1:
                ReflectUtil.invokeMethodSafely(sMethodWriteValueToFile, null, mWarmLightFile, value);
                break;
        }
    }

    @Override
    public String[] loadCACertificate() {
        return (String[]) ReflectUtil.invokeMethodSafely(sMethodLoadCACertificate, null);
    }

    @Override
    public String[] loadUserCertificate() {
        return (String[]) ReflectUtil.invokeMethodSafely(sMethodLoadUserCertificate, null);
    }

    @Override
    public void applySFDebug(boolean enableDebug) {
        ReflectUtil.invokeMethodSafely(sMethodApplySFDebug, null, enableDebug);
    }

    @Override
    public boolean isEMTPDisabled(Context context) {
        Boolean value = (Boolean) ReflectUtil.invokeMethodSafely(sMethodIsEMTPDisabled, getInputManager(context));
        if (value == null) {
            return false;
        }
        return value;
    }

    @Override
    public boolean isKeyboardDisabled(Context context) {
        Boolean value = (Boolean) ReflectUtil.invokeMethodSafely(sMethodIsKeyboardDisabled, getInputManager(context));
        if (value == null) {
            return false;
        }
        return value;
    }

    @Override
    public void setEMTPDisabled(Context context, boolean disabled) {
        ReflectUtil.invokeMethodSafely(sMethodSetEMTPDisabled, getInputManager(context), disabled);
    }

    @Override
    public void setKeyboardDisabled(Context context, boolean disabled) {
        ReflectUtil.invokeMethodSafely(sMethodSetKeyboardDisabled, getInputManager(context), disabled);
    }

    public Integer[] getWarmLightValues(Context context) {
        Integer[][] values = getNaturalLightValues(context);
        if (values != null && values.length > 0) {
            return values[0];
        }
        return null;
    }

    @Override
    public Integer[] getColdLightValues(Context context) {
        Integer[][] values = getNaturalLightValues(context);
        if (values != null && values.length > 1) {
            return values[1];
        }
        return null;
    }

    @Override
    public Integer[][] getNaturalLightValues(Context context) {
        Object object = this.invokeDeviceControllerMethod(context, sMethodGetNaturalLightValues, context);
        if (object != null && object instanceof Integer[][]) {
            return (Integer[][]) object;
        }
        return null;
    }

    @Override
    public int getWarmLightConfigValue(Context context) {
        Object object = this.invokeDeviceControllerMethod(context, sMethodGetBrightnessConfig, context, 0);
        if (object != null) {
            return (Integer) object;
        }
        return 0;
    }

    @Override
    public int getColdLightConfigValue(Context context) {
        Object object = this.invokeDeviceControllerMethod(context, sMethodGetBrightnessConfig, context, 1);
        if (object != null) {
            return (Integer) object;
        }
        return 0;
    }

    @Override
    public boolean setWarmLightDeviceValue(Context context, int value) {
        Object res = this.invokeDeviceControllerMethod(context, sMethodSetWarmLightDeviceValue, context, Integer.valueOf(value));
        return res != null;
    }

    @Override
    public boolean setColdLightDeviceValue(Context context, int value) {
        Object res = this.invokeDeviceControllerMethod(context, sMethodSetColdLightDeviceValue, context, Integer.valueOf(value));
        return res != null;
    }

    @Override
    public boolean increaseBrightness(Context context, int colorTemp) {
        Boolean result = (Boolean) this.invokeDeviceControllerMethod(context, sMethodIncreaseBrightness, context,  Integer.valueOf(colorTemp));
        return result == null ? false : result;
    }

    @Override
    public boolean decreaseBrightness(Context context, int colorTemp) {
        Boolean result = (Boolean) this.invokeDeviceControllerMethod(context, sMethodDecreaseBrightness, context,  Integer.valueOf(colorTemp));
        return result == null ? false : result;
    }

    @Nullable
    @Override
    public ComponentName getCurrentTopComponent(Context context) {
        return (ComponentName) ReflectUtil.invokeMethodSafely(sMethodGetCurrentTopComponent, null, context);
    }

    @Nullable
    public String getEACAppConfigByPkgName(String pkgName) {
        Object res = ReflectUtil.invokeMethodSafely(sMethodGetEACAppConfigStringByPkgName, null, pkgName);
        if (!(res instanceof String)) {
            return "";
        }
        return (String) res;
    }

    @Override
    public boolean isBrightnessOn(Context context) {
        if (!(hasCTMBrightness(context) || hasFLBrightness(context))) {
            return false;
        }
        Boolean value = (Boolean) ReflectUtil.invokeMethodSafely(sMethodIsBrightnessOn, null, context);
        if (value == null) {
            return false;
        }
        return value;
    }

    @Override
    public void setPwm1Output(int value) {
        this.invokeDeviceControllerMethod(null, sMethodSetPwm1Output, value);
    }
}
