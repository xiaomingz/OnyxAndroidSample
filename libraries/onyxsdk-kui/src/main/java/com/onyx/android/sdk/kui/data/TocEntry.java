package com.onyx.android.sdk.kui.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by suicheng on 2017/4/30.
 */

public class TocEntry<T> {
    public T item;
    public List<TocEntry<T>> children = new ArrayList<>();

    public String getTitle() {
        return "";
    }
}
