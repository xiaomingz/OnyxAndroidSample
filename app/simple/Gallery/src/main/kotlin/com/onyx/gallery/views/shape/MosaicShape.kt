package com.onyx.gallery.views.shape

import com.onyx.gallery.utils.ExpandShapeFactory

/**
 * Created by Leung 2020/7/18 10:32
 **/
class MosaicShape : ImageTrackShape() {
    override fun getType(): Int = ExpandShapeFactory.SHAPE_MOSAIC
}