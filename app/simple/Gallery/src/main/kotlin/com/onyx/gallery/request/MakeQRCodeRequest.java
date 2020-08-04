package com.onyx.gallery.request;

import android.graphics.Bitmap;

import com.onyx.android.sdk.rx.RxRequest;
import com.onyx.gallery.utils.QRCodeUtils;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/1/3 17:12
 *     desc   :
 * </pre>
 */
public class MakeQRCodeRequest extends RxRequest {

    private String url;
    private int width;
    private int height;

    private Bitmap bitmap;

    public MakeQRCodeRequest setUrl(String url) {
        this.url = url;
        return this;
    }

    public MakeQRCodeRequest setWidth(int width) {
        this.width = width;
        return this;
    }

    public MakeQRCodeRequest setHeight(int height) {
        this.height = height;
        return this;
    }

    @Override
    public void execute() throws Exception {
        bitmap = QRCodeUtils.createQRImage(url, width, height);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
