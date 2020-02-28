package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.onyx.android.sdk.utils.Debouncer;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by john on 16/8/2019.
 */

public class DebouncerTest extends ApplicationTestCase<Application> {

    public DebouncerTest() {
        super(Application.class);
    }

    public void testDebounce1() throws Exception {
        Debouncer debouncer = new Debouncer();
        int limit = 20;
        int duration = 20;
        final long delay = 100;
        final AtomicLong triggerDuration = new AtomicLong(0);
        final AtomicLong triggerCount = new AtomicLong(0);
        final long start = System.currentTimeMillis();
        final CountDownLatch countDownLatch = new CountDownLatch(limit);
        Log.e("##########", "test case1 start:");
        for(int i = 0; i < limit; ++i) {
            TestUtils.sleep(duration);
            Log.e("##########", "test case1 generate: " + i);
            debouncer.debounceWithDelay(delay, new Runnable() {
                @Override
                public void run() {
                    // should be triggered only once
                    Log.e("##########", "test case1: triggered");
                    triggerDuration.set(System.currentTimeMillis() - start);
                    triggerCount.incrementAndGet();
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await(limit * delay + delay, TimeUnit.MILLISECONDS);
        assertTrue(triggerDuration.get() > limit * duration);
        assertTrue(triggerDuration.get() < Debouncer.FORCE_DELAY_MIN_VALUE + delay);
        assertTrue(triggerCount.get() == 1);
    }


    public void testDebounce2() throws Exception {
        Debouncer debouncer = new Debouncer();
        int limit = 1;
        int duration = 20;
        final long delay = 100;
        final AtomicLong triggerDuration = new AtomicLong(0);
        final AtomicLong triggerCount = new AtomicLong(0);
        final long start = System.currentTimeMillis();
        final CountDownLatch countDownLatch = new CountDownLatch(limit);
        Log.e("##########", "test case2: ");
        for(int i = 0; i < limit; ++i) {
            TestUtils.sleep(duration);
            Log.e("##########", "test case2 generate: " + i);
            debouncer.debounceWithDelay(delay, new Runnable() {
                @Override
                public void run() {
                    // should be triggered only once
                    Log.e("##########", "test case2: triggered");
                    triggerDuration.set(System.currentTimeMillis() - start);
                    triggerCount.incrementAndGet();
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await(2 * delay, TimeUnit.MILLISECONDS);
        assertTrue(triggerDuration.get() > delay);
        assertTrue(triggerDuration.get() < Debouncer.FORCE_DELAY_MIN_VALUE);
        assertTrue(triggerCount.get() == 1);
    }

    // large than
    public void testDebounce3() throws Exception {
        Debouncer debouncer = new Debouncer();
        final int limit = 130;
        int duration = 20;
        final long delay = 100;
        final List<Long> list = new ArrayList<>();
        final AtomicLong triggerCount = new AtomicLong(0);
        final AtomicLong start = new AtomicLong(System.currentTimeMillis());
        final CountDownLatch countDownLatch = new CountDownLatch(limit);
        Log.e("##########", "test case3: ");
        for(int i = 0; i < limit; ++i) {
            TestUtils.sleep(duration);
            Log.e("##########", "test case3 generate: " + i);
            debouncer.debounceWithDelay(delay, new Runnable() {
                @Override
                public void run() {
                    // should be triggered only once
                    Log.e("##########", "test case3: triggered");
                    long duration = System.currentTimeMillis() - start.get();
                    list.add(duration);
                    start.set(System.currentTimeMillis());
                    triggerCount.incrementAndGet();
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await(limit * delay + delay, TimeUnit.MILLISECONDS);
        for(int i = 0; i < list.size(); ++i) {
            if (i != list.size() - 1) {
                assertTrue(list.get(i) > Debouncer.FORCE_DELAY_MIN_VALUE);
            } else {
                assertTrue(list.get(i) < Debouncer.FORCE_DELAY_MIN_VALUE);
            }
        }
        assertTrue(triggerCount.get() == limit * duration / Debouncer.FORCE_DELAY_MIN_VALUE + 1);
    }
}
