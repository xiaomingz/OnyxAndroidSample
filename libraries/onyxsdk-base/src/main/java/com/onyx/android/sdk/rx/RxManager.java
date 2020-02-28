package com.onyx.android.sdk.rx;

import android.content.Context;
import android.support.annotation.NonNull;

import com.onyx.android.sdk.common.request.WakeLockHolder;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.ResManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function3;

/**
 * <pre>
 *     author : liao lin tao
 *     time   : 2018/1/4 10:46
 *     desc   :
 * </pre>
 */

public final class RxManager {

    private static final String ONYX_ACTION_PREFIX = "onyx_";
    private static final String RX_MANAGER_ACTION = ONYX_ACTION_PREFIX + RxManager.class.getSimpleName();

    private WakeLockHolder wakeLockHolder = new WakeLockHolder();
    private Benchmark benchmark;
    private volatile Context appContext;
    private boolean enableBenchmarkDebug = false;
    private boolean useWakelock = true;
    private Scheduler subscribeOn;
    private Scheduler observeOn;

    private List<Observable<? extends RxRequest>> observables = new ArrayList<>();

    RxManager(Context context, Scheduler subscribeOn, Scheduler observeOn) {
        appContext = context;
        this.subscribeOn = subscribeOn;
        this.observeOn = observeOn;
    }

    public Context getAppContext() {
        return appContext;
    }

    public void setUseWakelock(boolean useWakelock) {
        this.useWakelock = useWakelock;
    }

    public <T extends RxRequest> void enqueue(final T request, final RxCallback<T> callback) {
        create(request)
                .compose(applyCommon(callback))
                .subscribe(createCallback(callback));
    }

    public <T extends RxRequest> void enqueueList(final RxCallback<T> callback) {
        final List<Observable<? extends RxRequest>> list = new ArrayList<>();
        if (!CollectionUtils.isNullOrEmpty(getObservables())) {
            list.addAll(getObservables());
        }
        Observable.concat(list)
                .compose(applyCommon(callback))
                .subscribe(createCallback(callback));
        getObservables().clear();
    }

    public <T extends RxRequest> RxManager append(final T request) {
        Observable<T> observable = create(request);
        getObservables().add(observable);
        return this;
    }

    private List<Observable<? extends RxRequest>> getObservables() {
        return observables;
    }

    public <T extends RxRequest> void concat(final List<T> requests,
                                             final RxCallback<T> callback) {
        final List<Observable<T>> list = new ArrayList<>();
        for (T rxRequest : requests) {
            list.add(create(rxRequest));
        }
        Observable.concat(list)
                .compose(applyCommon(callback))
                .subscribe(createCallback(callback));
    }

    public <T1 extends RxRequest, T2 extends RxRequest, T3 extends RxRequest, T4> void zip3(
            final T1 r1,
            final T2 r2,
            final T3 r3,
            final Function3<T1, T2, T3, T4> function3,
            final RxCallback<T4> callback) {
        final Observable<T1> observable1 = create(r1);
        final Observable<T2> observable2 = create(r2);
        final Observable<T3> observable3 = create(r3);

        Observable.zip(observable1, observable2, observable3, function3)
                .compose(applyCommon(callback))
                .subscribe(createCallback(callback));
    }

    public <T extends RxRequest> Observable<T> create(final T request) {
        request.setContext(getAppContext());
        return Observable.fromCallable(new Callable<T>() {
            @Override
            public T call() throws Exception {
                benchmarkStart();
                try {
                    request.execute();
                } catch (Throwable e) {
                    Debug.e(e);
                    throw e;
                }
                benchmarkEnd();
                return request;
            }
        });
    }

    private <T> ObservableTransformer<T, T> applyCommon(final RxCallback callback) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream
                        .subscribeOn(subscribeOn)
                        .observeOn(observeOn)
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(Disposable disposable) throws Exception {
                                acquireWakelock(RX_MANAGER_ACTION);
                                RxCallback.onSubscribe(callback, disposable);
                            }
                        })
                        .doFinally(new Action() {
                            @Override
                            public void run() throws Exception {
                                releaseWakelock();
                                RxCallback.onFinally(callback);
                            }
                        });
            }
        };
    }

    private <T> RxCallback<T> createCallback(final RxCallback callback) {
        return new RxCallback<T>() {
            @Override
            public void onNext(@NonNull T t) {
                try {
                    RxCallback.onNext(callback, t);
                } catch (Throwable e) {
                    Debug.e(e);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                try {
                    RxCallback.onError(callback, e);
                } catch (Throwable throwable) {
                    Debug.e(throwable);
                }
            }

            @Override
            public void onComplete() {
                try {
                    RxCallback.onComplete(callback);
                } catch (Throwable throwable) {
                    Debug.e(throwable);
                }
            }
        };
    }

    public boolean isEnableBenchmarkDebug() {
        return enableBenchmarkDebug;
    }

    private void benchmarkStart() {
        if (!isEnableBenchmarkDebug()) {
            return;
        }
        benchmark = new Benchmark();
    }

    private long benchmarkEnd() {
        if (!isEnableBenchmarkDebug() || benchmark == null) {
            return 0;
        }
        return (benchmark.duration());
    }

    private void acquireWakelock(final String tag) {
        if (useWakelock) {
            wakeLockHolder.acquireWakeLock(appContext, tag);
        }
    }

    private void releaseWakelock() {
        if (useWakelock) {
            wakeLockHolder.releaseWakeLock();
        }
    }

    public static final class Builder {
        private Scheduler subscribeOn;
        private Scheduler observeOn;
        private static Context appContext;

        public static void initAppContext(Context context) {
            appContext = context;
        }

        public Builder subscribeOn(@NonNull Scheduler subscribeOn) {
            this.subscribeOn = subscribeOn;
            return this;
        }

        public Builder observeOn(@NonNull Scheduler observeOn) {
            this.observeOn = observeOn;
            return this;
        }

        public RxManager build() {
            if (subscribeOn == null) {
                throw new IllegalStateException("subscribeOn required.");
            }

            if (observeOn == null) {
                observeOn = AndroidSchedulers.mainThread();
            }

            if (appContext == null) {
                Debug.w(getClass(), "Please call initAppContext first!");
                initAppContext(ResManager.getAppContext());
            }
            return new RxManager(appContext, subscribeOn, observeOn);
        }

        public static RxManager sharedSingleThreadManager() {
            return new Builder()
                    .subscribeOn(SingleThreadScheduler.scheduler())
                    .observeOn(AndroidSchedulers.mainThread())
                    .build();
        }

        public static RxManager sharedMultiThreadManager() {
            return new Builder()
                    .subscribeOn(MultiThreadScheduler.scheduler())
                    .observeOn(AndroidSchedulers.mainThread())
                    .build();
        }

        public static RxManager newSingleThreadManager() {
            return new Builder()
                    .subscribeOn(SingleThreadScheduler.scheduler())
                    .observeOn(AndroidSchedulers.mainThread())
                    .build();
        }

        public static RxManager newMultiThreadManager() {
            return new Builder()
                    .subscribeOn(MultiThreadScheduler.scheduler())
                    .observeOn(AndroidSchedulers.mainThread())
                    .build();
        }
    }
}
