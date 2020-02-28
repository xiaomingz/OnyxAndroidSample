package com.onyx.android.sdk.data.dpm;

import android.content.pm.IPackageDeleteObserver;
import android.os.RemoteException;

/**
 * Created by solskjaer49 on 2020/2/24 17:41.
 */
public abstract class PackageDeleteObserver extends IPackageDeleteObserver.Stub {
    private static final String TAG = PackageDeleteObserver.class.getSimpleName();

    public abstract void packageDeleted(String packageName, int returnCode) throws RemoteException;
}
