package com.onyx.android.sdk.device;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

import com.onyx.android.sdk.api.device.epd.EPDMode;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.api.device.epd.UpdateScheme;
import com.onyx.android.sdk.utils.Debug;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Joy on 2016/5/10.
 */
public class BaseDevice {
    private static final String TAG = BaseDevice.class.getSimpleName();
    private final static String SHOW_STATUS_BAR_ACTION = "show_status_bar";
    private final static String HIDE_STATUS_BAR_ACTION = "hide_status_bar";
    private final static String ENABLE_WIFI_CONNECT_STATUS_DETECTION_ACTION = "enable_wifi_connect_status_detection_status";
    private static final String ONYX_WIFI_DETECT_CANCEL_ACTION = "onyx.settings.action.cancel.wifi.detect";

    private final static String ARGS_WIFI_CONNECT_DETECTION_FLAG = "args_wifi_connect_detection_flag";
    private final static String ARGS_WIFI_CONNECT_DETECTION_LATENCY_FLAG = "args_wifi_connect_detection_latency_flag";

    private final static String SWITCH_TO_PAGE_KEY_ACTION = "switch_to_page_key";
    private final static String SWITCH_TO_VOLUME_KEY_ACTION = "switch_to_volume_key";
    private final static String SWITCH_TO_HOME_BACK_KEY_ACTION = "switch_to_home_back_key";
    private final static String SWITCH_KEY = "switch_key";
    private static final String ENG_VERSION = "eng";
    private static final String USER_DEBUG_VERSION = "userdebug";

    public static final int LIGHT_TYPE_NONE = 0;
    public static final int LIGHT_TYPE_FL = 1;
    public static final int LIGHT_TYPE_CTM_WARM = 2;
    public static final int LIGHT_TYPE_CTM_COLD = 3;
    public static final int LIGHT_TYPE_CTM_ALL = 4;

    public static int UPDATE_MODE_DEFAULT = 0;
    public static int UPDATE_MODE_DU = 0;
    public static int UPDATE_MODE_A2 = 0;
    public static int UPDATE_MODE_REGAL = 0;
    public static int UPDATE_MODE_X = 0;

    public File getStorageRootDirectory() {
        return android.os.Environment.getExternalStorageDirectory();
    }

    public File getExternalStorageDirectory() {
        return android.os.Environment.getExternalStorageDirectory();
    }

    public File getRemovableSDCardDirectory() {
        File storage_root = getExternalStorageDirectory();

        // if system has an emulated SD card(/mnt/sdcard) provided by device's NAND flash,
        // then real SD card will be mounted as a child directory(/mnt/sdcard/extsd) in it, which names "extsd" here
        final String SDCARD_MOUNTED_FOLDER = "extsd";

        File extsd = new File(storage_root, SDCARD_MOUNTED_FOLDER);
        if (extsd.exists()) {
            return extsd;
        } else {
            return storage_root;
        }
    }

    //TODO:for FileUtil to parse uri.
    public File getBluetoothRootDirectory() {
        return new File(getExternalStorageDirectory().getPath() + File.separator + "bluetooth");
    }

    public boolean isFileOnRemovableSDCard(File file) {
        return file.getAbsolutePath().startsWith(getRemovableSDCardDirectory().getAbsolutePath());
    }

    public PowerManager.WakeLock newWakeLock(Context context, String tag) {
        PowerManager pm = (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        return pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, tag);
    }

