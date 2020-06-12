package com.onyx.gallery.viewmodel

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import com.onyx.android.sdk.scribble.shape.ShapeFactory
import com.onyx.android.sdk.utils.ResManager
import com.onyx.gallery.R
import com.onyx.gallery.action.shape.ShapeChangeAction
import com.onyx.gallery.models.MenuAction

/**
 * Created by Leung on 2020/5/6
 */

open class BaseMenuViewModel : BaseViewModel() {

    var isSupportBrushPen = MutableLiveData(true)
    var selectShapeAction = MutableLiveData(MenuAction.SCRIBBLE_BRUSH)
    var selectColorAction = MutableLiveData(MenuAction.NOTE_COLOR_BLACK)
    private var colorMapping: MutableMap<Int, MenuAction>? = null

    open fun onHandleMenu(action: MenuAction): Boolean = true

    open fun defaultShape(): Int = ShapeFactory.SHAPE_PENCIL_SCRIBBLE

    open fun defaultStrokeColor(): Int = Color.BLACK

    override fun onCleared() {
        super.onCleared()
        colorMapping?.run {
            clear()
        }
    }

    open fun getColorFromNoteMenuAction(action: MenuAction): Int {
        val map: Map<Int, MenuAction> = getColorMapping()
        for ((key, value) in map) {
            if (value === action) {
                return key
            }
        }
        return defaultStrokeColor()
    }

    private fun getColorMapping(): Map<Int, MenuAction> {
        if (colorMapping == null) {
            colorMapping = mutableMapOf()
            colorMapping!!.let {
                it[Color.BLACK] = MenuAction.NOTE_COLOR_BLACK
                it[ResManager.getColor(R.color.dark_gray_color)] = MenuAction.NOTE_COLOR_DARK_GREY
                it[ResManager.getColor(R.color.medium_gray_color)] = MenuAction.NOTE_COLOR_MEDIUM_GREY
                it[ResManager.getColor(R.color.light_gray_color)] = MenuAction.NOTE_COLOR_LIGHT_GREY
                it[Color.WHITE] = MenuAction.NOTE_COLOR_WHITE
                it[ResManager.getColor(R.color.shape_red_color)] = MenuAction.NOTE_COLOR_RED
                it[ResManager.getColor(R.color.shape_green_color)] = MenuAction.NOTE_COLOR_GREEN
                it[ResManager.getColor(R.color.shape_blue_color)] = MenuAction.NOTE_COLOR_BLUE
            }
        }
        return colorMapping!!
    }

    open fun getShapeTypeFromNoteMenuAction(menuAction: MenuAction): Int =
            when (menuAction) {
                MenuAction.SCRIBBLE_BRUSH -> ShapeFactory.SHAPE_BRUSH_SCRIBBLE
                MenuAction.SCRIBBLE_LINE -> ShapeFactory.SHAPE_LINE
                MenuAction.SCRIBBLE_TRIANGLE -> ShapeFactory.SHAPE_TRIANGLE
                MenuAction.SCRIBBLE_CIRCLE -> ShapeFactory.SHAPE_CIRCLE
                MenuAction.SCRIBBLE_RECTANGLE -> ShapeFactory.SHAPE_RECTANGLE
//                todo SCRIBBLE_ERASER_PART -> type = ShapeFactory.SHAPE_ERASER ,  SCRIBBLE_PENCIL -> type = ShapeFactory.SHAPE_PENCIL_SCRIBBLE
                else -> defaultShape()
            }

    open fun updateTouchHandler() {
        selectShapeAction.value?.let {
            ShapeChangeAction().setShapeType(getShapeTypeFromNoteMenuAction(it)).execute(null)
        }
    }

}