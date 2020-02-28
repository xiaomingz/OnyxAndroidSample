package com.onyx.android.sdk.rx.rxbroadcast;

import android.content.Context;

import com.onyx.android.sdk.rx.RxCallback;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * <pre>
 *     author : lxw
 *     time   : 2018/6/5 14:33
 *     desc   :
 * </pre>
 */
public class RxBroadcast {

    public static final String TAG = RxBroadcast.class.getSimpleName();

    public static Disposable connectivityChangeWithTimeOut(Context context, final RxCallback<Boolean> callback) {
        return new RxConnectivityChangeObservable(context)
                .subscribeOn(AndroidSchedulers.mainThread())
                .timeout(8, TimeUnit.SECONDS)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        RxCallback.onNext(callback, aBoolean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        RxCallback.onError(callback, throwable);
                    }
                });
    }

    public static Disposable connectivityChange(Context context, final RxCallback<Boolean> callback) {
        return new RxConnectivityChangeObservable(context)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        RxCallback.onNext(callback, aBoolean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        RxCallback.onError(callback, throwable);
                    }
                });
    }

}
