package com.onyx.android.sdk.rx;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by john on 2/2/2018.
 */

public class MultiThreadScheduler {

    private static Scheduler scheduler;

    public static Scheduler scheduler() {
        if (scheduler == null) {
            scheduler = newScheduler();
        }
        return scheduler;
    }

    public static Scheduler newScheduler() {
        ExecutorService threadPool = Executors.newFixedThreadPool(5);
        return Schedulers.from(threadPool);
    }
}