    public PowerManager.WakeLock newWakeLockWithFlags(Context context, int flags, String tag) {
        PowerManager pm = (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        return pm.newWakeLock(flags, tag);
    }

    public void useBigPen(boolean use) {
    }

    public void stopTpd() {
    }

    public void startTpd() {
    }

    public void enableTpd(boolean enable) {
    }

    public float getTouchWidth() {
        return 0;
    }

    public float getTouchHeight() {
        return 0;
    }

    public float getMaxTouchPressure() {
        return 1024;
    }

    public float getEpdWidth() {
        return 0;
    }

    public float getEpdHeight() {
        return 0;
    }

    public void mapToView(View view, float[] src, float[] dst) {
    }

    public void mapToEpd(View view, float[] src, float[] dst) {
    }

    public Rect mapToEpd(View view, Rect srcRect) {
        float src[] = new float[]{srcRect.left, srcRect.top};
        float dst[] = new float[2];
        float dst2[] = new float[2];
        mapToEpd(view, src, dst);
        src[0] = srcRect.right;
        src[1] = srcRect.bottom;
        mapToEpd(view, src, dst2);
        return new Rect(
                (int)Math.min(dst[0], dst2[0]),
                (int)Math.min(dst[1], dst2[1]),
                (int)Math.max(dst[0], dst2[0]),
                (int)Math.max(dst[1], dst2[1]));
    }

    public void mapFromRawTouchPoint(View view, float[] src, float[] dst) {
    }

    public void mapToRawTouchPoint(View view, float[] src, float[] dst) {
    }

    public RectF mapToRawTouchPoint(View view, RectF rect) {
        float src[] = new float[]{rect.left, rect.top};
        float dst[] = new float[2];
        float dst2[] = new float[2];
        mapToRawTouchPoint(view, src, dst);

        src[0] = rect.right;
        src[1] = rect.bottom;
        mapToRawTouchPoint(view, src, dst2);
        return new RectF(
                Math.min(dst[0], dst2[0]),
                Math.min(dst[1], dst2[1]),
                Math.max(dst[0], dst2[0]),
                Math.max(dst[1], dst2[1]));
    }

    public int getFrontLightBrightnessMinimum(Context context) {
        return 0;
    }

    public int getFrontLightBrightnessMaximum(Context context) {
        return 0;
    }

    public int getFrontLightBrightnessDefault(Context context) {
        return 0;
    }

    public boolean openFrontLight(Context context) {
        return false;
    }

    public boolean closeFrontLight(Context context) {
        return false;
    }

    public boolean setLedColor(final String ledColor, final int on) {
        return false;
    }

    public int getFrontLightDeviceValue(Context context) {
        return 0;
    }

    public List<Integer> getFrontLightValueList(Context context) {
        return new ArrayList<Integer>();
    }

    public boolean setFrontLightDeviceValue(Context context, int value) {
        return false;
    }

    public boolean setNaturalLightConfigValue(Context context, int value) {
        return false;
    }

    public int getFrontLightConfigValue(Context context) {
        return 0;
    }

    public boolean setFrontLightConfigValue(Context context, int value) {
        return false;
    }

    public EPDMode getEpdMode() {
        return EPDMode.AUTO;
    }

    public boolean setEpdMode(Context context, EPDMode mode) {
        return false;
    }

    public boolean setEpdMode(View view, EPDMode mode) {
        return false;
    }

    public UpdateMode getViewDefaultUpdateMode(View view) {
        return UpdateMode.GU;
    }

    public boolean setViewDefaultUpdateMode(View view, UpdateMode mode) {
        return false;
    }

    public void resetViewUpdateMode(View view) {
    }

    public UpdateMode getSystemDefaultUpdateMode() {
        return UpdateMode.GU;
    }

    public boolean setSystemDefaultUpdateMode(UpdateMode mode) {
        return false;
    }

    public boolean applyApplicationFastMode(final String application, boolean enable, boolean clear) {
        return false;
    }

    public boolean applyApplicationFastMode(final String application, boolean enable, boolean clear, UpdateMode repeatMode, int repeatLimit) {
        return false;
    }

    public boolean clearApplicationFastMode() {
        return false;
    }

    public boolean setDisplayScheme(int scheme) {
        return false;
    }

    public void waitForUpdateFinished() {
    }

    public void invalidate(View view, UpdateMode mode) {
        view.invalidate();
    }

    public void invalidate(View view, int left, int top, int right, int bottom, UpdateMode mode) {
    }

    public boolean enableScreenUpdate(View view, boolean enable) {
        return false;
    }


    public void refreshScreen(View view, UpdateMode mode) {
    }

    public void refreshScreenRegion(View view, int left, int top, int width, int height, UpdateMode mode) {
    }

    public void screenshot(View view, int r, final String path) {
    }

    public boolean supportDFB() {
        return false;
    }

    public boolean supportRegal() {
        return false;
    }

    public void holdDisplay(boolean hold, UpdateMode updateMode, int ignoreFrame) {}

    public void byPass(int count) {}

    public void setStrokeColor(int color) {
    }

    public void setStrokeStyle(int style) {
    }

    public void setStrokeWidth(float width) {}

    public void setPainterStyle(boolean antiAlias, Paint.Style strokeStyle, Paint.Join joinStyle, Paint.Cap capStyle) {
    }

    public void moveTo(float x, float y, float width) {
    }

    public void moveTo(View view, float x, float y, float width) {
    }

    public void lineTo(float x, float y, UpdateMode mode) {
    }

    public void lineTo(View view, float x, float y, UpdateMode mode) {
    }

    public void quadTo(float x, float y, UpdateMode mode) {
    }

    public void quadTo(View view, float x, float y, UpdateMode mode) {
    }

    public float startStroke(float baseWidth, float x, float y, float pressure, float size, float time) {
        return baseWidth;
    }

    public float addStrokePoint(float baseWidth, float x, float y, float pressure, float size, float time) {
        return baseWidth;
    }

    public float finishStroke(float baseWidth, float x, float y, float pressure, float size, float time) {
        return baseWidth;
    }

    @Deprecated
    public void enterScribbleMode(View view) {
    }

    @Deprecated
    public void leaveScribbleMode(View view) {
    }

    @Deprecated
    public void enablePost(View view, int enable) {
    }

    public void enablePost(int enable) {
    }

    public void enterScribbleMode() {
    }

    public void leaveScribbleMode() {
    }

    public void resetEpdPost() {
    }

    public boolean isValidPenState() {
        return true;
    }

    public int getPenState() {
        return 0;
    }

    public void setScreenHandWritingPenState(View view, int penState) {
    }

    public void setScreenHandWritingRegionMode(View view, int mode) {
    }

    public void setScreenHandWritingRegionLimit(View view) {
    }

    public void setScreenHandWritingRegionLimit(View view, int left, int top, int right, int bottom) {
    }

    public void setScreenHandWritingRegionLimit(View view, int[] array) {
    }


    public void setScreenHandWritingRegionLimit(View view, Rect[] regions) {
    }

    public void setScreenHandWritingRegionExclude(View view, int[] array) {
    }

    public void setScreenHandWritingRegionExclude(View view, Rect[] regions) {
    }

    public void postInvalidate(View view, UpdateMode mode) {
        view.postInvalidate();
    }

    public boolean setSystemUpdateModeAndScheme(UpdateMode mode, UpdateScheme scheme, int count) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean clearSystemUpdateModeAndScheme() {
        // TODO Auto-generated method stub
        return false;
    }

    public void wifiLock(Context context, String className) {
        // TODO Auto-generated method stub

    }

    public void wifiUnlock(Context context, String className) {
        // TODO Auto-generated method stub

    }

    public void wifiLockClear(Context context) {
        // TODO Auto-generated method stub

    }

    public Map<String, Integer> getWifiLockMap(Context context) {
        // TODO Auto-generated method stub
        return null;
    }

    public void setWifiLockTimeout(Context context, long ms) {
        // TODO Auto-generated method stub

    }

    public String getEncryptedDeviceID() {
        return null;
    }

    public void led(Context context, boolean on) {

    }

    public void setVCom(Context context, int mv, String path) {

    }

    public void updateWaveform(Context context, String path, String target) {

    }

    public int getVCom(Context context, String path) {
        return 0;
    }

    public String readSystemConfig(Context context, String key) {
        return "";
    }

    public boolean saveSystemConfig(Context context, String key, String mv) {
        return false;
    }

    public void updateMetadataDB(Context context, String path, String target) {
    }

    public Point getWindowWidthAndHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        point.x = wm.getDefaultDisplay().getWidth();
        point.y = wm.getDefaultDisplay().getHeight();
        return point;
    }

