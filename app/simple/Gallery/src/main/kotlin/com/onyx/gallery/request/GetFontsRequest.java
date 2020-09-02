package com.onyx.gallery.request;

import com.onyx.android.sdk.data.FontInfo;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.gallery.bundle.EditBundle;
import com.onyx.gallery.common.BaseRequest;
import com.onyx.gallery.handler.DrawHandler;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static com.onyx.android.sdk.utils.DeviceUtils.SYSTEM_FONTS_PATH;

/**
 * Created by ming on 2016/11/18.
 */

public class GetFontsRequest extends BaseRequest {
    private static HashSet<String> ignoreFonts = new HashSet<>(Arrays.asList("AndroidClock Regular",
            "AndroidClock-Large Regular",
            "Clockopia"));

    private String currentFont;

    private List<FontInfo> chineseFontList;
    private List<FontInfo> englishFontList;
    private List<FontInfo> customizeFontList;
    private boolean onyxSystemFontExist;

    public GetFontsRequest(EditBundle editBundle, String currentFont) {
        super(editBundle);
        this.currentFont = currentFont;
    }

    public List<FontInfo> getChineseFontList() {
        return chineseFontList;
    }

    public List<FontInfo> getEnglishFontList() {
        return englishFontList;
    }

    public List<FontInfo> getCustomizeFonts() {
        return customizeFontList;
    }

    @Override
    public void execute(DrawHandler bundle) throws Exception {
        chineseFontList = requestFontInfoList(DeviceUtils.FontType.CHINESE);
        englishFontList = requestFontInfoList(DeviceUtils.FontType.ENGLISH);
        customizeFontList = requestCustomizeFontInfoList(DeviceUtils.FontType.CUSTOMIZE);
        onyxSystemFontExist = FileUtils.fileExist(DeviceUtils.ONYX_SYSTEM_DEFAULT_SYSTEM_FONT_ID);
    }

    private List<FontInfo> requestCustomizeFontInfoList(DeviceUtils.FontType fontType) {
        List<FontInfo> list = DeviceUtils.buildFontItemAdapter(Collections.singletonList(DeviceUtils.CUSTOMIZE_FONTS_PATH),
                currentFont, null, fontType);
        return filterIgnoreFonts(list);
    }

    private List<FontInfo> requestFontInfoList(DeviceUtils.FontType fontType) {
        List<FontInfo> list = DeviceUtils.buildFontItemAdapter(Collections.singletonList(SYSTEM_FONTS_PATH),
                currentFont, null, fontType);
        return filterIgnoreFonts(list);
    }

    private List<FontInfo> filterIgnoreFonts(List<FontInfo> list) {
        List<FontInfo> fontInfoList = new ArrayList<>();
        for (FontInfo fi : list) {
            if (!ignoreFonts.contains(fi.getName())) {
                fontInfoList.add(fi);
            }
        }
        return fontInfoList;
    }

    public boolean isOnyxSystemFontExist() {
        return onyxSystemFontExist;
    }

    public FontInfo getDetDefaultFont() {
        FontInfo fontInfo = getDetDefaultFontByList(chineseFontList);
        if (fontInfo == null) {
            fontInfo = getDetDefaultFontByList(customizeFontList);
        }
        if (fontInfo == null) {
            fontInfo = getDetDefaultFontByList(englishFontList);
        }
        return fontInfo;
    }

    @Nullable
    private FontInfo getDetDefaultFontByList(List<FontInfo> fontList) {
        for (FontInfo fontInfo : fontList) {
            if (StringUtils.isEquals(DeviceUtils.ONYX_SYSTEM_DEFAULT_SYSTEM_FONT_ID, fontInfo.getId())) {
                return fontInfo;
            }
        }
        return null;
    }

}
