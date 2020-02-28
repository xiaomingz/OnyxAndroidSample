package com.onyx.android.sdk.utils;

import android.database.CursorWindow;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Field;

/**
 * Created by ming on 2017/7/21.
 */

public class DatabaseUtils {

    public static final String FIELD_CURSOR_WINDOW_SIZE = "sCursorWindowSize";
    public static final int INVALID_CURSOR_WINDOW_SIZE = -1;

    public static int getDBVersion(String databasePath) {
        SQLiteDatabase database = SQLiteDatabase.openDatabase(databasePath, null,SQLiteDatabase.OPEN_READONLY);
        int version = database.getVersion();
        database.close();
        return version;
    }

    public static boolean canRestoreDB(final String src, final String dst) {
        int srcDBVersion = DatabaseUtils.getDBVersion(src);
        int dstDBVersion = DatabaseUtils.getDBVersion(dst);
        return dstDBVersion >= srcDBVersion;
    }

    public static void setCursorWindowSize(int cursorWindowSize) {
        try {
            Field field = CursorWindow.class.getDeclaredField(FIELD_CURSOR_WINDOW_SIZE);
            field.setAccessible(true);
            field.set(null, cursorWindowSize);
        } catch (Exception e) {
            Debug.w(e);
        }
    }

    public static int getCursorWindowSize() {
        try {
            Field field = CursorWindow.class.getDeclaredField(FIELD_CURSOR_WINDOW_SIZE);
            field.setAccessible(true);
            return field.getInt(null);
        } catch (Exception e) {
            Debug.w(e);
            return INVALID_CURSOR_WINDOW_SIZE;
        }
    }
}
