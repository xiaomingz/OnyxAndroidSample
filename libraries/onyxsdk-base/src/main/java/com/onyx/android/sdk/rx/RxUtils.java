package com.onyx.android.sdk.rx;

import android.os.Handler;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by john on 24/9/2018.
 */

public class RxUtils {

    public static void runInIO(final Runnable runnable) {
        run(runnable, Schedulers.io());
    }

    public static void runInComputation(final Runnable runnable) {
        run(runnable, Schedulers.computation());
    }

    public static Disposable runInComputation(final Runnable runnable, Consumer<Object> onNext) {
        return run(runnable, Schedulers.computation(), onNext);
    }

    public static void runInUI(final Runnable runnable) {
        run(runnable, AndroidSchedulers.mainThread());
    }

    public static void postRunInUISafely(final Handler handler, final Runnable runnable) {
        if (handler != null) {
            handler.post(runnable);
            return;
        }
        runInUI(runnable);
    }

    public static void run(final Runnable runnable, Scheduler scheduler) {
        Observable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                if (runnable != null) {
                    runnable.run();
                }
                return this;
            }
        })
                .subscribeOn(scheduler)
                .subscribe();
    }

    public static Disposable run(final Runnable runnable, Scheduler scheduler, Consumer<Object> onNext) {
        return Observable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                if (runnable != null) {
                    runnable.run();
                }
                return this;
            }
        })
                         .subscribeOn(scheduler)
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribe(onNext);
    }


    public static <T extends Object> void runWith(final Callable<T> callable,
                                                  final Consumer<T> consumer,
                                                  Scheduler scheduler) {
        Observable.fromCallable(callable)
                .subscribeOn(scheduler)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);

    }

    public static <T extends Object> void runWithInComputation(final Callable<T> callable,
                                                  final Consumer<T> consumer) {
        Observable.fromCallable(callable)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);

    }

    public static void dispose(Disposable disposable) {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public static boolean isDisposed(Disposable disposable) {
        if (disposable != null) {
            return disposable.isDisposed();
        }
        return true;
    }
}
