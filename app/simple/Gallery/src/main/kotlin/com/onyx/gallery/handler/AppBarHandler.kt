package com.onyx.gallery.handler

import androidx.appcompat.app.AppCompatActivity
import com.onyx.gallery.action.SaveEditPictureAction
import com.onyx.gallery.action.crop.SaveCropTransformAction
import com.onyx.gallery.bundle.GlobalEditBundle

/**
 * Created by Leung on 2020/6/5
 */

enum class ActionType {
    BACK, OK, SAVE_EDIT, REDO, UNDO
}

class AppBarHandler(private val hostActivity: AppCompatActivity) {
    private val globalEditBundle: GlobalEditBundle = GlobalEditBundle.instance

    fun onHandleAction(actionType: ActionType) = when (actionType) {
        ActionType.BACK -> onBackPressed()
        ActionType.OK -> saveTransform()
        ActionType.SAVE_EDIT -> saveEdit()
        ActionType.UNDO -> undo()
        ActionType.REDO -> redo()
    }

    fun onBackPressed() {
        globalEditBundle?.filePath?.let { SaveEditPictureAction(hostActivity, it, true).execute(null) }
    }

    private fun saveTransform() {
        globalEditBundle.filePath?.let { SaveCropTransformAction(it).execute(null) }
    }

    private fun saveEdit() {
        globalEditBundle.filePath?.let { SaveEditPictureAction(hostActivity, it, false).execute(null) }
    }

    private fun undo() {
        globalEditBundle.undo()
    }

    private fun redo() {
        globalEditBundle.redo()
    }

}