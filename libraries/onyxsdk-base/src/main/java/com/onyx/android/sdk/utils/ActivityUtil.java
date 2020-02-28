/**
 * 
 */
package com.onyx.android.sdk.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.onyx.android.sdk.data.AppDataInfo;

import java.util.List;
import java.util.Set;


/**
 * @author joy
 * 
 */
public class ActivityUtil {

    private static final String TAG = ActivityUtil.class.getSimpleName();

    public static boolean startActivitySafely(Context from, Class<?> activityCls) {
        return startActivitySafely(from, new Intent(from, activityCls));
    }

    public static boolean startActivitySafely(Context from, final String packageName) {
        return startActivitySafely(from, from.getPackageManager().getLaunchIntentForPackage(packageName));
    }

    public static boolean startActivitySafely(Context from, final String packageName, final String activityClassName) {
        return startActivitySafely(from, createIntent(packageName, activityClassName));
    }
    
    public static boolean startActivitySafely(Context from, Intent intent) {
        try {
            from.startActivity(intent);
            return true;
        } catch (Throwable tr) {
            Log.e(TAG, "", tr);
        }
        
        return false;
    }

    public static boolean startActivitySafely(Context from, Intent intent, ComponentName componentName) {
        if (componentName != null) {
            intent.setPackage(componentName.getPackageName());
            intent.setClassName(componentName.getPackageName(), componentName.getClassName());
        }
        return startActivitySafely(from, intent);
    }

    public static Intent createIntent(final String packageName, final String activityClassName) {
        Intent intent = new Intent();
        final String className;
        boolean differentPackage = !activityClassName.contains(packageName);
        if (differentPackage) {
            if (activityClassName.startsWith(".")) {
                className = packageName + activityClassName;
            } else {
                className = activityClassName;
            }
        } else {
            className = activityClassName;
        }
        intent.setComponent(new ComponentName(packageName, className));
        return intent;
    }

