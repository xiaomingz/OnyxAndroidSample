package com.onyx.android.sdk.utils;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import static com.onyx.android.sdk.utils.RotationUtils.ACTION_ROTATION;
import static com.onyx.android.sdk.utils.RotationUtils.ARGS_ROTATE_BY;
import static com.onyx.android.sdk.utils.RotationUtils.ARGS_ROTATION;

/**
 * <pre>
 *     author : liao lin tao
 *     time   : 2018/12/21 10:35
 *     desc   :
 * </pre>
 */
public class BroadcastHelper {
    private static final String OEC_CONFIG_CHANGE_ACTION = "oec.config.change";
    private static final String ARGS_IS_INSTANT_UPDATE = "args_is_instant_update";
    private static final String ARGS_OPERATION_FLAG = "args_operation_flag";

    public static final String EAC_CLOUD_RESULT_ACTION = "com.onyx.floatingbutton.setting.eac.cloud_result";
    public static final String FLOAT_BUTTON_TOUCHE_ACTION = "com.onyx.floatingbutton.touch";
    public static final String RECEIVER_INTENT_DELETE_ACTION = "com.onyx.intent.action.delete";
    public static final String SCREENSHOTS_SHOW_UI_KEY = "show_ui";
    public static final String SCREENSHOTS_ACTION = "onyx.android.intent.screenshot";
    public static final String REBOOT_ACTION = "onyx.android.intent.reboot";
    public static final String SHUTDOWN_ACTION = "onyx.android.intent.shutdown";
    public static final String SYSTEM_UI_SCREEN_SHOT_END_ACTION = "com.android.systemui.SYSTEM_UI_SCREEN_SHOT_END_ACTION";
    public static final String EAC_BRIGHTNESS_ACTION = "action.show.brightness.dialog";
    public static final String EAC_RECENT_SCREEN_ACTION = "toggle_recent_screen";
    public static final String SHOW_GLOBAL_DIALOG_ACTION = "onyx.android.show.global.dialog";
    public static final String FLOAT_OPEN_APP_ACTION = "com.onyx.floatbutton.open.apps";
    public static final String FLOAT_SHOW_SYSTEM_STATUS_BAR_ACTION = "com.onyx.floatbutton.show.system.status.bar";
    public static final String FLOAT_TASK_CLEANUP_ACTION = "com.onyx.recent.task.cleanup";
    public static final String FLOAT_OPEN_APP_FREEZE_ACTION = "com.onyx.app.freeze.page";
    public static final String RELOAD_APP_ICON_ACTION = "reload.app.icon";
    public static final String FLOAT_ENABLE_ACTION = "com.onyx.floatingbutton.enable";

    public final static String BRIGHTNESS_VALUE_CHANGE_ACTION = "brightness_value_change_action";
    public final static String REFRESH_MODE_CHANGED_ACTION = "refresh_mode_changed";

    public final static String SET_DEVICE_OWNER_ACTION = "com.onyx.action.DPM_SET_OWNER";

    public static final String KEY_NAME = "name";
    public static final String KEY_JSON = "json";
    public static final String KEY_JSON_FILE_PATH = "json_file_path";

    public static final String ARGS_EAC_USER_UPDATE_DATA_TYPE_KEY = "args_eac_user_update_data_type";
    public static final String FLOAT_BUTTON_STATUS = "floatbutton_status";

    public static final String ACTIVITY_RESUME_ACTION = "action.activity.resume";
    public static final String ACTIVITY_PAUSE_ACTION = "action.activity.pause";
    public static final String ACTIVITY_FINISH_ACTION = "action.activity.finish";
    public static final String VIEW_EPD_UPDATE_ACTION = "action.view.epd.update";
    public static final String ACTION_EAC_CONFIG = "action.eac.config";
    public static final String INPUT_EVENT_ACTION = "action.input.event";
    public static final String SF_DEBUG_ACTION = "action.sf.debug";

