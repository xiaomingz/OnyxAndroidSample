package com.onyx.android.sdk.utils;

import android.content.Context;
import android.os.Build;
import android.os.LocaleList;

import java.util.Collection;
import java.util.Locale;

/**
 * Created by zengzhu on 2/22/16.
 */
public class LocaleUtils {

    public static String LOCALE_SPLIT_CHAR = "_";
    public static String LOCALE_RUSSIAN_LANGUAGE = "ru";
    public static String LOCALE_RUSSIAN_COUNTRY = "RU";
    public static String LOCALE_HONGKONG = "HK";
    public static final int AUTO			= -1;

    public static final int CP437			= 437;
    public static final int CP850			= 850;
    public static final int CP855			= 855;
    public static final int CP860			= 860;
    public static final int CP861			= 861;
    public static final int CP863			= 863;
    public static final int CP865			= 865;
    public static final int CP866			= 866;
    public static final int CP874			= 874;

    public static final int CP932			= 932;
    public static final int CP936			= 936;
    public static final int CP949			= 949;
    public static final int CP950			= 950;

    public static final int CP1200			= 1200;
    public static final int CP1201			= 1201;
    public static final int CP1250			= 1250;
    public static final int CP1251			= 1251;
    public static final int CP1252			= 1252;
    public static final int CP1253			= 1253;
    public static final int CP1254			= 1254;
    public static final int CP1255			= 1255;
    public static final int CP1256			= 1256;
    public static final int CP1257			= 1257;
    public static final int CP1258			= 1258;

    public static final int CP10000			= 10000;
    public static final int CP10007			= 10007;
    public static final int CP10017			= 10017;
    public static final int CP10079			= 10079;
    public static final int CP20127			= 20127;
    public static final int CP20866			= 20866;
    public static final int CP21866			= 21866;
    public static final int CP28591			= 28591;
    public static final int CP28592			= 28592;
    public static final int CP28595			= 28595;
    public static final int CP28605			= 28605;

    public static final int CP65001			= 65001;

    public static boolean isChinese() {
        final String language = Locale.getDefault().getDisplayLanguage();
        return  language.equals(Locale.CHINESE.getDisplayLanguage());
    }

    public static boolean isEnglish() {
        final String language = Locale.getDefault().getDisplayLanguage();
        return  language.equals(Locale.ENGLISH.getDisplayLanguage());
    }

    public static boolean isRussian() {
        final String language = Locale.getDefault().getLanguage();
        return  language.equals(LOCALE_RUSSIAN_LANGUAGE);
    }

    public static int getLocaleDefaultCodePage() {
        switch (Locale.getDefault().getLanguage()) {
            case "zh":
                return CP936;
            case "ja":
                return CP932;
            case "ko":
                return CP949;
            case "ru":
                return CP1251;
            default:
                return CP65001;
        }
    }

    public static boolean isSameLocale(Locale sourceLocal, Locale compareLocal) {
        return sourceLocal.getLanguage().equalsIgnoreCase(compareLocal.getLanguage()) &&
                sourceLocal.getCountry().equalsIgnoreCase(compareLocal.getCountry());
    }

    public static Locale getCurrentLocale(Context context) {
        if (CompatibilityUtil.apiLevelCheck(Build.VERSION_CODES.N)) {
            LocaleList localeList = context.getResources().getConfiguration().getLocales();
            if (localeList.size() > 0) {
                return localeList.get(0);
            }
        }
        return context.getResources().getConfiguration().locale;
    }

    public static boolean contains(Locale targetLocale, Collection<Locale> localeCollection) {
        for (Locale locale : localeCollection) {
            if (isSameLocale(targetLocale, locale)) {
                return true;
            }
        }
        return false;
    }

    public static String getLocaleStr(Locale locale) {
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        StringBuilder builder = new StringBuilder();
        builder.append(language);
        builder.append(LOCALE_SPLIT_CHAR);
        builder.append(country);
        if (!StringUtils.isNullOrEmpty(variant)) {
            builder.append(LOCALE_SPLIT_CHAR);
            builder.append(variant);
        }
        return builder.toString();
    }

    public static Locale getLocal(String localStr) {
        String[] words = localStr.split(LOCALE_SPLIT_CHAR);
        if (words.length < 2) {
            return null;
        }
        return words.length > 2 ? new Locale(words[0], words[1], words[2]) : new Locale(words[0], words[1]);
    }

    public static String getDisplayNameSafely(Locale locale){
        if (isTWHKLocale(locale)) {
            return locale.getDisplayLanguage() + " (" + getDisplayCountrySafely(locale) + ")";
        }
        return locale.getDisplayName();
    }

    public static String getDisplayCountrySafely(Locale locale){
        if (isTWHKLocale(locale)) {
            StringBuilder buffer = new StringBuilder(Locale.CHINA.getDisplayCountry());
            if (StringUtils.isAlpha(Locale.CHINA.getDisplayCountry())) {
                buffer.append(" ");
            }
            buffer.append(locale.getDisplayCountry());
            return buffer.toString();
        }
        return locale.getDisplayCountry();
    }

    public static boolean isTWHKLocale(Locale locale) {
        return locale.equals(Locale.TAIWAN) || locale.getCountry().equals(LOCALE_HONGKONG);
    }

    public static boolean isChinaLocale(Locale locale) {
        return isSameLocale(locale, Locale.CHINA);
    }
}
