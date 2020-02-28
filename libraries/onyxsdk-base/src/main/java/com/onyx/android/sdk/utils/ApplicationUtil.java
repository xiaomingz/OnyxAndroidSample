package com.onyx.android.sdk.utils;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import com.onyx.android.sdk.data.AppBaseInfo;
import com.onyx.android.sdk.data.AppDataInfo;
import com.onyx.android.sdk.data.AppWidgetInfo;
import com.onyx.android.sdk.data.AppsConstant;
import com.onyx.android.sdk.device.Device;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.onyx.android.sdk.data.AppsConstant.ICON_SUFFIX;
import static com.onyx.android.sdk.utils.BaseConstant.ANDROID_SETTING_PACKAGE_NAME;

/**
 * Created by suicheng on 2017/2/16.
 */
public class ApplicationUtil {
    private static final String TAG = ApplicationUtil.class.getSimpleName();

    public static final String DONE_TAG = "done";
    public static final String DATA_KEEP = "/data/keep";
    private static final String APP_INFO = "android.onyx.intent.action.APP_INFO";
    private static final String ARGS_PKG = "package";
    private static final String ARGS_UID = "uid";
    public static final String ONYX_APP_PKG_TAG = "com.onyx";
    private static final String VPN_SERVICE_TAG = "android.net.VpnService";

    private static boolean checkDataKeepRecord(Context context, final String packageName) {
        return StringUtils.isNotBlank(packageName) && new File(DATA_KEEP, packageName).exists();
    }

    private static boolean checkSystemConfigRecord(Context context, final String packageName) {
        return StringUtils.isNotBlank(packageName) && StringUtils.isNotBlank(Device.currentDevice().readSystemConfig(context, packageName));
    }

    public static boolean testAppRecordExist(Context context, final String packageName) {
        return checkDataKeepRecord(context, packageName) || checkSystemConfigRecord(context, packageName);
    }

    public static boolean setSystemVerifyFlagDone(Context context, String verifyFlag) {
        Device.currentDevice().saveSystemConfig(context, verifyFlag, DONE_TAG);
        return true;
    }

    public static boolean clearAllTestApps(Context context, List<String> testAppList) {
        if (testAppList == null) {
            return false;
        }

        for (String object : testAppList) {
            Device.currentDevice().saveSystemConfig(context, object, DONE_TAG);
        }
        return true;
    }

    public static boolean isResourceVerified(Context context, final String verifyTag) {
        return checkDataKeepRecord(context, verifyTag) || checkSystemConfigRecord(context, verifyTag);
    }