    public static final String EAC_ITEM_CHANGED_ACTION = "com.onyx.floatingbutton.setting.eac.item.changed";
    public static final String EAC_RESET_ACTION = "com.onyx.floatingbutton.setting.eac.reset";
    public static final String EAC_CLOSE_ACTION = "com.onyx.floatingbutton.setting.eac.close";
    public static final String EAC_CLOUD_ACTION = "com.onyx.floatingbutton.setting.eac.cloud";
    public static final String FLOAT_BUTTON_START_APPLICATION_ACTION = "com.onyx.floatbutton.open.apps";
    public static final String EAC_TOP_COMPONENT_CHANGE_ACTION = "onyx.action.top.component.change";

    public static final String ONYX_SYSTEM_CONFIG_CHANGED = "com.onyx.action.system.config.changed";
    public static final String ONYX_LAUNCHER_BADGE_ACTION = "com.onyx.action.LAUNCHER_BADGE";
    public static final String ONYX_SILENT_UNINSTALL_ACTION = "com.onyx.action.ACTION_SILENT_UNINSTALL";
    public static final String ONYX_SETTING_ACTION = "com.onyx.action.SETTING";

    public static final String ARGS_DEBUG = "args_debug";
    public static final String ARGS_ENABLE = "args_enable";
    public static final String ARGS_PKG = "args_pkg";
    public static final String ARGS_CLASS = "args_class";
    public static final String ARGS_DRAW_COUNT = "args_draw_count";
    public static final String ARGS_TARGET_MODE = "args_target_mode";
    public static final String ARGS_VIEW_TYPE = "args_view_type";
    public static final String ARGS_STATUS = "args_status";
    public static final String ARGS_CODE = "args_code";
    public static final String ARGS_EAC_ENABLE = "args_eac_enable";

    public static final String ARGS_EVENT_TYPE = "args_event_type";
    public static final String ARGS_KEY_CODE = "args_key_code";
    public static final String ARGS_EVENT_ACTION = "args_event_action";

    public static final String ARGS_CLOSED_WHEN_CONNECTED = "args_closed_when_connected";
    public static final String ARGS_ENABLE_WHEN_STARTED= "args_enable_when_started";
    public static final String ARGS_EAC_KEY = "eacArgsKey";
    public static final String ARG_ENABLE_SIDE_BAR_POSITION = "enable_sidebar_position";

    public static final String ARGS_BADGE_COUNT = "args_badge_count";
    public static final String ARGS_UNINSTALL_FOR_ALL_USER = "args_uninstall_for_all_user";
    public static final String ARGS_OWNER_COMPONENT = "args_owner_component";

    public final static String CLEAR_ALL_NOTIFICATIONS_REQUEST = "action.clear.all.notifications";

    public class Settings{
        public static final String SETTINGS_MAIN = "onyx.settings.action.main";
        public static final String SETTINGS_WIFI_ACTION = "onyx.settings.action.wifi";
        public static final String SETTINGS_BLUETOOTH_ACTION = "onyx.settings.action.bluetooth";
        public static final String SETTINGS_LANGUAGE_ACTION = "onyx.settings.action.language";
        public static final String SETTINGS_DATE_TIME_ACTION = "onyx.settings.action.datetime";
        public static final String SETTINGS_POWER_ACTION = "onyx.settings.action.power";
        public static final String SETTINGS_NETWORK_ACTION = "onyx.settings.action.network";
        public static final String SETTINGS_LIBRARY_ACTION = "onyx.settings.action.library";
        public static final String SETTINGS_APPLICATION_ACTION = "onyx.settings.action.application";
        public static final String SETTINGS_ACCOUNT_ACTION = "onyx.settings.action.account";
        public static final String SETTINGS_FIRMWARE_ACTION = "onyx.settings.action.firmware";
        public static final String SETTINGS_FEEDBACK_ACTION = "onyx.settings.action.feedback";
        public static final String SETTINGS_CHILD_MODE_ACTION = "onyx.settings.action.child_mode";
        public static final String SETTINGS_CONFIGURE_NEW_WIFI_ACTION = "com.android.systemui.CONFIGURE_NEW_WIFI_ACTION";
        public static final String SETTINGS_ACTION_VIEW_DOWNLOADS = "android.intent.action.VIEW_DOWNLOADS";
        public static final String SETTINGS_ACTION_SYSTEM_PASSWORD_SETTING = "onyx.settings.action.SYSTEM_PASSWORD_SETTING";
        public static final String SETTINGS_ACTION_NAVIGATION_BAR_SETTING = "onyx.settings.action.NAVIGATION_BAR_SETTING";
        public static final String SETTINGS_ACTION_SCREENSAVER_SETTING = "onyx.settings.action.SCREENSAVER_SETTING";
        public static final String SETTINGS_ACTION_DREAM_STYLE_SETTING = "onyx.settings.action.DREAM_STYLE_SETTING";
        public static final String SETTINGS_ACTION_OTHER_DREAM_SETTING_GUIDE = "onyx.settings.action.OTHER_SCREENSAVER_SETTING_GUIDE";
        public static final String STRING_ABOUT_DEVICE_ACTION = "onyx.settings.action.ABOUT";
    }

