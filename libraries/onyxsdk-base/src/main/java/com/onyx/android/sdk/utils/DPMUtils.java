package com.onyx.android.sdk.utils;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PersistableBundle;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.onyx.android.sdk.data.UserInfo;
import com.onyx.android.sdk.data.dpm.PackageDeleteObserver;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by solskjaer49 on 2020/2/20 15:47.
 */
public class DPMUtils {
    private static final String TAG = DPMUtils.class.getSimpleName();
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static Class<?> userManagerClass = UserManager.class;
    private static Class<?> userInfoClass = ReflectUtil.classForName("android.content.pm.UserInfo");

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static Class<?> packageInstallerClass = PackageInstaller.class;
    private static Class<?> packageManagerClass = PackageManager.class;
    private static Class<?> devicePolicyManagerClass = DevicePolicyManager.class;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static Method sMethodGetUsers = ReflectUtil.getMethodSafely(userManagerClass, "getUsers");

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static Method sMethodCreateSession = ReflectUtil.getMethodSafely(packageInstallerClass, "createSession",
            PackageInstaller.SessionParams.class, int.class);

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static Method sMethodGetInstalledPackageAsUser = ReflectUtil.getMethodSafely(packageManagerClass, "getInstalledPackagesAsUser",
            int.class, int.class);

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static Method sMethodDeletePackageAsUser = ReflectUtil.getMethodSafely(packageManagerClass, "deletePackageAsUser",
            String.class, IPackageDeleteObserver.class, int.class, int.class);

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static Method sMethodStartUserInBackground = ReflectUtil.getMethodSafely(devicePolicyManagerClass, "startUserInBackground",
            ComponentName.class, UserHandle.class);

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static UserManager getUserManager(Context context) {
        return (UserManager) context.getSystemService(Context.USER_SERVICE);
    }

    public static DevicePolicyManager getDevicePolicyManager(Context context) {
        return (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static List getAllUsers(Context context) {
        Object result = ReflectUtil.invokeMethodSafely(sMethodGetUsers, getUserManager(context));
        if (result instanceof List) {
            return (List) result;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static List<String> getAllUserName(Context context) {
        List<String> nameList = new ArrayList<>();
        List userLists = getAllUsers(context);
        if (CollectionUtils.isNonBlank(userLists)) {
            for (Object object : userLists) {
                nameList.add(ReflectUtil.getDeclareStringFieldSafely(userInfoClass, object, "name"));
            }
        }
        return nameList;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static List<Integer> getAllUserID(Context context) {
        List<Integer> idList = new ArrayList<>();
        List userLists = getAllUsers(context);
        if (CollectionUtils.isNonBlank(userLists)) {
            for (Object object : userLists) {
                idList.add(ReflectUtil.getDeclareIntFieldSafely(userInfoClass, object, "id"));
            }
        }
        return idList;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static List<Integer> getAllUserSerialNumber(Context context) {
        List<Integer> serialNumberList = new ArrayList<>();
        List userLists = getAllUsers(context);
        if (CollectionUtils.isNonBlank(userLists)) {
            for (Object object : userLists) {
                serialNumberList.add(ReflectUtil.getDeclareIntFieldSafely(userInfoClass, object, "serialNumber"));
            }
        }
        return serialNumberList;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static int createSessionForTargetUser(PackageInstaller packageInstaller,
                                                 PackageInstaller.SessionParams sessionParams, int userID) {
        int sessionId = -1;
        Object result = ReflectUtil.invokeMethodSafely(sMethodCreateSession, packageInstaller, sessionParams, userID);
        if (result instanceof Integer) {
            sessionId = (int) result;
        }
        return sessionId;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getIdForUser(Context context, UserHandle userHandle) {
        List<UserInfo> list = getUserInfoList(context);
        int serialNumber = getSerialNumberForUser(context, userHandle);
        for (UserInfo userInfo : list) {
            if (userInfo.serialNumber == serialNumber) {
                return userInfo.userId;
            }
        }
        return serialNumber;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static UserHandle getUserForId(Context context, int userId) {
        return UserHandle.getUserHandleForUid(userId);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getSerialNumberForUser(Context context, UserHandle userHandle) {
        return (int) getUserManager(context).getSerialNumberForUser(userHandle);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static UserHandle getUserForSerialNumber(Context context, long serialNumber) {
        return getUserManager(context).getUserForSerialNumber(serialNumber);
    }

    public static boolean isDeviceOwner(Context context) {
        if (CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.LOLLIPOP)) {
            if (!getDevicePolicyManager(context).isDeviceOwnerApp(context.getPackageName())) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    public static UserHandle createUser(@NonNull Context context,
                                        @NonNull String name,
                                        @NonNull ComponentName adminComponent) {
        if (CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.N)) {
            try {
                PersistableBundle adminExtras = new PersistableBundle();
                return getDevicePolicyManager(context).createAndManageUser(
                        adminComponent,
                        name,
                        adminComponent,
                        adminExtras,
                        0x0011);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Deprecated
    public static boolean startUser(Context context, int userId) {
        String command = "am start-user " + userId;
        ShellUtils.CommandResult result = ShellUtils.execCommand(command, false);
        boolean success = result.result == 0;
        if (!success) {
            Debug.e(TAG, StringUtils.isNotBlank(result.errorMsg) ? result.errorMsg : result.successMsg);
        }
        return success;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void startUserInBackground(Context context, ComponentName admin, int userId) {
        ReflectUtil.invokeMethodSafely(sMethodStartUserInBackground, getDevicePolicyManager(context), admin, getUserForId(context, userId));
    }

    public static boolean switchUser(@NonNull Context context, long serialNum,
                                     @NonNull ComponentName adminComponent) {
        if (CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.N)) {
            try {
                return getDevicePolicyManager(context).switchUser(adminComponent,
                        getUserForSerialNumber(context, serialNum));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public static boolean switchUser(@NonNull Context context, UserHandle userHandle,
                                     @NonNull ComponentName adminComponent) {
        if (CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.N)) {
            try {
                return getDevicePolicyManager(context).switchUser(adminComponent, userHandle);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isMasterUser(Context context) {
        return getUserManager(context).getSerialNumberForUser(Process.myUserHandle()) != 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static List<UserInfo> getUserInfoList(Context context) {
        List<UserInfo> list = new ArrayList<>();
        List userLists = getAllUsers(context);
        if (CollectionUtils.isNonBlank(userLists)) {
            for (Object object : userLists) {
                list.add(new UserInfo(ReflectUtil.getDeclareIntFieldSafely(userInfoClass, object, "id")
                        , ReflectUtil.getDeclareStringFieldSafely(userInfoClass, object, "name"),
                        ReflectUtil.getDeclareIntFieldSafely(userInfoClass, object, "serialNumber")));
            }
        }
        return list;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static List<PackageInfo> getInstalledPackageAsUser(Context context, int flag, int userID) {
        Object result = ReflectUtil.invokeMethodSafely(sMethodGetInstalledPackageAsUser, context.getPackageManager(), flag, userID);
        if (result instanceof List) {
            return (List<PackageInfo>) result;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void deletePackageAsUser(Context context, String packageName, PackageDeleteObserver packageDeleteObserver, int flag, int userID) {
        ReflectUtil.invokeMethodSafely(sMethodDeletePackageAsUser, context.getPackageManager(), packageName, packageDeleteObserver, flag, userID);
    }
}
