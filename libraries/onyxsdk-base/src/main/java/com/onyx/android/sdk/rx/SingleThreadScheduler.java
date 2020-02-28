package com.onyx.android.sdk.rx;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhuzeng on 01/02/2018.
 */

public class SingleThreadScheduler {
    private static Scheduler scheduler;

    public static Scheduler scheduler() {
        if (scheduler == null) {
            scheduler = newScheduler();
        }
        return scheduler;
    }

    public static Scheduler newScheduler() {
        return Schedulers.from(Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setPriority(Thread.MAX_PRIORITY);
                return t;
            }
        }));
    }
}

