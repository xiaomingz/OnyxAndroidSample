package com.onyx.android.sdk.utils;

import android.util.Log;

import com.onyx.android.sdk.data.AppDataInfo;
import com.onyx.android.sdk.data.SortOrder;

import java.text.Collator;
import java.util.Locale;

/**
 * Created by solskjaer49 on 14/10/23 15:24.
 */
public class ComparatorUtils {

    public static int stringComparator(String lhs, String rhs, SortOrder ascOrder) {
        int invalid = -1;
        int lhsNumber = StringUtils.findPrefixNumber(lhs);
        int rhsNumber = StringUtils.findPrefixNumber(rhs);
        if (lhsNumber == invalid) {
            lhsNumber = Integer.MAX_VALUE;
        }
        if (rhsNumber == invalid) {
            rhsNumber = Integer.MAX_VALUE;
        }
        int prefixNumberComparator = integerComparator(lhsNumber, rhsNumber, ascOrder);
        if (prefixNumberComparator != 0) {
            return prefixNumberComparator;
        }

        switch (ascOrder) {
            case Desc:
                return Collator.getInstance(Locale.CHINESE).compare(rhs, lhs);
            default:
                return Collator.getInstance(Locale.CHINESE).compare(lhs, rhs);
        }
    }

    public static int longComparator(long lhs, long rhs, SortOrder ascOrder) {
        switch (ascOrder) {
            case Desc:
                return rhs < lhs ? -1 : (lhs == rhs ? 0 : 1);
            default:
                return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
        }
    }

    public static int integerComparator(int lhs, int rhs, SortOrder ascOrder) {
        switch (ascOrder) {
            case Desc:
                return rhs < lhs ? -1 : (lhs == rhs ? 0 : 1);
            default:
                return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
        }
    }

    public static int booleanComparator(boolean lhs, boolean rhs, SortOrder ascOrder) {
        switch (ascOrder) {
            case Desc:
                return lhs == rhs ? 0 : rhs ? 1 : -1;
            default:
                return lhs == rhs ? 0 : lhs ? 1 : -1;
        }
    }

    public static int appDataInfoComparator(AppDataInfo lhs, AppDataInfo rhs, SortOrder sortOrder) {
        if (lhs.isSystemApp && !rhs.isSystemApp) {
            return 1;
        }
        if (!lhs.isSystemApp && rhs.isSystemApp) {
            return -1;
        }
        if (lhs.isSystemApp == rhs.isSystemApp) {
            return longComparator(lhs.lastUpdatedTime, rhs.lastUpdatedTime, sortOrder);
        }
        return 0;
    }
}