    public static class ReaderConfig {
        public static final String ACTION_PRE_START_ACTIVITY = "com.onyx.kreader.action.PRE_START_ACTIVITY";

        public static final String ARGS_TOP_ACTIVITY_TASK_ID = "top_activity_task_id";
    }

    public static class MultiWindowConfig {
        public static final String ACTION_START_MULTI_WINDOW = "com.onyx.action.START_MULTI_WINDOW";
        public static final String ACTION_QUIT_MULTI_WINDOW = "com.onyx.action.QUIT_MULTI_WINDOW";
        public static final String ACTION_QUIT_NOTE = "com.onyx.action.QUIT_NOTE";
        public static final String ACTION_SWITCH_LEFT_RIGHT_WINDOW = "com.onyx.action.SWITCH_LEFT_RIGHT_WINDOW";

        public static final String ARGS_PRIMARY_TASK_ID = "primary_task_id";
        public static final String ARGS_PRIMARY_TASK_TO_TOP = "args_primary_task_to_top";
    }

    public static void ensureRegisterReceiver(@NonNull Context context, @NonNull BroadcastReceiver receiver, @NonNull List<String> actions) {
        try {
            if (actions.size() <= 0) {
                return;
            }
            IntentFilter intentFilter = new IntentFilter();
            for (String action : actions) {
                intentFilter.addAction(action);
            }
            context.getApplicationContext().registerReceiver(receiver, intentFilter);
        } catch (Exception ignored) {
        }
    }

    public static void ensureRegisterReceiver(@NonNull Context context, @NonNull BroadcastReceiver receiver, @NonNull IntentFilter intentFilter) {
        try {
            context.getApplicationContext().registerReceiver(receiver, intentFilter);
        } catch (Exception ignored) {
        }
    }

    public static void ensureUnregisterReceiver(@NonNull Context context, @NonNull BroadcastReceiver receiver) {
        try {
            context.getApplicationContext().unregisterReceiver(receiver);
        } catch (Exception ignored) {
        }
    }

    public static void sendOECConfigChanged(Context context, String pkg) {
        sendOECConfigChanged(context, pkg, null, false);
    }

    public static void sendOECConfigChanged(Context context, String pkg, String cls, boolean isInstantUpdate) {
        sendOECConfigChanged(context, pkg, cls, isInstantUpdate, 0);
    }

    public static void sendOECConfigChanged(Context context, String pkg, String cls, boolean isInstantUpdate, int operationFlag) {
        Intent intent = new Intent(OEC_CONFIG_CHANGE_ACTION);
        intent.putExtra(ARGS_PKG, pkg);
        if (!TextUtils.isEmpty(cls)) {
            intent.putExtra(ARGS_CLASS, cls);
        }
        intent.putExtra(ARGS_IS_INSTANT_UPDATE, isInstantUpdate);
        intent.putExtra(ARGS_OPERATION_FLAG, operationFlag);
        context.getApplicationContext().sendBroadcast(intent);
    }

    public static void sendBroadcast(Context context, String action) {
        sendBroadcast(context, new Intent(action));
    }

