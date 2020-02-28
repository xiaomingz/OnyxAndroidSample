package com.onyx.android.sdk.data;

import android.appwidget.AppWidgetProviderInfo;
import android.graphics.drawable.Drawable;

import com.alibaba.fastjson.annotation.JSONField;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * <pre>
 *     author : suicheng
 *     time   : 2019/12/20 12:11
 *     desc   :
 * </pre>
 */
public class AppWidgetInfo extends AppBaseInfo {
    public String appLabelName;
    public String idString;
    public long time;
    @JSONField(deserialize = false, serialize = false)
    public Drawable previewDrawable;
    @JSONField(deserialize = false, serialize = false)
    public AppWidgetProviderInfo providerInfo;
    private String packageName;
    private String providerClsName;

    public String getLabelName() {
        return StringUtils.isNotBlank(appLabelName) ? appLabelName : name;
    }

    public void setProviderInfo(AppWidgetProviderInfo providerInfo) {
        this.providerInfo = providerInfo;
        if (providerInfo != null) {
            packageName = providerInfo.provider.getPackageName();
            providerClsName = providerInfo.provider.getClassName();
        }
    }

    public String getPackageName() {
        if (StringUtils.isNullOrEmpty(packageName) && providerInfo != null) {
            packageName = providerInfo.provider.getPackageName();
        }
        return packageName;
    }

    public String getProviderClsName() {
        if (StringUtils.isNullOrEmpty(providerClsName) && providerInfo != null) {
            providerClsName = providerInfo.provider.getClassName();
        }
        return providerClsName;
    }

    public void setProviderClsName(String providerClsName) {
        this.providerClsName = providerClsName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @JSONField(deserialize = false, serialize = false)
    public int getMinWidth() {
        return providerInfo.minWidth;
    }

    @JSONField(deserialize = false, serialize = false)
    public int getMinHeight() {
        return providerInfo.minHeight;
    }
}
