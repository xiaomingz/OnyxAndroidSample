package com.onyx.gallery.viewmodel

import com.onyx.gallery.handler.touch.TouchHandlerType

/**
 * Created by Leung on 2020/6/8
 */
class MosaicMenuViewModel : BaseMenuViewModel() {

    override fun updateTouchHandler() {
        globalEditBundle.touchHandlerManager.activateHandler(TouchHandlerType.MOSAIC)
        globalEditBundle.drawHandler.setRawDrawingRenderEnabled(false)
    }

}