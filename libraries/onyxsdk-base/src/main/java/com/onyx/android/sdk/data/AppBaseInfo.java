package com.onyx.android.sdk.data;

import android.databinding.BaseObservable;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.alibaba.fastjson.annotation.JSONField;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.Serializable;

/**
 * <pre>
 *     author : suicheng
 *     time   : 2019/12/27 17:00
 *     desc   :
 * </pre>
 */
public class AppBaseInfo extends BaseObservable implements Serializable {
    public enum Type {
        APP, GROUP, WIDGET
    }

    public String name;
    @JSONField(deserialize = false, serialize = false)
    public Drawable iconDrawable;
    @JSONField(deserialize = false, serialize = false)
    public Bitmap iconImage;
    private Type type;
    public String typeAlias;
    public int rowSpan = 1;
    public int colSpan = 1;
    public int x = 0;
    public int y = 0;
    public int page = 0;

    public void setType(Type type) {
        this.type = type;
        setTypeAlias(type);
    }

    private void setTypeAlias(Type type) {
        this.typeAlias = getTypeAlias(type);
    }

    private static String getTypeAlias(Type type) {
        return "type:" + type.name();
    }

    public Type getType() {
        return type;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    public int getColSpan() {
        return colSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    public int getSpanSize() {
        return colSpan * rowSpan;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public Bitmap getIconImage() {
        return iconImage;
    }

    public void setIconImage(Bitmap iconImage) {
        this.iconImage = iconImage;
    }

    public Drawable getIconDrawable() {
        return iconDrawable;
    }

    public void setIconDrawable(Drawable iconDrawable) {
        this.iconDrawable = iconDrawable;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean isGroupType() {
        return getType() == Type.GROUP;
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean isAppType() {
        return getType() == Type.APP;
    }

    @JSONField(serialize = false, deserialize = false)
    public boolean isWidgetType() {
        return getType() == Type.WIDGET;
    }
}
