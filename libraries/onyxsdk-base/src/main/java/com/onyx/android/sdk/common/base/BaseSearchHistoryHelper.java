package com.onyx.android.sdk.common.base;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.onyx.android.sdk.common.MMKVBuilder;
import com.onyx.android.sdk.utils.JSONUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kaiguang
 * @Description
 * @Time 2019/5/28
 */
public class BaseSearchHistoryHelper {
    private static final String SEARCH_HISTORY_KEY = "search_history_key";
    private static final int MAX_COUNT = 8;

    private MMKVBuilder mmkvBuilder;

    protected BaseSearchHistoryHelper() {

    }

    protected BaseSearchHistoryHelper(@NonNull MMKVBuilder mmkvBuilder) {
        this.mmkvBuilder = mmkvBuilder;
    }

    /**
     * Default implementation
     * @param searchText
     */
    public List<String> save(@NonNull String searchText){
        List<String> list = getSearchHistory();
        int maxCount = getMaxCount();
        if (list == null) {
            list = new ArrayList<>(maxCount);
        }
        if (list.contains(searchText)) {
            list.remove(searchText);
        }
        list.add(0, searchText);
        if (list.size() > maxCount) {
            list.removeAll(list.subList(maxCount, list.size()));
        }
        save(list);
        return list;
    }

    public void save(List<String> list) {
        mmkvBuilder.putString(getSearchHistoryKey(), JSONUtils.toJson(list));
    }

    public List<String> getSearchHistory() {
        String listString = mmkvBuilder.getString(getSearchHistoryKey(), "");
        if (TextUtils.isEmpty(listString)) {
            return new ArrayList<>();
        }
        return JSONUtils.toList(listString, String.class);
    }

    public void clear() {
        mmkvBuilder.putString(getSearchHistoryKey(), "");
    }

    public int getMaxCount() {
        return MAX_COUNT;
    }

    protected String getSearchHistoryKey() {
        return SEARCH_HISTORY_KEY;
    }
}
