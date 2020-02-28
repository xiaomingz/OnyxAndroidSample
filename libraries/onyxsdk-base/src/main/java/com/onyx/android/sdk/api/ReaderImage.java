package com.onyx.android.sdk.api;

import android.graphics.Bitmap;
import android.graphics.RectF;

/**
 * Created by joy on 2/13/17.
 */

public interface ReaderImage {
    /**
     * image region on page
     * @return
     */
    RectF getRectangle();

    /**
     * source bitmap
     * @return
     */
    Bitmap getBitmap();

    /**
     * The index of a certain item in a page
     * @return
     */
    int getPos();
}
