package com.onyx.gallery.models;

import android.graphics.Typeface;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.onyx.android.sdk.data.FontInfo;

/**
 * created by weiyang on 18-9-29
 */
public class ItemTextStyleOptionChoiceViewModel {
    public ObservableField<String> name = new ObservableField<>();
    public ObservableBoolean isChecked = new ObservableBoolean();
    public Typeface typeface;
    public FontInfo fontInfo;
    public int id;

    public ItemTextStyleOptionChoiceViewModel(String name) {
        this.name.set(name);
    }

    public ItemTextStyleOptionChoiceViewModel(String name, boolean isChecked) {
        this.name.set(name);
        this.isChecked.set(isChecked);
    }

    public static ItemTextStyleOptionChoiceViewModel createViewModel(String name, boolean isChecked) {
        return new ItemTextStyleOptionChoiceViewModel(name, isChecked);
    }

    public ItemTextStyleOptionChoiceViewModel setTypeface(Typeface typeface) {
        this.typeface = typeface;
        return this;
    }

    public FontInfo getFontInfo() {
        return fontInfo;
    }

    public ItemTextStyleOptionChoiceViewModel setFontInfo(FontInfo fontInfo) {
        this.fontInfo = fontInfo;
        return this;
    }

    public int getId() {
        return id;
    }

    public ItemTextStyleOptionChoiceViewModel setId(int id) {
        this.id = id;
        return this;
    }
}
