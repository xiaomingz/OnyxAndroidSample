package com.onyx.android.sdk.rx;

import android.content.Context;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>
 *     author : suicheng
 *     time   : 2018/10/9 18:17
 *     desc   :
 * </pre>
 */
public class ThreadPoolHolder {

    private ConcurrentHashMap<String, RxManager> threadPoolMap = new ConcurrentHashMap<>();

    public RxManager getRxManager(Context context, String identifier, boolean multi) {
        String poolKey = identifier + (multi ? "-multi" : "-single");
        RxManager rxManager = threadPoolMap.get(poolKey);
        if (rxManager == null) {
            RxManager.Builder.initAppContext(context.getApplicationContext());
            rxManager = multi ? RxManager.Builder.newMultiThreadManager() : RxManager.Builder.newSingleThreadManager();
            threadPoolMap.put(poolKey, rxManager);
        }
        return rxManager;
    }
}
