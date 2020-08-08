package com.onyx.android.sdk.kui.rxbinding;

import android.view.View;
import android.view.View.OnClickListener;


import com.onyx.android.sdk.kui.utils.ObservableUtils;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

final class ViewClickObservable extends Observable<View> {

    private final View view;

    ViewClickObservable(View view) {
        this.view = view;
    }

    @Override
    protected void subscribeActual(Observer<? super View> observer) {
        if (!ObservableUtils.checkMainThread(observer)) {
            return;
        }
        Listener listener = new Listener(view, observer);
        observer.onSubscribe(listener);
        view.setOnClickListener(listener);
    }

    static final class Listener extends MainThreadDisposable implements OnClickListener {
        private final View view;
        private final Observer<? super View> observer;

        Listener(View view, Observer<? super View> observer) {
            this.view = view;
            this.observer = observer;
        }

        @Override
        public void onClick(View v) {
            if (!isDisposed()) {
                observer.onNext(v);
            }
        }

        @Override
        protected void onDispose() {
            view.setOnClickListener(null);
        }
    }
}
