package com.onyx.android.sdk.data;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import com.alibaba.fastjson.annotation.JSONField;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : suicheng
 *     time   : 2019/12/11 19:30
 *     desc   :
 * </pre>
 */
public class AppGroupInfo extends AppBaseInfo implements Serializable {
    public String idString;
    public long time;
    public List<AppDataInfo> appInfoList = new ArrayList<>();

    @Nullable
    public AppDataInfo getMatchPackageNameAppInfo(String packageName) {
        for (AppDataInfo appDataInfo : appInfoList) {
            if (StringUtils.safelyEquals(appDataInfo.packageName, packageName)) {
                return appDataInfo;
            }
        }
        return null;
    }
}
