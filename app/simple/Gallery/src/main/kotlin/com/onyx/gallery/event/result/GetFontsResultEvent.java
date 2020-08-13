package com.onyx.gallery.event.result;

import com.onyx.android.sdk.data.FontInfo;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * <pre>
 *     author : liao lin tao
 *     time   : 2018/10/31 16:44
 *     desc   :
 * </pre>
 */

public class GetFontsResultEvent extends BaseResultEvent {
    public Exception throwable;
    private List<FontInfo> chineseFontList;
    private List<FontInfo> englishFontList;
    private List<FontInfo> customizeFontList;
    private boolean onyxSystemFontExist;

    public GetFontsResultEvent() {
        super(null);
    }

    public GetFontsResultEvent(@Nullable Throwable throwable) {
        super(throwable);
    }

    public List<FontInfo> getChineseFontList() {
        return chineseFontList;
    }

    public GetFontsResultEvent setChineseFontList(List<FontInfo> chineseFontList) {
        this.chineseFontList = chineseFontList;
        return this;
    }

    public List<FontInfo> getEnglishFontList() {
        return englishFontList;
    }

    public GetFontsResultEvent setEnglishFontList(List<FontInfo> englishFontList) {
        this.englishFontList = englishFontList;
        return this;
    }

    public List<FontInfo> getCustomizeFontList() {
        return customizeFontList;
    }

    public GetFontsResultEvent setCustomizeFontList(List<FontInfo> customizeFontList) {
        this.customizeFontList = customizeFontList;
        return this;
    }

    public GetFontsResultEvent setOnyxSystemFontExist(boolean onyxSystemFontExist) {
        this.onyxSystemFontExist = onyxSystemFontExist;
        return this;
    }

    public boolean isOnyxSystemFontExist() {
        return onyxSystemFontExist;
    }

}