    public static void sendBroadcast(Context context, Intent intent) {
        context.getApplicationContext().sendBroadcast(addFlagsForAndroidO(intent));
    }

    public static void sendDeleteIntent(Context context, List<String> filePathList) {
        Intent intent = new Intent();
        intent.setAction(RECEIVER_INTENT_DELETE_ACTION);
        intent.putExtra(KEY_NAME, JSON.toJSONString(filePathList));
        context.sendBroadcast(intent);
    }

    public static void sendDeleteIntent(Context context, String filePath) {
        ArrayList<String> list = new ArrayList<>();
        list.add(filePath);
        sendDeleteIntent(context, list);
    }

    public static void sendPreStartReaderActivityIntent(Context context) {
        ActivityManager.RunningTaskInfo topTask = TaskUtils.getTopTask(context);
        if (topTask == null) {
            Debug.d("topTask is null, sendPreStartReaderActivityIntent fail. ");
            return;
        }
        Intent intent = new Intent();
        intent.setAction(ReaderConfig.ACTION_PRE_START_ACTIVITY);
        intent.putExtra(ReaderConfig.ARGS_TOP_ACTIVITY_TASK_ID, topTask.id);
        sendBroadcast(context, intent);
    }

    /**
     *
     * @param rotation {@link android.view.Surface#ROTATION_0}
     */
    public static void sendRotationIntent(Context context, int rotation, int rotateBy) {
        Intent intent = new Intent();
        intent.setAction(ACTION_ROTATION);
        intent.putExtra(ARGS_ROTATION, rotation);
        intent.putExtra(ARGS_ROTATE_BY, rotateBy);
        sendBroadcast(context, intent);
    }

    public static void sendStartMultiWindowIntent(Context context, int primaryTaskId) {
        Intent intent = new Intent();
        intent.setAction(MultiWindowConfig.ACTION_START_MULTI_WINDOW);
        intent.putExtra(MultiWindowConfig.ARGS_PRIMARY_TASK_ID, primaryTaskId);
        sendBroadcast(context, intent);
    }

    public static void sendQuitMultiWindowIntent(Context context, boolean primaryTaskToTop) {
        Intent intent = new Intent();
        intent.setAction(MultiWindowConfig.ACTION_QUIT_MULTI_WINDOW);
        intent.putExtra(MultiWindowConfig.ARGS_PRIMARY_TASK_TO_TOP, primaryTaskToTop);
        sendBroadcast(context, intent);
    }

    public static void sendBatchAppUninstallIntent(Context context, ArrayList<String> packageList) {
        Intent intent = new Intent(ONYX_SILENT_UNINSTALL_ACTION);
        intent.putStringArrayListExtra(ARGS_PKG, packageList);
        intent.setPackage("com.android.packageinstaller");
        context.sendBroadcast(intent);
    }

    public static void sendLauncherBadgeIntent(Context context, String launchName, int count) {
        Intent intent = new Intent(ONYX_LAUNCHER_BADGE_ACTION);
        intent.putExtra(ARGS_PKG, launchName);
        intent.putExtra(ARGS_BADGE_COUNT, count);
        context.sendBroadcast(intent);
    }

    public static Intent addFlagsForAndroidO(Intent intent) {
        if (!CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.O)) {
            return intent;
        }
        Object includeBackground = ReflectUtil.getStaticFieldSafely(Intent.class, "FLAG_RECEIVER_INCLUDE_BACKGROUND");
        if (includeBackground instanceof Integer) {
            intent.addFlags((int) includeBackground);
        }
        return intent;
    }

    public static void sendClearAllNotifications(Context context) {
        Intent intent = new Intent();
        intent.setAction(CLEAR_ALL_NOTIFICATIONS_REQUEST);
        sendBroadcast(context, intent);
    }

    public static void sendSetOwner(Context context, ComponentName adminComponent) {
        Intent intent = new Intent();
        intent.setAction(SET_DEVICE_OWNER_ACTION);
        intent.putExtra(ARGS_OWNER_COMPONENT, adminComponent);
        context.sendBroadcast(intent);
    }
}
