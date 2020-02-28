package com.onyx.android.sdk.utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ObjectHolder<T> {
    private List<WeakReference<T>> objectList;

    public ObjectHolder() {
        objectList = new ArrayList<>();
    }

    public void add(WeakReference<T> weakReference) {
        objectList.add(weakReference);
    }

    public void remove(WeakReference<T> weakReference) {
        objectList.remove(weakReference);
    }

    public void remove(T object) {
        for (WeakReference<T> weakReference : objectList) {
            if (weakReference.get() == object) {
                remove(weakReference);
                return;
            }
        }
    }

    public void clear() {
        objectList.clear();
    }

    public List<WeakReference<T>> getObjectList() {
        return objectList;
    }

    public List<WeakReference<T>> getCopyOfObjectList() {
        return new ArrayList<>(objectList);
    }
}
