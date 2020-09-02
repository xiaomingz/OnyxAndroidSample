package com.onyx.gallery.request;


import com.onyx.gallery.bundle.EditBundle;
import com.onyx.gallery.common.BaseRequest;
import com.onyx.gallery.handler.DrawHandler;

import org.jetbrains.annotations.NotNull;

/**
 * Created by lxm on 2018/2/27.
 */

public class RendererToScreenRequest extends BaseRequest {

    public RendererToScreenRequest(@NotNull EditBundle editBundle) {
        super(editBundle);
    }

    @Override
    public void execute(DrawHandler drawHandler) throws Exception {
        setRenderToScreen(true);
    }
}
