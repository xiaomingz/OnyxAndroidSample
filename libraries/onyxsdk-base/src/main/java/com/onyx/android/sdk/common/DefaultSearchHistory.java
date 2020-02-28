package com.onyx.android.sdk.common;

import android.support.annotation.NonNull;

import com.onyx.android.sdk.common.base.BaseSearchHistoryHelper;

/**
 * @author Kaiguang
 * @Description
 * @Time 2019/5/28
 */
public class DefaultSearchHistory extends BaseSearchHistoryHelper {

    public static DefaultSearchHistory create(MMKVBuilder mmkvBuilder) {
        return new DefaultSearchHistory(mmkvBuilder);
    }

    private DefaultSearchHistory(@NonNull MMKVBuilder mmkvBuilder) {
        super(mmkvBuilder);
    }
}
