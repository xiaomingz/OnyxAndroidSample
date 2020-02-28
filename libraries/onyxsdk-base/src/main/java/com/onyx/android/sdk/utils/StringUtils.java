package com.onyx.android.sdk.utils;


import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Patterns;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhuzeng on 10/16/15.
 */
public class StringUtils {

    static public final String UTF16LE = "UTF-16LE";
    static public final String UTF16BE = "UTF-16BE";
    static public final String UTF16 = "UTF-16";

    static public String punctuation="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
    static public String END_WIDTH_NUMBER_REGEX = "\\d+$";

    static public String toStringSafely(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    static public boolean isNullOrEmpty(final String string) {
        return (string == null || string.trim().length() <= 0);
    }

    static public boolean isNotBlank(final String string) {
        return (string != null && string.trim().length() > 0);
    }

    static public boolean isBlank(final String string) {
        return !isNotBlank(string);
    }

    static public boolean isInteger(final String string) {
        if (isNullOrEmpty(string)) {
            return false;
        }
        String str = string;
        if (string.charAt(0) == '-') {
            if (string.length() <= 1) {
                return false;
            }
            str = string.substring(1, string.length() - 1);
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    static public String utf16le(final byte [] data) {
        String string = "";
        if (data == null) {
            return string;
        }
        try {
            string = new String(data, UTF16LE);
        } catch (Exception e) {
            Log.w("", e);
        }
        return string;
    }

    static public String utf16(final byte [] data) {
        String string = "";
        try {
            string = new String(data, UTF16);
        } catch (Exception e) {
        }
        return string;
    }

    static public byte[] utf16leBuffer(final String text) {
        byte [] buffer = null;
        try {
            buffer = text.getBytes(UTF16LE);
        } catch (Exception e) {
        }
        return buffer;
    }

    public static String join(Iterable<?> elements, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (Object e : elements) {
            if (sb.length() > 0)
                sb.append(delimiter);
            sb.append(e);
        }
        return sb.toString();
    }

    public static List<String> split(final String string, final String delimiter) {
        if (isNullOrEmpty(string)) {
            return new ArrayList<String>();
        }
        final String [] result = string.split(delimiter);
        return Arrays.asList(result);
    }

    public static String deleteNewlineSymbol(String content){
        if (!isNullOrEmpty(content)){
            content = content.replaceAll("\r\n"," ").replaceAll("\n", " ");
        }
        return content;
    }

    public static String leftTrim(String content) {
        if (isNullOrEmpty(content)) {
            return content;
        }
        int start = 0, last = content.length() - 1;
        while ((start <= last) && (content.charAt(start) <= ' ')) {
            start++;
        }
        if (start == 0) {
            return content;
        }
        return content.substring(start, last + 1);
    }

    public static String rightTrim(String content) {
        if (isNullOrEmpty(content)) {
            return content;
        }
        int start = 0, last = content.length() - 1;
        int end = last;
        while ((end >= start) && (content.charAt(end) <= ' ')) {
            end--;
        }
        if (end == last) {
            return content;
        }
        return content.substring(start, end + 1);
    }

    public static String fullTrim(String content) {
        return StringUtils.rightTrim(StringUtils.leftTrim(content));
    }

    public static String substring(String content, int beginIndex, int endIndex) {
        if (StringUtils.isNullOrEmpty(content)) {
            return "";
        }
        int count = content.codePointCount(0, content.length());
        if (endIndex > count) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = beginIndex; i < endIndex; i++) {
            builder.appendCodePoint(content.codePointAt(i));
        }
        return builder.toString();
    }

    public static String trim(String input) {
        if (StringUtils.isNotBlank(input)) {
            input = input.trim();
            input = input.replace("\u0000", "");
            input = input.replace("\\u0000", "");
            input = input.replaceAll("\\u0000", ""); // removes NUL chars
            input = input.replaceAll("\\\\u0000", ""); // removes backslash+u0000
        }
        return input;
    }

    public static String trimPunctuation(String input) {
        input = StringUtils.trim(input);
        if (StringUtils.isNullOrEmpty(input)) {
            return input;
        }

        int start = 0;
        while (start < input.length()) {
            if (!punctuation.contains(String.valueOf(input.charAt(start)))) {
                break;
            }
            ++start;
        }
        if (start > input.length() - 1) {
            return "";
        }
        int end = input.length() - 1;
        while (end > start) {
            if (!punctuation.contains(String.valueOf(input.charAt(end)))) {
                break;
            }
            --end;
        }
        input = input.substring(start, end + 1);
        return input;
    }

    public static boolean isAlpha(char ch) {
        /**
         * The following defines which characters are included in these sets. The values are Unicode code points.
         * - ALPHA
         *		- 0x0041 - 0x007A Basic Latin
         *		- 0x00C0 - 0x00D6 Latin-1 Supplement
         *		- 0x00D8 - 0x00F6 Latin-1 Supplement
         *		- 0x00F8 - 0x00FF Latin-1 Supplement
         *		- 0x0100 - 0x017F Latin Extended-A
         *		- 0x0180 - 0x024F Latin Extended-B
         *		- 0x0386          Greek
         *		- 0x0388 - 0x03FF Greek
         *		- 0x0400 - 0x0481 Cyrillic
         *		- 0x048A - 0x04FF Cyrillic
         *		- 0x0500 - 0x052F Cyrillic Supplement
         *		- 0x1E00 - 0x1EFF Latin Extended Additional
         */
        int codepoint = (int)ch;
        return (0x0041 <= codepoint && codepoint <= 0x007A) ||
                (0x00C0 <= codepoint && codepoint <= 0x00D6) ||
                (0x00D8 <= codepoint && codepoint <= 0x00F6) ||
                (0x00F8 <= codepoint && codepoint <= 0x00FF) ||
                (0x0100 <= codepoint && codepoint <= 0x017F) ||
                (0x0180 <= codepoint && codepoint <= 0x024F) ||
                (0x0386 == codepoint) ||
                (0x0388 <= codepoint && codepoint <= 0x03FF) ||
                (0x0400 <= codepoint && codepoint <= 0x0481) ||
                (0x048A <= codepoint && codepoint <= 0x04FF) ||
                (0x0500 <= codepoint && codepoint <= 0x052F) ||
                (0x1E00 <= codepoint && codepoint <= 0x1EFF);

    }

    public static boolean isUrl(String url) {
        return !isNullOrEmpty(url) && Patterns.WEB_URL.matcher(url).matches();
    }

    public static String safelyGetStr(String origin) {
        return StringUtils.isNullOrEmpty(origin) ? "" : origin;
    }

    public static boolean safelyEquals(String firstStr, String secondStr) {
        if (firstStr == null && secondStr == null) {
            return true;
        }
        if (firstStr == null || secondStr == null) {
            return false;
        }
        return firstStr.equals(secondStr);
    }

    public static boolean safelyContains(String src, String pattern) {
        if (isNullOrEmpty(src)) {
            return false;
        }
        return src.contains(pattern);
    }

    public static int getLength(String origin) {
        if (isNullOrEmpty(origin)) {
            return 0;
        }
        return origin.length();
    }

    public static int getTextWidth(Paint paint, String str) {
        int resultWidth = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                resultWidth += (int) Math.ceil(widths[j]);
            }
        }
        return resultWidth;
    }

    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS);
    }

    public static boolean isChinese(String s) {
        if (isNullOrEmpty(s)) {
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (isChinese(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAlpha(String s) {
        if (isNullOrEmpty(s)) {
            return false;
        }
        return isAlpha(s.charAt(0));
    }

    public static boolean isEquals(String s1, String s2) {
        if (StringUtils.isNullOrEmpty(s1) || StringUtils.isNullOrEmpty(s2)) {
            return false;
        }
        return s1.equals(s2);
    }

    public static String getBlankStr(String origin) {
        if (StringUtils.isNullOrEmpty(origin)) {
            return "";
        }
        return origin;
    }

    public static String getHtmlFormatString(String content) {
        if (content == null) {
            return null;
        }
        return content.replaceAll("\\<.*?>|\\n", "");
    }

    public static boolean isMatchCaseInsensitive(@NonNull String string, @NonNull String pattern) {
        String regexPattern = "(?i)" + pattern;
        return Pattern.compile(regexPattern).matcher(string).find();
    }

    /**
     * Reads a line from the specified file.
     * @param filename the file to read from
     * @return the first line, if any.
     * @throws IOException if the file couldn't be read
     */
    public static String readLine(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }

    public static int calculateSpaceNumForString(String str) {
        if (str == null) {
            return 0;
        }
        if (str.contains(" ")) {
            String[] ss = str.split(" ");
            return ss.length > 0 ? (ss.length - 1) : 0;
        }
        return 0;
    }

    public static String nonBlankValue(final String value, final String fallbackValue) {
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        return fallbackValue;
    }

    public static boolean isPhoneNumber(String phone) {
        if (isNullOrEmpty(phone)) {
            return false;
        }
        String regExp = "^[0-9]{7,11}$";
        Pattern p = Pattern.compile(regExp);
        Matcher matcher = p.matcher(phone);
        return matcher.matches();
    }

    public static boolean isEmail(String email) {
        if (isNullOrEmpty(email)) {
            return false;
        }
        String regExp = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(regExp);
        Matcher matcher = p.matcher(email);
        return matcher.matches();
    }

    public static String hidePartPhone(String phone) {
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    public static String hidePartEmail(String email) {
        return email.replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1****$3$4");
    }

    public static int lastNumberOffset(String str) {
        if (StringUtils.isNullOrEmpty(str)) {
            return -1;
        }
        Pattern pattern = Pattern.compile(END_WIDTH_NUMBER_REGEX);
        Matcher matcher = pattern.matcher(str);
        if (!matcher.find()) {
            return -1;
        }
        return matcher.start();
    }

    public static int findEndNumber(String str) {
        if (StringUtils.isNullOrEmpty(str)) {
            return -1;
        }
        int numberStart = StringUtils.lastNumberOffset(str);
        if (numberStart >= 0) {
            return NumberUtils.parseInt(str.substring(numberStart, str.length()));
        }
        return -1;
    }

    public static int findPrefixNumber(String str) {
        if (StringUtils.isNullOrEmpty(str.trim())) {
            return -1;
        }
        String notStartWithNumberRegex = "[^0-9].*";
        String prefixNumStr = str.replaceAll(notStartWithNumberRegex, "");
        if (StringUtils.isNullOrEmpty(prefixNumStr)) {
            return -1;
        }
        return NumberUtils.parseInt(prefixNumStr);
    }

    public static boolean parseBoolean(String s) {
        try {
            return Boolean.parseBoolean(s);
        } catch (Exception e) {
            return false;
        }
    }

    public static String removeNewlineSymbol(String content){
        if (isNotBlank(content)){
            content = content.replaceAll("\r\n","").replaceAll("\n", "");
        }
        return content;
    }

    public static String encodeHeaderValue(String value) {
        if (isNullOrEmpty(value)) {
            return "";
        }
        String newValue = value.replace("\n", "");
        try {
            newValue = URLEncoder.encode(newValue, "UTF-8");
        }catch (Exception e) {
            e.printStackTrace();
        }
        return newValue;
    }

    public static String subString(String str, int limitLength) {
        if (StringUtils.isNullOrEmpty(str)) {
            return "";
        }
        if (limitLength >= str.length()) {
            return str;
        }
        return substring(str, 0, limitLength);
    }

    public static String copyTitle(String originTitle, int titleIndex) {
        return originTitle + "(" + titleIndex + ")";
    }

    public static void appendTextToLastItem(List<String> list, String text) {
        if (list.isEmpty()) {
            list.add(text);
            return;
        }
        list.set(list.size() - 1, list.get(list.size() - 1) + text);
    }
}
