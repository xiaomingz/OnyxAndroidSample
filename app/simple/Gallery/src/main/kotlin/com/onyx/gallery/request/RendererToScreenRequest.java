package com.onyx.gallery.request;


import com.onyx.gallery.common.BaseRequest;
import com.onyx.gallery.handler.DrawHandler;

/**
 * Created by lxm on 2018/2/27.
 */

public class RendererToScreenRequest extends BaseRequest {

    @Override
    public void execute(DrawHandler drawHandler) throws Exception {
        setRenderToScreen(true);
    }
}
