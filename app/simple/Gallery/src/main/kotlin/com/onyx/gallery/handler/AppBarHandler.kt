package com.onyx.gallery.handler

import androidx.appcompat.app.AppCompatActivity
import com.onyx.gallery.action.SaveEditPictureAction
import com.onyx.gallery.action.crop.SaveCropTransformAction
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.event.ui.UpdateTouchHandlerEvent

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
        globalEditBundle?.imagePath?.let {
            SaveEditPictureAction(hostActivity, it, {
                hostActivity.finish()
            }).execute(null)
        }
    }

    private fun saveTransform() {
        globalEditBundle.imagePath?.let { SaveCropTransformAction(it).execute(null) }
    }

    private fun saveEdit() {
        globalEditBundle.imagePath?.let {
            SaveEditPictureAction(hostActivity, it, {
                globalEditBundle.eventBus.post(UpdateTouchHandlerEvent())
            }).execute(null)
        }
    }

    private fun undo() {
        globalEditBundle.undo()
    }

    private fun redo() {
        globalEditBundle.redo()
    }

}