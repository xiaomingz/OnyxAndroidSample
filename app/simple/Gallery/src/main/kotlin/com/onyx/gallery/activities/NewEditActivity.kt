package com.onyx.gallery.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import com.onyx.android.sdk.utils.EventBusUtils
import com.onyx.gallery.R
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.databinding.ActivityNewEditBinding
import com.onyx.gallery.event.result.SaveCropTransformResultEvent
import com.onyx.gallery.event.result.SaveEditPictureResultEvent
import com.onyx.gallery.event.ui.ActivityWindowFocusChangedEvent
import com.onyx.gallery.event.ui.ShowSaveCropMenuEvent
import com.onyx.gallery.event.ui.UpdateOptionsMenuEvent
import com.onyx.gallery.extensions.replaceLoadFragment
import com.onyx.gallery.fragments.EditContentFragment
import com.onyx.gallery.fragments.EditMenuFragment
import com.onyx.gallery.handler.ActionType
import com.onyx.gallery.handler.AppBarHandler
import com.onyx.gallery.handler.touch.CropTouchHandler
import com.simplemobiletools.commons.extensions.checkAppSideloading
import kotlinx.android.synthetic.main.view_action_bar.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * Created by Leung on 2020/4/30
 */
class NewEditActivity : SimpleActivity() {
    private lateinit var binding: ActivityNewEditBinding
    private lateinit var appBarHandler: AppBarHandler
    private var globalEditBundle: GlobalEditBundle? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_edit)
        configActionBar()
        showImageEditMenu()
        if (checkAppSideloading()) {
            return
        }
        globalEditBundle = GlobalEditBundle.instance
        globalEditBundle?.run {
            parseIntent(this@NewEditActivity)
            EventBusUtils.ensureRegister(eventBus, this@NewEditActivity)
            replaceLoadFragment(R.id.content_layout, EditContentFragment.instance(uri))
            replaceLoadFragment(R.id.menu_layout, EditMenuFragment())
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onShowSaveCropMenuEvent(event: ShowSaveCropMenuEvent) {
        showCropMenu(true)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSaveCropTransformResultEvent(event: SaveCropTransformResultEvent) {
        if (event.isSuccess()) {
            showCropMenu(false)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateOptionsMenuEvent(event: UpdateOptionsMenuEvent) {
        updateMenu()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSaveEditPictureResultEvent(event: SaveEditPictureResultEvent) {
        if (event.isSuccess() && event.isExit) {
            setResult(Activity.RESULT_OK, Intent())
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusUtils.ensureUnregister(globalEditBundle?.eventBus, this)
        globalEditBundle = null
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        globalEditBundle?.eventBus?.post(ActivityWindowFocusChangedEvent(hasFocus))
    }

    private fun configActionBar() {
        appBarHandler = AppBarHandler(this)
        window.statusBarColor = Color.TRANSPARENT
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
        tvTitle.setText(R.string.editor)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            appBarHandler.onBackPressed()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun updateMenu() {
        globalEditBundle?.run {
            val isShowCropMenu = touchHandlerManager.activateHandler is CropTouchHandler
            showCropMenu(isShowCropMenu)
        }
    }

    override fun onBlackClick(view: View) {
        appBarHandler.onHandleAction(ActionType.BACK)
    }

    override fun onUndoClick(view: View) {
        super.onUndoClick(view)
        appBarHandler.onHandleAction(ActionType.UNDO)
    }

    override fun onRedoClick(view: View) {
        super.onRedoClick(view)
        appBarHandler.onHandleAction(ActionType.REDO)
    }

    override fun onSaveClick(view: View) {
        super.onSaveClick(view)
        appBarHandler.onHandleAction(ActionType.SAVE_EDIT)
    }

    override fun onOkClick(view: View) {
        super.onOkClick(view)
        appBarHandler.onHandleAction(ActionType.OK)
    }

}