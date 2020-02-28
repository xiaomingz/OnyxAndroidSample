package com.onyx.android.sdk.rx;


import android.content.Context;

/**
 * <pre>
 *     author : liao lin tao
 *     time   : 2018/1/4 10:45
 *     desc   :
 * </pre>
 */

public abstract class RxRequest {
    public static final String DEFAULT_IDENTIFIER = "default";
    private volatile boolean abort = false;
    private Context context;

    public abstract void execute() throws Exception;

    public void setAbort() {
        abort = true;
    }

    public boolean isAbort() {
        return abort;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getIdentifier() {
        return DEFAULT_IDENTIFIER;
    }
}