    public static boolean isSystemApp(String pkgName, PackageManager pkgManager) throws PackageManager.NameNotFoundException {
        return isSystemApp(pkgManager.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES));
    }

    public static boolean isSystemApp(PackageInfo pkgInfo) {
        return isSystemApp(pkgInfo.applicationInfo);
    }

    public static boolean isSystemApp(ApplicationInfo applicationInfo) {
        return (applicationInfo.flags & (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP | ApplicationInfo.FLAG_SYSTEM)) > 0;
    }

    public static boolean isOnyxApp(PackageInfo info) {
        return isOnyxApp(info.packageName);
    }

    public static boolean isOnyxApp(String pkgName) {
        return pkgName.contains(ONYX_APP_PKG_TAG);
    }

    static public AppDataInfo appDataFromApplicationInfo(final Context context,
                                                         final String packageName) {
        return appDataFromApplicationInfo(getPackageInfoFromPackageName(context, packageName), context.getPackageManager());
    }

    static public AppDataInfo appDataFromApplicationInfo(final PackageInfo pkgInfo,
                                                         final PackageManager pkgManager) {
        return appDataFromApplicationInfo(pkgInfo, pkgManager, new ArrayList<ResolveInfo>());
    }

    static public AppDataInfo appDataFromApplicationInfo (PackageInfo pkgInfo,
                                                          PackageManager pkgManager,
                                                          ResolveInfo resolveInfo) {
        List<ResolveInfo> resolveInfos = new ArrayList<>(1);
        resolveInfos.add(resolveInfo);
        return appDataFromApplicationInfo(pkgInfo, pkgManager, resolveInfos);
    }

    static public AppDataInfo appDataFromApplicationInfo(final PackageInfo pkgInfo,
                                                         final PackageManager pkgManager,
                                                         final List<ResolveInfo> launcherResolveInfoList) {
        return appDataFromApplicationInfo(pkgInfo, pkgManager, launcherResolveInfoList, new AppDataInfo());
    }

    static public AppDataInfo appDataFromApplicationInfo(final PackageInfo pkgInfo,
                                                         final PackageManager pkgManager,
                                                         final List<ResolveInfo> launcherResolveInfoList,
                                                         final AppDataInfo appInfo) {
        if (pkgInfo == null) {
            return null;
        }
        Intent i = ActivityUtil.getLaunchIntentForPackage(pkgManager, pkgInfo, launcherResolveInfoList);
        if (i == null) {
            return null;
        }
        appInfo.packageName = pkgInfo.packageName;
        if (i.getComponent() != null) {
            appInfo.activityClassName = i.getComponent().getClassName();
        }
        appInfo.isEnable = pkgInfo.applicationInfo.enabled;
        appInfo.labelName = pkgInfo.applicationInfo.loadLabel(pkgManager).toString();
        appInfo.lastUpdatedTime = pkgInfo.lastUpdateTime;
        appInfo.isSystemApp = ApplicationUtil.isSystemApp(pkgInfo);
        appInfo.intent = i;
        appInfo.iconDrawable = pkgInfo.applicationInfo.loadIcon(pkgManager);
        appInfo.setType(AppBaseInfo.Type.APP);
        return appInfo;
    }

    static public AppDataInfo appDataFromPackageInfo(final Context context, final PackageInfo packageInfo) {
        AppDataInfo appInfo = null;
        try {
            appInfo = appDataFromApplicationInfo(packageInfo, context.getPackageManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appInfo;
    }

    static public AppDataInfo appDataFromPackageInfo(final Context context, final List<ResolveInfo> apps, final PackageInfo packageInfo) {
        AppDataInfo appInfo = null;
        try {
            appInfo = appDataFromApplicationInfo(packageInfo, context.getPackageManager(), apps);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appInfo;
    }

    static public PackageInfo getPackageInfoFromPackageName(Context context, String packageName) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    static public AppDataInfo getAppDataInfoFromPackageName(Context context, String packageName) {
        PackageInfo pkgInfo = getPackageInfoFromPackageName(context, packageName);
        if (pkgInfo == null) {
            return null;
        }
        AppDataInfo appInfo = new AppDataInfo();
        appInfo.packageName = pkgInfo.packageName;
        appInfo.labelName = pkgInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
        appInfo.lastUpdatedTime = pkgInfo.lastUpdateTime;
        appInfo.isSystemApp = ApplicationUtil.isSystemApp(pkgInfo);
        appInfo.intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        appInfo.iconDrawable = pkgInfo.applicationInfo.loadIcon(context.getPackageManager());
        return appInfo;
    }

    static public void checkCustomIcon(final Context context, Map<String, String> customizedIconAppsMap, AppDataInfo appDataInfo) {
        if (customizedIconAppsMap == null) {
            return;
        }
        if (CollectionUtils.safelyContains(customizedIconAppsMap.keySet(), appDataInfo.packageName)) {
            String iconResourceName = customizedIconAppsMap.get(appDataInfo.packageName);
            appDataInfo.iconDrawable = context.getResources().getDrawable(RawResourceUtil.getDrawableIdByName(context, iconResourceName));
        }
    }

    public static void checkIconByTargetDir(String fileDir, AppDataInfo appDataInfo) {
        if (fileDir == null) {
            return;
        }
        File file = getApkIconFilePath(fileDir, appDataInfo.packageName);
        if (file.exists()) {
            Bitmap bitmap = BitmapUtils.loadBitmapFromFile(file.getAbsolutePath());
            if (bitmap != null) {
                appDataInfo.iconDrawable = new BitmapDrawable(bitmap);
            }
        }
    }

    public static File getOnyxApkIconFilePath(String packageName) {
        return getApkIconFilePath(AppsConstant.ONYX_CUSTOM_ICON_CACHE_DIR, packageName);
    }

    private static File getApkIconFilePath(String fileDir, String packageName) {
        File dir = new File(fileDir);
        if (!dir.exists()) {
            boolean mkdirsResult = dir.mkdirs();
            Debug.i(ApplicationUtil.class, fileDir + " mkdir status :" + mkdirsResult);
        }
        String fileName = packageName.replaceAll("\\.", "_") + ICON_SUFFIX;
        return new File(dir, fileName);
    }

    public static Intent getAppInfoIntent(Context context, String packageName) {
        Intent intent = new Intent(APP_INFO);
        if (CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.O)) {
            intent.setPackage(ANDROID_SETTING_PACKAGE_NAME);
        }
        intent.putExtra(ARGS_PKG, packageName);
        try {
            intent.putExtra(ARGS_UID, context.getPackageManager().
                    getApplicationInfo(packageName, 0).uid);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return intent;
    }

    public static List<String> getAllIMEPkg() {
        List<String> resultList = new ArrayList<>();
        Context context = ResManager.getAppContext();
        if (context == null) {
            throw new IllegalStateException("Init ResManager in your application first");
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> mInputMethodProperties = null;
        if (imm == null) {
            throw new IllegalStateException("Input method service acquired failed");
        }
        mInputMethodProperties = imm.getInputMethodList();
        for (InputMethodInfo imi : mInputMethodProperties) {
            resultList.add(imi.getPackageName());
        }
        return resultList;
    }

    public static List<String> getAllTTSPkg() {
        List<String> resultList = new ArrayList<>();
        Context context = ResManager.getAppContext();
        if (context == null) {
            throw new IllegalStateException("Init ResManager in your application first");
        }
        TextToSpeech tts = new TextToSpeech(context, null);
        List<TextToSpeech.EngineInfo> ttsEngines = tts.getEngines();
        for (TextToSpeech.EngineInfo engineInfo : ttsEngines) {
            resultList.add(engineInfo.name);
        }
        tts.shutdown();
        return resultList;
    }

    public static List<TextToSpeech.EngineInfo> getAllTTSEngine() {
        List<TextToSpeech.EngineInfo> resultList = new ArrayList<>();
        Context context = ResManager.getAppContext();
        if (context == null) {
            throw new IllegalStateException("Init ResManager in your application first");
        }
        TextToSpeech tts = new TextToSpeech(context, null);
        List<TextToSpeech.EngineInfo> ttsEngines = tts.getEngines();
        for (TextToSpeech.EngineInfo engineInfo : ttsEngines) {
            resultList.add(engineInfo);
        }
        tts.shutdown();
        return resultList;
    }

    public static List<String> getAllVPNPkg() {
        List<String> resultList = new ArrayList<>();
        Context context = ResManager.getAppContext();
        if (context == null) {
            throw new IllegalStateException("Init ResManager in your application first");
        }

        PackageManager manager = context.getPackageManager();
        Intent intent = new Intent();
        intent.setAction(VPN_SERVICE_TAG);
        List<ResolveInfo> infoList = manager.queryIntentServices(intent,
                PackageManager.GET_RESOLVED_FILTER);
        for (ResolveInfo info : infoList) {
            ServiceInfo serviceInfo = info.serviceInfo;
            if (serviceInfo != null) {
                resultList.add(serviceInfo.packageName);
            }
        }
        return resultList;
    }

    public static List<ResolveInfo> getLaunchResolveInfoList(PackageManager packageManager) {
        return packageManager.queryIntentActivities(ActivityUtil.buildMainLaunchIntent(),
                PackageManager.GET_DISABLED_COMPONENTS);
    }

    public static AppDataInfo getCustomAppDataInfo(Context context, AppDataInfo appData) {
        if (appData == null || appData.intent != null) {
            return null;
        }
        AppDataInfo packagedAppData = null;
        if (StringUtils.isNotBlank(appData.packageName)) {
            PackageInfo packageInfo = ApplicationUtil.getPackageInfoFromPackageName(context, appData.packageName);
            if (packageInfo != null) {
                appData.lastUpdatedTime = packageInfo.lastUpdateTime;
                packagedAppData = ApplicationUtil.appDataFromPackageInfo(context, packageInfo);
            }
        }
        appData.labelName = appData.getName();
        if (appData.iconDrawable == null) {
            if (StringUtils.isNotBlank(appData.iconDrawableName)) {
                appData.iconDrawable = ResManager.getDrawable(appData.iconDrawableName);
            }
            if (appData.iconDrawable == null && packagedAppData != null) {
                appData.iconDrawable = packagedAppData.iconDrawable;
            }
        }
        if (StringUtils.isNullOrEmpty(appData.activityClassName) && packagedAppData != null) {
            appData.activityClassName = packagedAppData.activityClassName;
        }
        appData.intent = new Intent();
        if (StringUtils.isNotBlank(appData.action)) {
            appData.intent.setAction(appData.action);
        }
        if (StringUtils.isNotBlank(appData.packageName) && StringUtils.isNotBlank(appData.activityClassName)) {
            appData.intent.setComponent(new ComponentName(appData.packageName, appData.activityClassName));
        }
        return appData;
    }

    public static String getAppInfoLaunchName(AppDataInfo appDataInfo) {
        String packageName = appDataInfo.packageName;
        if (appDataInfo.isCustomizedApp) {
            if (StringUtils.isNotBlank(appDataInfo.action)) {
                packageName = appDataInfo.action;
            } else if (StringUtils.isNotBlank(appDataInfo.activityClassName)) {
                packageName = appDataInfo.activityClassName;
            }
        }
        return packageName;
    }

    public static AppWidgetInfo getAppWidgetInfo(Context context, AppWidgetProviderInfo providerInfo) {
        AppWidgetInfo appWidgetInfo = new AppWidgetInfo();
        return getAppWidgetInfo(context, appWidgetInfo, providerInfo);
    }

    public static AppWidgetInfo getAppWidgetInfo(Context context, AppWidgetInfo appWidgetInfo, AppWidgetProviderInfo info) {
        appWidgetInfo.setProviderInfo(info);
        appWidgetInfo.name = info.label;
        appWidgetInfo.appLabelName = PackageUtils.getAppDisplayName(context, info.provider.getPackageName());
        PackageInfo packageInfo = ApplicationUtil.getPackageInfoFromPackageName(context, info.provider.getPackageName());
        if (packageInfo == null) {
            return appWidgetInfo;
        }
        int density = ResManager.getDensityDpi();
        appWidgetInfo.previewDrawable = ResManager.getDrawableForDensity(info.provider.getPackageName(),
                info.previewImage, density);
        appWidgetInfo.iconDrawable = packageInfo.applicationInfo.loadIcon(context.getPackageManager());
        return appWidgetInfo;
    }
}
