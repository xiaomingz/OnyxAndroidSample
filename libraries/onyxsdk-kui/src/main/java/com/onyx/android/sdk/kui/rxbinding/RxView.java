package com.onyx.android.sdk.kui.rxbinding;

import android.view.View;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class RxView {
    public static int DEFAULT_SHORT_DURATION = 500;
    public static int DEFAULT_LONG_DURATION = 2000;
    public static int DEFAULT_SHOW_DELAY = 500;
    public static int localCount = 0;
    public static long firstClickTime = 0;

    public static Disposable onClick(View view, View.OnClickListener listener) {
        return onClick(view, listener, DEFAULT_LONG_DURATION, TimeUnit.MILLISECONDS);
    }

    public static Disposable onShortClick(View view, View.OnClickListener listener) {
        return onClick(view, listener, DEFAULT_SHORT_DURATION, TimeUnit.MILLISECONDS);
    }

    public static Disposable onClick(View view, final View.OnClickListener listener, long duration, TimeUnit unit) {
        return RxView.clicks(view)
                .throttleFirst(duration, unit)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<View>() {
                    @Override
                    public void accept(View v) throws Exception {
                        listener.onClick(v);
                    }
                });
    }

    public static Observable<View> clicks(View view) {
        return new ViewClickObservable(view);
    }

    public interface ViewOnclickListener {
        void onClickListener(View view);
    }

    public static Disposable onClickTimes(final View view, long millisecond, final int count, final ViewOnclickListener listener) {
        return RxView.clicks(view)
                .buffer(millisecond, TimeUnit.MILLISECONDS)
                .filter(new Predicate<List<View>>() {
                    @Override
                    public boolean test(List<View> objects) throws Exception {
                        return objects.size() >= count;
                    }
                })
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable d) throws Exception {
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<View>>() {
                    @Override
                    public void accept(List<View> views) throws Exception {
                        if (listener != null) {
                            listener.onClickListener(view);
                        }
                    }
                });
    }
}
