package com.onyx.android.sdk.utils;

import android.graphics.Matrix;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/8/8 18:20
 *     desc   :
 * </pre>
 */
public class MatrixUtils {

    public static boolean isEmptyMatrix(Matrix matrix) {
        return matrix == null || matrix.equals(new Matrix());
    }

    public static float[] getMatrixValue(Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);
        return values;
    }

}
