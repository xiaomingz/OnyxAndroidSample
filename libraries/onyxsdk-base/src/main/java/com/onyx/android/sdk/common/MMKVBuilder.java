package com.onyx.android.sdk.common;

import android.content.Context;

import com.tencent.mmkv.MMKV;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/5/13 18:35
 *     desc   :
 * </pre>
 */
public class MMKVBuilder {

    private String mmapID;
    private int mmapMode;

    private MMKVBuilder(String mmapID, int mmapMode) {
        this.mmapID = mmapID;
        this.mmapMode = mmapMode;
    }

    public MMKV getMMKV() {
        return MMKV.mmkvWithID(mmapID, mmapMode);
    }

    public static MMKVBuilder init(Context context, String id, int mode) {
        MMKV.initialize(context);
        return new MMKVBuilder(id, mode);
    }

    public static MMKVBuilder init(Context context, String id) {
        return init(context, id, MMKV.SINGLE_PROCESS_MODE);
    }

    public void putString(String k, String v) {
        getMMKV().putString(k, v);
    }

    public void putInt(String k, int v) {
        getMMKV().putInt(k, v);
    }

    public void putBoolean(String k, boolean v) {
        getMMKV().putBoolean(k, v);
    }

    public void putFloat(String k, float v) {
        getMMKV().putFloat(k, v);
    }

    public void putLong(String k, long v) {
        getMMKV().putLong(k, v);
    }


    public String getString(String k, String defaul) {
        return getMMKV().getString(k, defaul);
    }

    public int getInt(String k, int defaul) {
        return getMMKV().getInt(k, defaul);
    }

    public boolean getBoolean(String k, boolean defaul) {
        return getMMKV().getBoolean(k, defaul);
    }

    public float getFloat(String k, float defaul) {
        return getMMKV().getFloat(k, defaul);
    }

    public long getLong(String k, long defaul) {
        return getMMKV().getLong(k, defaul);
    }
}
