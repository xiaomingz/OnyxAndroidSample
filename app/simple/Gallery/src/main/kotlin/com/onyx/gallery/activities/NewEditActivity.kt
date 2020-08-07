package com.onyx.gallery.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.onyx.android.sdk.utils.EventBusUtils
import com.onyx.gallery.R
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.databinding.ActivityNewEditBinding
import com.onyx.gallery.event.result.SaveEditPictureResultEvent
import com.onyx.gallery.event.ui.UpdateOptionsMenuEvent
import com.onyx.gallery.extensions.replaceLoadFragment
import com.onyx.gallery.fragments.EditContentFragment
import com.onyx.gallery.fragments.EditMenuFragment
import com.onyx.gallery.handler.ActionType
import com.onyx.gallery.handler.AppBarHandler
import com.onyx.gallery.handler.touch.CropTouchHandler
import com.simplemobiletools.commons.extensions.checkAppSideloading
import com.simplemobiletools.commons.extensions.getColoredDrawableWithColor
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * Created by Leung on 2020/4/30
 */
class NewEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewEditBinding
    private lateinit var appBarHandler: AppBarHandler
    private var globalEditBundle: GlobalEditBundle? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_edit)
        configActionBar()
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
    fun onUpdateOptionsMenuEvent(event: UpdateOptionsMenuEvent) {
        invalidateOptionsMenu()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSaveEditPictureResultEvent(event: SaveEditPictureResultEvent) {
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusUtils.ensureUnregister(globalEditBundle?.eventBus, this)
        globalEditBundle = null
    }

    private fun configActionBar() {
        appBarHandler = AppBarHandler(this)
        window.statusBarColor = Color.TRANSPARENT
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menuInflater.inflate(R.menu.menu_new_editor, menu)
        globalEditBundle?.run {
            val deleteMenuItem = menu.findItem(R.id.ok)
            val saveMenuItem = menu.findItem(R.id.save)
            val showCropMenu = touchHandlerManager.activateHandler is CropTouchHandler
            deleteMenuItem.setVisible(showCropMenu)
            saveMenuItem.setVisible(!showCropMenu)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val actionType: ActionType = when (item.itemId) {
            android.R.id.home -> ActionType.BACK
            R.id.ok -> ActionType.OK
            R.id.save -> ActionType.SAVE_EDIT
            R.id.undo -> ActionType.UNDO
            R.id.redo -> ActionType.REDO
            else -> return super.onOptionsItemSelected(item)
        }
        appBarHandler.onHandleAction(actionType)
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            appBarHandler.onBackPressed()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}