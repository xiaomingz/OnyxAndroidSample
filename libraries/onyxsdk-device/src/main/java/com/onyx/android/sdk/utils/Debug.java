package com.onyx.android.sdk.utils;

import android.text.TextUtils;
import android.util.Log;

import org.w3c.dom.Text;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Pattern;

/**
 * Created by Joy on 2016/5/13.
 */
public class Debug {
    private static final String TAG = Debug.class.getSimpleName();

    private static boolean debug = false;
    private static boolean obfuscateLogEnabled = false;
    private static Pattern obfuscatePattern = Pattern.compile("[a-z]+");

    public interface IMessage{
        String getMessage();
    }

    public static void setDebug(boolean debug) {
        Debug.debug = debug;
    }

    public static boolean getDebug() {
        return debug;
    }

    public static boolean isObfuscateLogEnabled() {
        return obfuscateLogEnabled;
    }

    public static void setObfuscateLogEnabled(boolean obfuscateLogEnabled) {
        Debug.obfuscateLogEnabled = obfuscateLogEnabled;
    }

    public static void d(final String msg) {
        if (debug) {
            Log.d(TAG, msg);
        }
    }

    public static void d(final IMessage msg) {
        if (debug) {
            Log.d(TAG, msg.getMessage());
        }
    }

    public static void d(Throwable tr) {
        if (debug) {
            Log.d(TAG, "", tr);
        }
    }

    public static void printStackTraceDebug(String msg) {
        printStackTraceDebug(TAG, msg);
    }

    public static void printStackTraceDebug(String tag, String msg) {
        if (debug) {
            String header = "Printing detailed debug information.\n "
                    + "Message :";
            Log.v(tag, "", new Exception(header + msg));
        }
    }

    public static void v(final Class<?> cls, final String msg, final Object... args) {
        if (debug) {
            Log.v(verifyTag(cls.getSimpleName()), formatString(msg, args));
        }
    }

    public static void v(final String tag, final String msg, final Object... args) {
        if (debug) {
            Log.v(tag, formatString(msg, args));
        }
    }

    public static void d(final Class<?> cls, final String msg, final Object... args) {
        if (debug) {
            Log.d(verifyTag(cls.getSimpleName()), formatString(msg, args));
        }
    }

    public static void d(final String tag, final String msg, final Object... args) {
        if (debug) {
            Log.d(tag, formatString(msg, args));
        }
    }

    public static void i(final String msg) {
        Log.i(TAG, obfuscateLog(msg));
    }

    public static void i(final Class<?> cls, final String msg, final Object... args) {
        String tag = obfuscateLog(verifyTag(cls.getSimpleName()));
        String str = obfuscateLog(formatString(msg, args));
        Log.i(tag, str);
    }

    public static String obfuscateLog(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return "";
        }
        if (isObfuscateLogEnabled()) {
            return obfuscatePattern.matcher(msg).replaceAll("");
        }
        return msg;
    }

    public static void w(final String msg) {
        Log.w(TAG, msg);
    }

    public static void w(final Class<?> cls, final String msg, final Object... args) {
        Log.w(verifyTag(cls.getSimpleName()), formatString(msg, args));
    }

    public static void w(final Throwable throwable) {
        Log.w(TAG, throwable);
    }

    public static void w(final Class<?> cls, final Throwable throwable) {
        Log.w(verifyTag(cls.getSimpleName()), throwable);
    }

    public static void e(final String msg) {
        if (msg != null) {
            Log.e(TAG, msg);
        }
    }

    public static void e(final String tag, final String msg, final Object... args) {
        Log.e(tag, formatString(msg, args));
    }

    public static void e(final Class<?> cls, final String msg, final Object... args) {
        Log.e(verifyTag(cls.getSimpleName()), formatString(msg, args));
    }

    public static void e(final Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        Log.e(TAG, sw.toString());
    }

    public static void e(final Class<?> cls, final Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        Log.e(verifyTag(cls.getSimpleName()), sw.toString());
    }

    private static String formatString(final String str, final Object... args) {
        if (str == null) {
            return "";
        }
        if (args.length <= 0) {
            return str;
        }
        try {
            return String.format(null, str, args);
        } catch (Throwable tr) {
            return str;
        }
    }

    private static String verifyTag(String tag) {
        if (tag != null && !TextUtils.isEmpty(tag.trim())) {
            return tag;
        }
        return TAG;
    }
}
