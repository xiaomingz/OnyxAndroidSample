package com.onyx.android.sdk.utils;

import android.graphics.Point;
import android.support.annotation.Nullable;

/**
 * <pre>
 *     author : liao lin tao
 *     time   : 2019/8/2 12:18
 *     desc   :
 * </pre>
 */
public class ArraysUtils {

    public static int[] toIntArray(Integer[] inArray) {
        int[] outArray = new int[inArray.length];
        for (int i = 0; i < outArray.length; i++) {
            outArray[i] = inArray[i];
        }
        return outArray;
    }

    @Nullable
    public static Point findFreeSpace(boolean[][] booleanArrays) {
        for (int x = 0; x < booleanArrays.length; x++) {
            for (int y = 0; y < booleanArrays[0].length; y++) {
                if (!booleanArrays[x][y]) {
                    return new Point(x, y);
                }
            }
        }
        return null;
    }

    @Nullable
    public static Point findFreeSpace(boolean[][] booleanArrays, int spanX, int spanY) {
        for (int x = 0; x < booleanArrays.length; x++) {
            for (int y = 0; y < booleanArrays[0].length; y++) {
                if (!booleanArrays[x][y] && !checkOccupied(booleanArrays, new Point(x, y), spanX, spanY)) {
                    return new Point(x, y);
                }
            }
        }
        return null;
    }

    public static boolean isTargetFreeSpace(boolean[][] booleanArrays, Point start, int spanX, int spanY) {
        if (start.y + spanY > booleanArrays[0].length || start.x + spanX > booleanArrays.length) {
            return false;
        }
        return !booleanArrays[start.x][start.y] && !checkOccupied(booleanArrays, start, spanX, spanY);
    }

    @Nullable
    public static Point checkAvailableFreeSpace(final boolean[][] occupied, int col, int row) {
        for (int x = 0; x < occupied.length; x++) {
            for (int y = 0; y < occupied[0].length; y++) {
                if (!occupied[x][y] && !checkOccupied(occupied, new Point(x, y), col, row)) {
                    setOccupied(occupied, true, x, y, col, row);
                    return new Point(x, y);
                }
            }
        }
        return null;
    }

    public static boolean checkOccupied(boolean[][] booleanArrays, Point start, int col, int row) {
        if (start.x + col > booleanArrays.length || start.y + row > booleanArrays[0].length) {
            return true;
        }
        for (int x = start.x; x < start.x + col; x++) {
            for (int y = start.y; y < start.y + row; y++) {
                if (booleanArrays[x][y]) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void setOccupied(boolean[][] booleanArrays, boolean b,
                                   int x, int y, int col, int row) {
        for (int i = x; i < x + col; i++) {
            for (int j = y; j < y + row; j++) {
                booleanArrays[i][j] = b;
            }
        }
    }
}
