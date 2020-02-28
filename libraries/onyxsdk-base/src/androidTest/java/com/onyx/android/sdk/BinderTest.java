package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.rx.RxUtils;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.concurrent.CountDownLatch;

/**
 * Created by john on 15/12/2018.
 */

public class BinderTest extends ApplicationTestCase<Application> {

    private static final String TAG = BinderTest.class.getSimpleName();

    public BinderTest() {
        super(Application.class);
    }

    static final int PEN_STOP = 0;
    static final int PEN_START = 1;
    static final int PEN_DRAWING = 2;
    static final int PEN_PAUSE = 3;
    static final int PEN_ERASING = 4;

    public void testPenState2() throws Exception {
        final CountDownLatch countDownLatch = TestUtils.createCountDownLatch(1);
        RxUtils.runInComputation(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 99999; ++i) {
                    EpdController.setScreenHandWritingPenState(null, PEN_START);
                    TestUtils.sleep(1500);
                    assertTrue(EpdController.isValidPenState());
                    EpdController.setScreenHandWritingPenState(null, PEN_DRAWING);
                    assertTrue(EpdController.isValidPenState());
                    EpdController.setScreenHandWritingPenState(null, PEN_PAUSE);
                    assertTrue(EpdController.isValidPenState());
                    EpdController.setScreenHandWritingPenState(null, PEN_STOP);
                    TestUtils.sleep(500);
                    assertFalse(EpdController.isValidPenState());
                }
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testByPass() {
        for(int i = -1000; i < 1000; ++i) {
            EpdController.byPass(i);
        }
        EpdController.byPass(0);
        Log.e(TAG, "Bypass test finished.");
    }

    public void testOECEnable() {
        for(int i = 0; i < 999999; ++i) {
            Device.currentDevice().isOECEnable();
            if (i % 1000 == 0) {
                Log.e(TAG, "OEC testing: " + i);
            }
        }
        Device.currentDevice().isOECEnable();
        Log.e(TAG, "OEC test finished.");
    }

    public void testPenState1() {
        for(int i = 0; i < 999999; ++i) {
            Device.currentDevice().isValidPenState();
            if (i % 1000 == 0) {
                Log.e(TAG, "PEN state testing: " + i);
            }
        }
        Device.currentDevice().isValidPenState();
        Log.e(TAG, "Pen state test finished.");
    }
}
