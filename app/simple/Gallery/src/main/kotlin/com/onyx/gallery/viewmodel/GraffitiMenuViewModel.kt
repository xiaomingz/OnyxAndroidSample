package com.onyx.gallery.viewmodel

import com.onyx.gallery.action.StrokeColorChangeAction
import com.onyx.gallery.action.shape.ShapeChangeAction
import com.onyx.gallery.models.MenuAction

/**
 * Created by Leung on 2020/5/6
 */
class GraffitiMenuViewModel : BaseMenuViewModel() {

    override fun onHandleMenu(action: MenuAction): Boolean {
        super.onHandleMenu(action)
        when (action) {
            MenuAction.SCRIBBLE_BRUSH,
            MenuAction.SCRIBBLE_CIRCLE,
            MenuAction.SCRIBBLE_RECTANGLE,
            MenuAction.SCRIBBLE_TRIANGLE,
            MenuAction.SCRIBBLE_LINE -> onSelectShape(action)
            MenuAction.NOTE_COLOR_BLACK,
            MenuAction.NOTE_COLOR_DARK_GREY,
            MenuAction.NOTE_COLOR_MEDIUM_GREY,
            MenuAction.NOTE_COLOR_LIGHT_GREY,
            MenuAction.NOTE_COLOR_WHITE,
            MenuAction.NOTE_COLOR_RED,
            MenuAction.NOTE_COLOR_GREEN,
            MenuAction.NOTE_COLOR_BLUE -> onSelectColor(action)
            else -> return false
        }
        return true
    }

    private fun onSelectColor(action: MenuAction) = setSelectColorAction(action)

    private fun onSelectShape(action: MenuAction) = setSelectShapeAction(action)

    private fun setSelectShapeAction(action: MenuAction) {
        selectShapeAction.value = action
        ShapeChangeAction().setShapeType(getShapeTypeFromNoteMenuAction(action)).execute(null)
    }

    private fun setSelectColorAction(action: MenuAction) {
        selectColorAction.value = action
        StrokeColorChangeAction(getColorFromNoteMenuAction(action)).execute(null)
    }
}