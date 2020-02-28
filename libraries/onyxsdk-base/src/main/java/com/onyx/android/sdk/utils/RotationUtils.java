package com.onyx.android.sdk.utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.Surface;

/**
 * <pre>
 *     author : liao lin tao
 *     time   : 2019/6/14 14:54
 *     desc   :
 * </pre>
 */
public class RotationUtils {

    public static final String ACTION_ROTATION = "com.onyx.action.ROTATION";
    public static final String ARGS_ROTATION = "rotation";

    public static final String ARGS_ROTATE_BY = "args_rotate_by";
    public static final int UNKNOWN_ROTATION = 0;
    public static final int ROTATE_BY_USER = 1;
    public static final int ROTATE_BY_APP = 2;

    private static final int INVALID_ROTATION = -1;

    public static void setRequestedOrientation(Activity activity, int orientation, boolean isScreenRotationFollowingSystem, int rotateBy) {
        if (isScreenRotationFollowingSystem) {
            int rotation = convertOrientationToRotation(orientation);
            BroadcastHelper.sendRotationIntent(activity.getApplicationContext(), rotation, rotateBy);
        } else {
            activity.setRequestedOrientation(orientation);
        }
    }

    public static int convertOrientationToRotation(int orientation) {
        switch (orientation) {
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                return Surface.ROTATION_0;
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                return Surface.ROTATION_90;
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                return Surface.ROTATION_180;
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                return Surface.ROTATION_270;
            default:
                return INVALID_ROTATION;
        }
    }

    public static int convertRotationToOrientation(int rotation) {
        switch (rotation) {
            case Surface.ROTATION_0:
                return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            case Surface.ROTATION_90:
                return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            case Surface.ROTATION_180:
                return ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
            case Surface.ROTATION_270:
                return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
            default:
                return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }
    }

    public static boolean isValidRotation(int rotation) {
        return rotation != INVALID_ROTATION;
    }

    public static boolean isRotateByUser(int rotateBy) {
        return rotateBy == ROTATE_BY_USER;
    }
}
