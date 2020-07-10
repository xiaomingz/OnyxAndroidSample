package com.onyx.gallery.request;

import com.onyx.android.sdk.data.FontInfo;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.gallery.common.BaseRequest;
import com.onyx.gallery.handler.DrawHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

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

    public GetFontsRequest(String currentFont) {
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
    }

    private List<FontInfo> requestCustomizeFontInfoList(DeviceUtils.FontType fontType) {
        List<FontInfo> list = DeviceUtils.buildFontItemAdapter(Collections.singletonList(DeviceUtils.CUSTOMIZE_FONTS_PATH),
                currentFont, null, fontType);
        return filterIgnoreFonts(list);
    }

    private List<FontInfo> requestFontInfoList(DeviceUtils.FontType fontType) {
        List<FontInfo> list = DeviceUtils.buildFontItemAdapter(Collections.singletonList(DeviceUtils.SYSTEM_FONTS_PATH),
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

}
