package com.onyx.android.sdk.utils;

import android.util.SparseArray;

import java.lang.ref.SoftReference;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by TonyXie on 2018/3/31.
 */

public class RxTimerUtil {
    private static SparseArray<SoftReference<TimerObserver>> timerObservers = new SparseArray<>();

    public static int timer(long mills, TimerObserver timerObserver) {
        return timer(mills, TimeUnit.MILLISECONDS, timerObserver);
    }

    public static int timer(long delay, TimeUnit timeUnit, TimerObserver timerObserver) {
        int timerObserverKey = timerObserver.hashCode();
        Observable.timer(delay, timeUnit)
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(timerObserver);
        timerObservers.put(timerObserverKey, new SoftReference<>(timerObserver));
        return timerObserverKey;
    }

    public static int timer(long initialDelay, long period, TimerObserver timerObserver) {
        int timerObserverKey = timerObserver.hashCode();
        Observable.interval(initialDelay, period, TimeUnit.MILLISECONDS)
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(timerObserver);
        timerObservers.put(timerObserverKey, new SoftReference<>(timerObserver));
        return timerObserverKey;
    }

    public static int timerRange(long start, long count, long initialDelay, long period, TimeUnit unit, TimerObserver timerObserver) {
        int timerObserverKey = timerObserver.hashCode();
        Observable.intervalRange(start, count, initialDelay, period, unit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timerObserver);
        timerObservers.put(timerObserverKey, new SoftReference<>(timerObserver));
        return timerObserverKey;
    }

    public static void cancel(TimerObserver timerObserver) {
        if (timerObserver == null) {
            return;
        }
        cancel(timerObserver.hashCode());
    }

    public static void cancel(int timerObserverKey) {
        if (timerObservers == null || timerObservers.size() == 0) {
            return;
        }
        SoftReference<TimerObserver> observerSoftReference = timerObservers.get(timerObserverKey);
        if (observerSoftReference == null) {
            return;
        }
        TimerObserver timerObserver = observerSoftReference.get();
        if (timerObserver != null) {
            timerObserver.cancel();
            timerObservers.remove(timerObserverKey);
        }
    }

    public static void cancelAll() {
        if (timerObservers == null || timerObservers.size() == 0) {
            return;
        }
        SoftReference<TimerObserver> observerSoftReference;
        TimerObserver timerObserver;
        for (int i = 0; i < timerObservers.size(); i++) {
            int key = timerObservers.keyAt(i);
            observerSoftReference = timerObservers.get(key);
            if (observerSoftReference == null) {
                continue;
            }
            timerObserver = observerSoftReference.get();
            if (timerObserver != null) {
                timerObserver.cancel();
                timerObservers.remove(key);
            }
        }
    }

    public abstract static class TimerObserver implements Observer<Long> {
        private Disposable disposable;

        @Override
        public void onSubscribe(Disposable disposable) {
            this.disposable = disposable;
        }

        @Override
        public void onError(Throwable e) {
            cancel();
        }

        @Override
        public void onComplete() {
            cancel();
        }

        public void cancel() {
            if (disposable != null && !disposable.isDisposed()) {
                disposable.dispose();
            }
        }
    }
}