    public void hideSystemStatusBar(Context context) {
        broadcastActionToSystemStatusBar(context, HIDE_STATUS_BAR_ACTION);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Debug.printStackTraceDebug("hideSystemStatusBar");
        }
    }

    public void showSystemStatusBar(Context context) {
        broadcastActionToSystemStatusBar(context, SHOW_STATUS_BAR_ACTION);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Debug.printStackTraceDebug("showSystemStatusBar");
        }
    }

    public void setSystemStatusBarVisible(Context context, boolean visible) {
        if (visible) {
            showSystemStatusBar(context);
        } else {
            hideSystemStatusBar(context);
        }
    }

    public void mapSideKeyToVolumeKey(Context context) {
        broadcastActionToSystemStatusBar(context, SWITCH_TO_VOLUME_KEY_ACTION);
    }

    public void mapSideKeyToHomeBackKey(Context context) {
        broadcastActionToSystemStatusBar(context, SWITCH_TO_HOME_BACK_KEY_ACTION);
    }

    public void resetKeyMapping(Context context) {
        broadcastActionToSystemStatusBar(context, SWITCH_TO_PAGE_KEY_ACTION);
    }

    public int getCurrentSideKeyMapping(Context context) {
        try {
            return Settings.System.getInt(context.getContentResolver(), SWITCH_KEY);
        } catch (Exception e) {
            if (!(e instanceof Settings.SettingNotFoundException)) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public void enableWifiDetect(Context context) {
        enableWifiDetect(context, true);
    }

    public void enableWifiDetect(Context context, boolean enableDetect) {
        enableWifiDetect(context, enableDetect, 3000);
    }

    public void enableWifiDetect(Context context, boolean enableDetect, int detectLatency) {
        Intent intent = new Intent(ENABLE_WIFI_CONNECT_STATUS_DETECTION_ACTION);
        intent.putExtra(ARGS_WIFI_CONNECT_DETECTION_FLAG, enableDetect);
        intent.putExtra(ARGS_WIFI_CONNECT_DETECTION_LATENCY_FLAG, detectLatency);
        context.sendBroadcast(intent);
    }

    public void cancelPendingWifiDetect(Context context){
        Intent intent = new Intent(ONYX_WIFI_DETECT_CANCEL_ACTION);
        context.sendBroadcast(intent);
    }

    private void broadcastActionToSystemStatusBar(Context context, String action) {
        context.sendBroadcast(new Intent(action));
    }

    public void stopBootAnimation() {
    }

    public void disableA2ForSpecificView(View view) {
    }

    public void enableA2ForSpecificView(View view) {
    }

    public void setWebViewContrastOptimize(WebView view, boolean enabled) {

    }

    public boolean isLegalSystem(final Context context){
        return true;
    }

    public boolean isTouchable(Context context) {
        return true;
    }

    public void gotoSleep(final Context context) {}

    public void enableRegal(boolean enable) {
    }

    public boolean hasWifi(Context context) {
        return true;
    }

    public void setQRShowConfig(int orientation, int startX, int startY) {};

    public void setInfoShowConfig(int orientation, int startX, int startY) {}

    public void setUpdListSize(int size) {
    }

    public boolean inSystemFastMode() {
        return false;
    }

    public String getUpgradePackageName() {
        return "update.zip";
    }

    public boolean shouldVerifyUpdateModel() {
        return true;
    }

    public void powerCTP(boolean on) {
    }

    public void powerEMTP(boolean on) {
    }

    public boolean isCTPPowerOn() {
        return true;
    }

    public boolean isEMTPPowerOn() {
        return true;
    }

    public void setAppCTPDisableRegion(Context context, int[] disableRegionArray, @Nullable int[] excludeRegionArray) {
    }

    public void setAppCTPDisableRegion(Context context, Rect[] disableRegions, @Nullable Rect[] excludeRegions) {
    }

    public boolean isCTPDisableRegion(Context context) {
        return false;
    }

    public void appResetCTPDisableRegion(Context context) {
    }

    public void updateMtpDb(Context context, String filePath){}

    public void updateMtpDb(Context context, File file){}

    @Deprecated
    public void removeAppConfig(String jsonString) {
    }

    @Deprecated
    public void setEACServiceConfig(boolean enable, boolean debug) {
    }

    @Deprecated
    public void setEACAppConfig(String pkgName, String jsonString) {
    }

    public void switchToA2Mode(){
    }

    public void toggleA2Mode(){
    }

    public void applyGammaCorrection(boolean apply, int value){
    }

    public void applyColorFilter(int value) {
    }

    public void applyGCOnce() {}

    public String getCTPInfo() {
        return DeviceFileUtils.readContentOfFile(new File("/sys/android_touch/vendor"));
    }

    public boolean hasAudio(Context context) {
        return true;
    }

    public boolean hasFLBrightness(Context context) {
        return false;
    }

    public boolean hasCTMBrightness(Context context) {
        return false;
    }

    public boolean hasBluetooth(Context context) {
        return false;
    }

    public boolean supportExternalSD(Context context) {
        return true;
    }

    public void setTrigger(int count) {
    }

    public void freezeApplication(Context context, String pkgName) {
    }

    public void unfreezeApplication(Context context, String pkgName) {
    }

    public void forceStopApplication(Context context, String pkgName){
    }

    public void freezeGooglePlay(Context context) {
    }

    public void unfreezeGooglePlay(Context context) {
    }

    public boolean isGooglePlayEnabled(Context context) {
        return false;
    }

    public boolean isGoogleAppsExists(Context context) {
        return false;
    }

    @Nullable
    protected int[] convertRectArrayToIntArray(@Nullable Rect[] regions) {
        if (regions == null) {
            return null;
        }
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
        return array;
    }

    @SuppressLint("HardwareIds")
    public String getBluetoothAddress() {
        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (bluetooth != null) {
            String address = bluetooth.isEnabled() ? bluetooth.getAddress() : null;
            if (!TextUtils.isEmpty(address)) {
                // Convert the address to lowercase for consistency with the wifi MAC address.
                return address.toLowerCase();
            }
        }
        return "";
    }

    @Nullable
    public String getIPAddress(Context context){
        return null;
    }

    public String getOpenSourceCodeLicensePath(){
        return "/system/etc/NOTICE.html.gz";
    }

    public boolean isPowerSavedMode(Context context) {
        return false;
    }

    public void enablePowerSavedMode(Context context, boolean enable) {
    }

    public boolean isHallControlEnable(Context context) {
        return false;
    }

    public void enableHallControl(Context context, boolean enable) {
    }

    /**
     *
     * @return elapsed milliseconds since boot.
     */
    public long getBootUpTime() {
        return SystemClock.elapsedRealtime();
    }

    public void repaintEveryThing(UpdateMode mode) {}

    public void handwritingRepaint(View view, int left, int top, int right, int bottom) {
    }

    public void applySystemFastMode(boolean enable) {
    }

    public void setCTMBrightnessValue(int type, int value) {
    }

    public String[] loadCACertificate() {
        return new String[0];
    }

    public String[] loadUserCertificate() {
        return new String[0];
    }

    public void applySFDebug(boolean enableDebug){}

    public boolean isEMTPDisabled(Context context) {
        return false;
    }

    public boolean isKeyboardDisabled(Context context) {
        return false;
    }

    public void setEMTPDisabled(Context context, boolean disabled) {
    }

    public void setKeyboardDisabled(Context context, boolean disabled) {
    }

    public Integer[] getWarmLightValues(Context context) {
        return new Integer[0];
    }

    public Integer[] getColdLightValues(Context context) {
        return new Integer[0];
    }

    public Integer[][] getNaturalLightValues(Context context) {
        return new Integer[0][];
    }

    public int getWarmLightConfigValue(Context context) {
        return 0;
    }

    public int getColdLightConfigValue(Context context) {
        return 0;
    }

    public boolean setWarmLightDeviceValue(Context context, int value) {
        return false;
    }

    public boolean setColdLightDeviceValue(Context context, int value) {
        return false;
    }
  
    @Nullable
    public ComponentName getCurrentTopComponent(Context context) {
        return null;
    }

    public List<ActivityManager.RunningTaskInfo> getRunningTasksWithoutPermissionCheck(Context context, int maxNum){
        return Collections.emptyList();
    }

    @Nullable
    public String getEACAppConfigByPkgName(String pkgName){
        return null;
    }

    public boolean increaseBrightness(Context context, int colorTemp) {
            return false;
    };

    public boolean decreaseBrightness(Context context, int colorTemp) {
        return false;
    };

    @Deprecated
    public boolean isBrightnessOn(Context context) {
        return false;
    }

    public boolean isLightOn(Context context) {
        return false;
    }

    public boolean isLightOn(Context context, int type) {
        return false;
    }

    public boolean isEngVersion() {
        return ENG_VERSION.equalsIgnoreCase(Build.TYPE);
    }

    public boolean isUserDebugVersion() {
        return USER_DEBUG_VERSION.equalsIgnoreCase(Build.TYPE);
    }

    public void setPwm1Output(int value) {}

    public void configLightStatusBar(Window window) {
    }

    public void setTaskWindowingModeSplitScreenPrimary(Context context, int taskId, int createMode) {

    }

    /**
     *
     * @param rotation {@link Surface#ROTATION_0}
     */
    public void setRotationLockAtAngle(Context context, final boolean enabled, final int rotation) {

    }

    public void dismissSplitScreenMode(Context context, boolean toTop) {

    }

    public boolean isInMultiWindowMode(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return activity.isInMultiWindowMode();
        }
        return false;
    }
    public int getFrontLightTypeCTMWarm() {
        return 2;
    }

    public int getFrontLightTypeCTMCold() {
        return 3;
    }

    public void resizeDockedStack(Context context,
                                  Rect dockedBounds, Rect tempDockedTaskBounds,
                                  Rect tempDockedTaskInsetBounds,
                                  Rect tempOtherTaskBounds, Rect tempOtherTaskInsetBounds) {

    }

    public Rect getTaskBounds(Context context, int taskId) {
        return new Rect();
    }

    public int getTaskWindowingMode(Context context, int taskId) {
        return 0;
    }

    public void setDockedStackCreateState(Context context, int mode, Rect bounds) {

    }

    public boolean isSystemInMultiWindowMode(Context context) {
        return false;
    }

    public String getSystemConfigPrefix(Context context) {
        return "/vendor/";
    }

    public boolean isOECEnable() {
        return  false;
    }

    public int getGlobalContrast() {
        return -1;
    }

    public void setSystemProperties(String key, String value) {}

    public String getSystemProperties(String key) {
        return null;
    }

    public int getSystemRefreshMode() {
        return 0;
    }

    public void dumpCTPInfo(Context context) {}

    public int getAppActiveNotificationsCount(Context context, String pkg) {
        return 0;
    }

    public boolean isInSystemRefreshModeDefault() {
        return false;
    }

    public boolean isInSystemRefreshModeX() {
        return false;
    }
}

