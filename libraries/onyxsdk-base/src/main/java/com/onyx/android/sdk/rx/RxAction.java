package com.onyx.android.sdk.rx;


/**
 * <pre>
 *     author : liao lin tao
 *     time   : 2018/1/19 16:37
 *     desc   : if you use {@link RxManager#enqueue(RxRequest)}, you can extends this.
 * </pre>
 */

public abstract class RxAction<T extends RxRequest> {

    public abstract void execute(RxCallback<T> callback);

}
