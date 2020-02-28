package com.onyx.android.sdk.data;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.alibaba.fastjson.annotation.JSONField;
import com.onyx.android.sdk.utils.ResManager;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.Serializable;

/**
 * Created by suicheng on 2017/2/16.
 */
public class AppDataInfo extends AppBaseInfo implements Serializable, Cloneable {
    public String action;
    public String packageName;
    public String activityClassName;
    public String labelName;
    public String customName;
    public long lastUpdatedTime;
    public boolean isSystemApp;
    public boolean isCustomizedApp;
    public String iconDrawableName;
    @JSONField(deserialize = false, serialize = false)
    public Intent intent;
    public boolean isAutoFroze = false;
    public boolean isEnable;

    public AppDataInfo clone() {
        AppDataInfo appDataInfo = null;
        try {
            appDataInfo = (AppDataInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return appDataInfo;
    }

    @JSONField(serialize = false, deserialize = false)
    public String getName() {
        String name = ResManager.getStringSafely(labelName);
        if (StringUtils.isNullOrEmpty(name) && StringUtils.isNotBlank(customName)) {
            name = ResManager.getStringSafely(customName);
        }
        return name;
    }

    @Override
    public String toString() {
        return "packageName:" + packageName
                + " labelName:" + labelName
                + " isSystemApp:" + isSystemApp
                + " isEnable:" + isEnable;
    }
}
