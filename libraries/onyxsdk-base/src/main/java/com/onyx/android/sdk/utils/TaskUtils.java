package com.onyx.android.sdk.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;

import com.onyx.android.sdk.device.Device;

import java.util.Collections;
import java.util.List;

/**
 * <pre>
 *     author : liao lin tao
 *     time   : 2019/7/4 20:26
 *     desc   :
 * </pre>
 */
public class TaskUtils {
    public static final int WINDOWING_MODE_UNDEFINED = 0;
    public static final int WINDOWING_MODE_FULLSCREEN = 1;
    public static final int WINDOWING_MODE_PINNED = 2;
    public static final int WINDOWING_MODE_SPLIT_SCREEN_PRIMARY = 3;
    public static final int WINDOWING_MODE_SPLIT_SCREEN_SECONDARY = 4;
    public static final int WINDOWING_MODE_FREEFORM = 5;

    public static final int SPLIT_SCREEN_CREATE_MODE_TOP_OR_LEFT = 0;
    public static final int SPLIT_SCREEN_CREATE_MODE_BOTTOM_OR_RIGHT = 1;

    public static final int INVALID_TASK_ID = -1;

    public static List<RunningTaskInfo> getTasks(Context context) {
        return getTasks(context, Integer.MAX_VALUE);
    }

    public static List<RunningTaskInfo> getTasks(Context context, int maxNum) {
        ActivityManager activityManager = getActivityManager(context);
        if (activityManager == null) {
            return Collections.emptyList();
        }
        return activityManager.getRunningTasks(maxNum);
    }

    public static ActivityManager getActivityManager(Context context) {
        return (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    public static boolean isPrimaryWindowingMode(Context context, int taskId) {
        if (!isValidTaskId(taskId)) {
            return false;
        }
        return WINDOWING_MODE_SPLIT_SCREEN_PRIMARY == Device.currentDevice().getTaskWindowingMode(context, taskId);
    }

    public static boolean isSecondaryWindowingMode(Context context, int taskId) {
        if (!isValidTaskId(taskId)) {
            return false;
        }
        return WINDOWING_MODE_SPLIT_SCREEN_SECONDARY == Device.currentDevice().getTaskWindowingMode(context, taskId);
    }

    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager am = getActivityManager(context);
        if (am == null) {
            return false;
        }
        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo info : services) {
            String name = info.service.getClassName();
            if (serviceName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isActivityRunning(Context context, String activity) {
        return getTaskInfo(context, activity) != null;
    }

    @Nullable
    public static RunningTaskInfo getTaskInfo(Context context, String activity) {
        ActivityManager activityManager = getActivityManager(context);
        if (activityManager == null) {
            return null;
        }
        List<RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (RunningTaskInfo task : tasks) {
            if (activity.equalsIgnoreCase(task.topActivity.getClassName()))
                return task;
        }

        return null;
    }

    public static RunningTaskInfo getTopTask(Context context) {
        List<RunningTaskInfo> tasks = getTasks(context, 1);
        if (CollectionUtils.isNullOrEmpty(tasks)) {
            return null;
        }
        return tasks.get(0);
    }

    public static boolean isValidTaskId(int taskId) {
        return taskId != INVALID_TASK_ID;
    }

    public static Rect getTaskBounds(Context context, int taskId) {
        return Device.currentDevice().getTaskBounds(context, taskId);
    }

    public static int getTaskSplitScreenCreateMode(Context context, int taskId) {
        Rect taskBounds = getTaskBounds(context, taskId);
        return getTaskSplitScreenCreateMode(taskBounds);
    }

    public static int getTaskSplitScreenCreateMode(Rect taskBounds) {
        if (RectUtils.isNullOrEmpty(taskBounds)) {
            return SPLIT_SCREEN_CREATE_MODE_TOP_OR_LEFT;
        }
        if (taskBounds.left == 0) {
            return SPLIT_SCREEN_CREATE_MODE_TOP_OR_LEFT;
        }
        return SPLIT_SCREEN_CREATE_MODE_BOTTOM_OR_RIGHT;
    }
}
