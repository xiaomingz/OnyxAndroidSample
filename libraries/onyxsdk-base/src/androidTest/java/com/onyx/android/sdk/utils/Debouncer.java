package com.onyx.android.sdk.utils;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
* Created by john on 16/8/2019.
*/

public class Debouncer {

    private static final String TAG = Debouncer.class.getSimpleName();

    private ScheduledExecutorService executor;
    private AtomicLong triggeredTimestamp = new AtomicLong(0);
    private AtomicLong latestSubmitTimestamp = new AtomicLong(0);


    //min delay value, 200*3. unit:ms
    public static final int FORCE_DELAY_MIN_VALUE = 600;
    //max delay value, 400*3. unit:ms
    public static final int FORCE_DELAY_MAX_VALUE = 1200;
    //debounce timeout multiplier.
    public static final int DEBOUNCE_TIMEOUT_MULTIPLIER = 3;

    public Debouncer() {
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }


    public static int getForceDelayByDebounceTimeout(int debounceTimeout) {
        return Math.min(Math.max(debounceTimeout * DEBOUNCE_TIMEOUT_MULTIPLIER,
                FORCE_DELAY_MIN_VALUE), FORCE_DELAY_MAX_VALUE);
    }

    public void debounceWithDelay(long shortDelay, final Runnable task) {
        long ts = System.currentTimeMillis();
        long longDelay = getForceDelayByDebounceTimeout((int) shortDelay);
        latestSubmitTimestamp.set(ts);
        executor.schedule(myShortRunnable(ts, shortDelay, task), shortDelay,  TimeUnit.MILLISECONDS);
        executor.schedule(myLongRunnable(ts, longDelay, task),  longDelay, TimeUnit.MILLISECONDS);
    }

    private Runnable myShortRunnable(final long submitTimestamp, final long shortDelay, final Runnable task) {
        return new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                if (now - Debouncer.this.latestSubmitTimestamp.get() >= shortDelay) {
                    triggeredTimestamp.set(System.currentTimeMillis());
                    task.run();
                }
            }
        };
    }

    private Runnable myLongRunnable(final long submitTimestamp, final long longDelay, final Runnable task) {
        return new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                if (now - submitTimestamp >= longDelay && now - triggeredTimestamp.get() > longDelay) {
                    triggeredTimestamp.set(System.currentTimeMillis());
                    task.run();
                }
            }
        };
    }

}
