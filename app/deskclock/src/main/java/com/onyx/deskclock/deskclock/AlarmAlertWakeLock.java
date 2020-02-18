/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.onyx.deskclock.deskclock;

import android.content.Context;
import android.os.PowerManager;

/**
 * Utility class to hold wake lock in app.
 */
public class AlarmAlertWakeLock {
    private static PowerManager.WakeLock sCpuWakeLock;
    /*2 minutes*/
    private static int WAKELOCK_TIMEOUT = 2 * 60 * 1000;

    public static PowerManager.WakeLock createPartialWakeLock(Context context) {
        return createOnyxDeskClockWakeLock(context);
    }

    public static void acquireCpuWakeLock(Context context) {
        if (sCpuWakeLock != null) {
            return;
        }

        sCpuWakeLock = createPartialWakeLock(context);
        sCpuWakeLock.acquire(WAKELOCK_TIMEOUT);
    }

    public static void acquireScreenCpuWakeLock(Context context) {
        if (sCpuWakeLock != null) {
            return;
        }
        sCpuWakeLock = createOnyxDeskClockWakeLock(context);
        sCpuWakeLock.acquire(WAKELOCK_TIMEOUT);
    }

    private static PowerManager.WakeLock createOnyxDeskClockWakeLock(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "onyx_workLowPower");
    }

    public static void releaseCpuLock() {
        if (sCpuWakeLock != null) {
            if (sCpuWakeLock.isHeld()) {
                sCpuWakeLock.release();
            }
            sCpuWakeLock = null;
        }
    }
}
