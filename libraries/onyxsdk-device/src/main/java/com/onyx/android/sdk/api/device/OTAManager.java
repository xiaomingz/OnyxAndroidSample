package com.onyx.android.sdk.api.device;

import android.content.Context;
import android.content.Intent;

public class OTAManager {

    private static final String OTA_SERVICE_PACKAGE = "com.onyx.android.onyxotaservice";
    private static final String OTA_SERVICE_ACTIVITY = "com.onyx.android.onyxotaservice.OtaInfoActivity";
    private static final String OTA_SERVICE_PACKAGE_PATH_KEY = "updatePath";

    public void startFirmwareUpdate(Context context, String path) {
        Intent i = new Intent();
        i.putExtra(OTA_SERVICE_PACKAGE_PATH_KEY, path);
        i.setClassName(OTA_SERVICE_PACKAGE, OTA_SERVICE_ACTIVITY);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

}
