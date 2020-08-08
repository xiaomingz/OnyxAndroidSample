package com.onyx.android.sdk.kui.utils;

import android.os.Looper;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposables;

/**
 * Created by suicheng on 2018/4/21.
 */

public class ObservableUtils {

    public static boolean checkMainThread(Observer<?> observer) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            observer.onSubscribe(Disposables.empty());
            observer.onError(new IllegalStateException(
                    "Expected to be called on the main thread but was " + Thread.currentThread().getName()));
            return false;
        }
        return true;
    }
}
