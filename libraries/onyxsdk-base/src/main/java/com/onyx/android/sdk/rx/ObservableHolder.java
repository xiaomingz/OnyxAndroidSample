package com.onyx.android.sdk.rx;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * <pre>
 *     author : lxw
 *     time   : 2018/12/27 16:19
 *     desc   :
 * </pre>
 */
public class ObservableHolder<T> {

    private Observable<T> observable;
    private ObservableEmitter<T> emitter;
    private Disposable disposable;

    public ObservableHolder() {
        createObservable();
    }

    public ObservableHolder(Observable<T> observable) {
        this.observable = observable;
    }

    private void createObservable() {
        observable = Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) {
                ObservableHolder.this.emitter = emitter;
            }
        });
    }

    public ObservableHolder<T> onNext(T t) {
        if (emitter != null) {
            emitter.onNext(t);
        }
        return this;
    }

    public Observable<T> getObservable() {
        return observable;
    }

    public Observable<T> subscribeOn(Scheduler scheduler) {
        return getObservable().subscribeOn(scheduler);
    }

    public Observable<T> observeOn(Scheduler scheduler) {
        return getObservable().observeOn(scheduler);
    }

    public ObservableHolder<T> setDisposable(Disposable disposable) {
        this.disposable = disposable;
        return this;
    }

    public void dispose() {
        if (disposable != null) {
            disposable.dispose();
        }
    }

}
