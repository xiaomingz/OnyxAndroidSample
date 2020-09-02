package com.onyx.gallery.handler

import android.app.Activity
import android.content.Intent
import com.onyx.gallery.action.SaveEditPictureAction
import com.onyx.gallery.action.crop.SaveCropTransformAction
import com.onyx.gallery.activities.NewEditActivity
import com.onyx.gallery.bundle.EditBundle
import com.onyx.gallery.event.ui.UpdateTouchHandlerEvent

/**
 * Created by Leung on 2020/6/5
 */

enum class ActionType {
    BACK, OK, SAVE_EDIT, REDO, UNDO
}

class AppBarHandler(private val hostActivity: NewEditActivity) {

    private val editBundle: EditBundle = hostActivity.editBundle

    fun onHandleAction(actionType: ActionType) = when (actionType) {
        ActionType.BACK -> onBackPressed()
        ActionType.OK -> saveTransform()
        ActionType.SAVE_EDIT -> saveEdit()
        ActionType.UNDO -> undo()
        ActionType.REDO -> redo()
    }

    fun onBackPressed() {
        SaveEditPictureAction(editBundle, hostActivity, editBundle.imagePath, true) {
            hostActivity.setResult(Activity.RESULT_OK, Intent())
            hostActivity.finish()
        }.execute(null)
    }

    private fun saveTransform() {
        SaveCropTransformAction(editBundle).execute(null)
    }

    private fun saveEdit() {
        SaveEditPictureAction(editBundle, hostActivity, editBundle.imagePath) {
            hostActivity.editBundle.eventBus.post(UpdateTouchHandlerEvent())
        }.execute(null)
    }

    private fun undo() {
        hostActivity.editBundle.undo()
    }

    private fun redo() {
        hostActivity.editBundle.redo()
    }

}