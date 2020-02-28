/**
 *
 */
package com.onyx.android.sdk.utils;

import android.content.Context;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;

import com.onyx.android.sdk.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author joy
 */
public class DateTimeUtil {
    public static final SimpleDateFormat DATE_FORMAT_YYYYMMDD_HHMMSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    public static final SimpleDateFormat DATE_FORMAT_YYYYMMDD_HH_MM_SS = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss", Locale.getDefault());
    public static final SimpleDateFormat DATE_FORMAT_YYYYMMDD_HHMM = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    public static final SimpleDateFormat DATE_FORMAT_YYYYMMDD = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    public static final SimpleDateFormat DATE_FORMAT_YYYYMMDD_2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public static final SimpleDateFormat DATE_FORMAT_YYYYMMDD_T_HHMMSS_Z = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    public static final SimpleDateFormat DATE_FORMAT_YYYYMMDD_HHMMSS_FOR_FILE_NAME = new SimpleDateFormat("yyyy-MM-dd HH mm ss", Locale.getDefault());

    public static final SimpleDateFormat DATE_FORMAT_WEEK = new SimpleDateFormat("EEEE", Locale.getDefault());
    
    public static final String HOURS_12 = "12";
    public static final String HOURS_24 = "24";

    public static boolean isSystemHour24(Context context) {
        return HOURS_24.equals(Settings.System.getString(context.getContentResolver(), Settings.System.TIME_12_24));
    }

    /**
     * format the time according to the current locale and the user's 12-/24-hour clock preference
     *
     * @param context
     * @return
     */
    public static String getCurrentTimeString(Context context) {
        return DateFormat.getTimeFormat(context).format(new Date());
    }

    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd hh:mm:ss:SSS");
    }

    public static String getCurrentTime24Format() {
        return getCurrentTime("yyyy-MM-dd HH:mm:ss:SSS");
    }

    public static String getCurrentTime(String pattern) {
        Calendar cal = Calendar.getInstance();
        Date currentTime = cal.getTime();
        SimpleDateFormat sdFormat = new SimpleDateFormat(pattern);
        return sdFormat.format(currentTime);
    }

    public static Date getDayBegin() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        Date beginTime = calendar.getTime();
        return beginTime;
    }

    public static Date getDayEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        Date beginTime = calendar.getTime();
        return beginTime;
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMAT_YYYYMMDD_HHMMSS.format(date);
    }

    public static String formatDate(Date date, SimpleDateFormat simpleDateFormat) {
        if (date == null) {
            return "";
        }
        return simpleDateFormat.format(date);
    }

    public static long parse(String dateString, SimpleDateFormat simpleDateFormat) {
        return parse(dateString, simpleDateFormat, null);
    }

    public static long parse(String dateString, SimpleDateFormat simpleDateFormat, String timeZone) {
        if (!StringUtils.isNullOrEmpty(timeZone)) {
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        }
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date == null ? 0 : date.getTime();
    }

    public static String formatTime(Context context, long allSecond) {
        long hour_value = allSecond / 3600;
        long minute_value = allSecond % 3600 / 60;
        long second_value = allSecond % 3600 % 60;
        String whitespace_symbol = " ";

        String hour_symbol = context.getResources().getString(R.string.hour_symbol);
        String minute_symbol = context.getResources().getString(R.string.minute_symbol);
        String second_symbol = context.getResources().getString(R.string.second_symbol);

        if (hour_value > 0) {
            return hour_value + hour_symbol + whitespace_symbol + minute_value + minute_symbol;
        } else if (minute_value > 0) {
            return minute_value + minute_symbol + whitespace_symbol + second_value + second_symbol;
        } else {
            return second_value + second_symbol;
        }
    }

    public static String getBootUpTime() {
        long ut = SystemClock.elapsedRealtime() / 1000;

        if (ut == 0) {
            ut = 1;
        }
        return convert(ut);
    }

    private static String pad(int n) {
        if (n >= 10) {
            return String.valueOf(n);
        } else {
            return "0" + String.valueOf(n);
        }
    }

    private static String convert(long t) {
        int s = (int) (t % 60);
        int m = (int) ((t / 60) % 60);
        int h = (int) ((t / 3600));

        return h + ":" + pad(m) + ":" + pad(s);
    }

    public static int compareDate(@Nullable Date o1, @Nullable Date o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        return o1.compareTo(o2);
    }

    public static boolean laterThan(@Nullable Date o1, @Nullable Date o2) {
        return compareDate(o1, o2) > 0;
    }

    public static int hour(long time) {
        return (int) Math.ceil(time / 3600 / 1000);
    }
}
