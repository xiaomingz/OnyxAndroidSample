package com.onyx.gallery.handler

import android.app.Activity
import com.onyx.gallery.action.crop.SaveCropTransformAction
import com.onyx.gallery.action.SaveEditPictureAction
import com.onyx.gallery.bundle.GlobalEditBundle

/**
 * Created by Leung on 2020/6/5
 */

enum class ActionType {
    BACK, OK, SAVE_EDIT, DELETE, REDO, UNDO
}

class AppBarHandler(private val hostActivity: Activity) {
    private val globalEditBundle: GlobalEditBundle = GlobalEditBundle.instance

    fun onHandleAction(actionType: ActionType) = when (actionType) {
        ActionType.BACK -> hostActivity.finish()
        ActionType.OK -> saveTransform()
        ActionType.SAVE_EDIT -> saveEdit()
        ActionType.DELETE -> delete()
        ActionType.UNDO -> undo()
        ActionType.REDO -> redo()
    }

    private fun saveTransform() {
        globalEditBundle.filePath?.let { SaveCropTransformAction(it).execute(null) }
    }

    private fun saveEdit() {
        globalEditBundle.filePath?.let { SaveEditPictureAction(it).execute(null) }
    }

    private fun delete() {

    }

    private fun undo() {
        globalEditBundle.undo()
    }

    private fun redo() {
        globalEditBundle.redo()
    }

}