    public static boolean startActivityForResultSafely(Activity from, Intent intent, int requestCode) {
        try {
            from.startActivityForResult(intent, requestCode);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        return false;
    }

    public static boolean startActivitySafely(Context from, Intent intent,
            ActivityInfo appInfo)
    {
        @SuppressWarnings("unused")
        CharSequence app_name = appInfo.applicationInfo.loadLabel(from
                .getPackageManager());

        try {
            intent.setPackage(appInfo.packageName);
            intent.setClassName(appInfo.packageName, appInfo.name);

            from.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
        } catch (SecurityException e) {
        }

        return false;
    }

    public static boolean startActivitySafely(Context context, AppDataInfo appDataInfo) {
        if (appDataInfo == null) {
            return false;
        }
        if (StringUtils.isNotBlank(appDataInfo.action)) {
            Intent intent = new Intent(appDataInfo.action);
            if (StringUtils.isNotBlank(appDataInfo.packageName) && StringUtils.isNotBlank(appDataInfo.activityClassName)) {
                intent.setComponent(new ComponentName(appDataInfo.packageName, appDataInfo.activityClassName));
            }
            return ActivityUtil.startActivitySafely(context, intent);
        } else {
            if (StringUtils.isNullOrEmpty(appDataInfo.activityClassName)) {
                return ActivityUtil.startActivitySafely(context, appDataInfo.packageName);
            } else {
                return ActivityUtil.startActivitySafely(context, appDataInfo.packageName,
                        appDataInfo.activityClassName);
            }
        }
    }

    public static boolean startActivityForResultSafely(Activity from, Intent intent, ActivityInfo appInfo, int requestCode) {
        @SuppressWarnings("unused")
        CharSequence app_name = appInfo.applicationInfo.loadLabel(from.getPackageManager());
        try {
            intent.setPackage(appInfo.packageName);
            intent.setClassName(appInfo.packageName, appInfo.name);
            from.startActivityForResult(intent, requestCode);
            return true;
        } catch (ActivityNotFoundException e) {
        } catch (SecurityException e) {
        }
        return false;
    }

    public static boolean startUninstallAppForResultSafely(Context context, String packageName) {
        Uri uri = Uri.fromParts("package", packageName, null);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        return ActivityUtil.startActivitySafely(context, intent);
    }

    public static Intent getLaunchIntentForPackage(PackageManager pm, PackageInfo packageInfo, List<ResolveInfo> launchResolveInfoList) {
        Intent intent = pm.getLaunchIntentForPackage(packageInfo.packageName);
        if (intent == null) {
            ActivityInfo activityInfo = getActivityInfoFromResolveInfoList(packageInfo.packageName, launchResolveInfoList);
            if (activityInfo != null && activityInfo.exported) {
                if (activityInfo.isEnabled() || !packageInfo.applicationInfo.enabled) {
                    intent = new Intent();
                    intent.setComponent(new ComponentName(activityInfo.packageName,
                            TextUtils.isEmpty(activityInfo.targetActivity) ? activityInfo.name : activityInfo.targetActivity));
                }
            }
        } else if (ApplicationUtil.isSystemApp(packageInfo)) {
            Set<String> intentCategory = intent.getCategories();
            if (intentCategory != null && !intentCategory.isEmpty()) {
                return intentCategory.contains(Intent.CATEGORY_LAUNCHER) ? intent : null;
            }
        }
        return intent;
    }

    @Nullable
    private static ActivityInfo getActivityInfoFromResolveInfoList(String packageName, List<ResolveInfo> launchResolveInfoList) {
        if (CollectionUtils.isNullOrEmpty(launchResolveInfoList)) {
            return null;
        }
        for (ResolveInfo info : launchResolveInfoList) {
            if (info.activityInfo.packageName.equalsIgnoreCase(packageName)) {
                return info.activityInfo;
            }
        }
        return null;
    }

    @Nullable
    public static ActivityInfo getActivityInfoFromPackageName(PackageManager pm, String packageName) {
        try {
            ActivityInfo[] activities = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).activities;
            return activities == null ? null : activities[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Help Preference Screen To Enable Back Function on Action Bar.
     * Sets up the action bar for an {@link PreferenceScreen}
     * Use this method in onPreferenceTreeClick();
     */
    public static void enableActionBarBackFunc(PreferenceScreen preferenceScreen) {
        final Dialog dialog = preferenceScreen.getDialog();

        if (dialog != null) {
            // Initialize the action bar
            dialog.getActionBar().setHomeButtonEnabled(true);
            dialog.getActionBar().setDisplayHomeAsUpEnabled(true);

            // Apply custom home button area click listener to close the PreferenceScreen because PreferenceScreens are dialogs which swallow
            // events instead of passing to the activity
            // Related Issue: https://code.google.com/p/android/issues/detail?id=4611
            View homeBtn = dialog.findViewById(android.R.id.home);

            if (homeBtn != null) {
                View.OnClickListener dismissDialogClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                };

                // Prepare yourselves for some hacky programming
                ViewParent homeBtnContainer = homeBtn.getParent();
                // The home button is an ImageView inside a FrameLayout
                if (homeBtnContainer instanceof FrameLayout) {
                    ViewGroup containerParent = (ViewGroup) homeBtnContainer.getParent();

                    if (containerParent instanceof LinearLayout) {
                        // This view also contains the title text, set the whole view as clickable
                        ((LinearLayout) containerParent).setOnClickListener(dismissDialogClickListener);
                    } else {
                        // Just set it on the home button
                        ((FrameLayout) homeBtnContainer).setOnClickListener(dismissDialogClickListener);
                    }
                } else {
                    // The 'If all else fails' default case
                    homeBtn.setOnClickListener(dismissDialogClickListener);
                }
            }
        }
    }

    /**
     * Help Preference Activity To Enable Back Function on Action Bar.
     * Sets up the action bar for an {@link PreferenceActivity}
     * Use this method in onResume();
     */
    public static void enableActionBarBackFunc(final PreferenceActivity activity) {
        View homeBtn = activity.findViewById(android.R.id.home);
        if (homeBtn != null) {
            View.OnClickListener backPressedClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onBackPressed();
                }
            };

            // Prepare yourselves for some hacky programming
            ViewParent homeBtnContainer = homeBtn.getParent();

            // The home button is an ImageView inside a FrameLayout
            if (homeBtnContainer instanceof FrameLayout) {
                ViewGroup containerParent = (ViewGroup) homeBtnContainer.getParent();
                for (int i = 0; i < ((FrameLayout) homeBtnContainer).getChildCount(); i++) {
                    ((FrameLayout) homeBtnContainer).getChildAt(i).setVisibility(View.VISIBLE);
                }

                if (containerParent instanceof LinearLayout) {
                    // This view also contains the title text, set the whole view as clickable
                    containerParent.setEnabled(true);
                    containerParent.setClickable(true);
                    ((LinearLayout) containerParent).setOnClickListener(backPressedClickListener);
                } else {
                    // Just set it on the home button
                    ((FrameLayout) homeBtnContainer).setEnabled(true);
                    ((FrameLayout) homeBtnContainer).setClickable(true);
                    ((FrameLayout) homeBtnContainer).setOnClickListener(backPressedClickListener);
                }
            } else {
                // The 'If all else fails' default case
                homeBtn.setClickable(true);
                homeBtn.setEnabled(true);
                homeBtn.setOnClickListener(backPressedClickListener);
            }
        }
    }

    public static Intent buildMainLaunchIntent() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return intent;
    }
}
