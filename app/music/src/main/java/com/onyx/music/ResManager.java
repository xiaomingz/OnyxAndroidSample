package com.onyx.music;

import android.content.Context;
import android.util.TypedValue;

/**
 * @author Kaiguang
 * @Description
 * @Time 2019/8/8
 */
public class ResManager {
    public static float getFloat(Context context, int resId) {
        TypedValue outValue = new TypedValue();
        context.getResources().getValue(resId, outValue, true);
        return outValue.getFloat();
    }
}
