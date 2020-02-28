package com.onyx.android.sdk.rx.rxbroadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import com.onyx.android.sdk.utils.NetworkUtil;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * <pre>
 *     author : lxw
 *     time   : 2018/6/5 12:08
 *     desc   :
 * </pre>
 */
public class RxConnectivityChangeObservable extends Observable<Boolean> {

    private Context context;

    public RxConnectivityChangeObservable(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    protected void subscribeActual(Observer<? super Boolean> observer) {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        Listener listener = new Listener(observer, context);
        observer.onSubscribe(listener);
        context.registerReceiver(listener, filter);
    }

    static final class Listener extends BroadcastReceiver implements Disposable {

        private Observer<? super Boolean> observer;
        private Context context;

        public Listener(Observer<? super Boolean> observer, Context context) {
            this.observer = observer;
            this.context = context;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetworkUtil.isWiFiConnected(context)) {
                observer.onNext(true);
            }else {
                observer.onNext(false);
            }
        }

        @Override
        public void dispose() {
            context.unregisterReceiver(this);
        }

        @Override
        public boolean isDisposed() {
            return false;
        }
    }
}
