package com.onyx.gallery.action;


import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.rx.RxUtils;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.TestUtils;
import com.onyx.gallery.common.BaseEditAction;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * <pre>
 *     author : suicheng
 *     time   : 2019/10/26 12:38
 *     desc   :
 * </pre>
 */
public class WaitForUpdateFinishedAction extends BaseEditAction {
    private static String TAG = "WaitForUpdateFinishedAction";
    private static final long DEFAULT_WAIT_TIME = 150;
    private Disposable disposable;
    private long minWaitTime = 0;

    public WaitForUpdateFinishedAction setMinWaitTime(long minWaitTime) {
        this.minWaitTime = minWaitTime;
        return this;
    }

    @Override
    public void execute(final RxCallback callback) {
        disposable = RxUtils.runInComputation(new Runnable() {
            @Override
            public void run() {
                long sleepTime = Math.max(minWaitTime, DEFAULT_WAIT_TIME);
                TestUtils.sleep(sleepTime);
                boolean disposed = RxUtils.isDisposed(disposable);
                String msg = "sleepTime = " + sleepTime + " ms"
                        + ", isDisposed = " + disposed;
                if (disposed) {
                    Debug.v(TAG, msg);
                } else {
                    Debug.d(TAG, msg);
                }
            }
        }, new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                if (callback != null) {
                    callback.onNext(WaitForUpdateFinishedAction.this);
                }
            }
        });
    }

    public Disposable getDisposable() {
        return disposable;
    }
}
