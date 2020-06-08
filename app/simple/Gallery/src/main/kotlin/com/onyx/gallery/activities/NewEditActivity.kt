package com.onyx.gallery.activities

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.onyx.android.sdk.utils.EventBusUtils
import com.onyx.gallery.R
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.event.result.SaveEditPictureResultEvent
import com.onyx.gallery.extensions.replaceLoadFragment
import com.onyx.gallery.fragments.EditContentFragment
import com.onyx.gallery.fragments.EditMenuFragment
import com.onyx.gallery.handler.ActionType
import com.onyx.gallery.handler.AppBarHandler
import com.simplemobiletools.commons.extensions.checkAppSideloading
import com.simplemobiletools.commons.extensions.getColoredDrawableWithColor
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Leung on 2020/4/30
 */
class NewEditActivity : SimpleActivity() {
    private lateinit var appBarHandler: AppBarHandler
    private val globalEditBundle: GlobalEditBundle = GlobalEditBundle.instance
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_edit)
        configActionBar()
        if (checkAppSideloading()) {
            return
        }
        globalEditBundle.parseIntent(this)
        EventBusUtils.ensureRegister(globalEditBundle.eventBus, this)
        replaceLoadFragment(R.id.content_layout, EditContentFragment.instance(globalEditBundle.uri))
        replaceLoadFragment(R.id.menu_layout, EditMenuFragment())
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusUtils.ensureUnregister(globalEditBundle.eventBus, this)
    }

    private fun configActionBar() {
        appBarHandler = AppBarHandler(this)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(resources.getColoredDrawableWithColor(R.drawable.ic_arrow_left_vector, Color.BLACK))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_new_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var actionType: ActionType = when (item.itemId) {
            android.R.id.home -> ActionType.BACK
            R.id.save -> ActionType.SAVE_EDIT
            R.id.delete -> ActionType.DELETE
            R.id.undo -> ActionType.UNDO
            R.id.redo -> ActionType.REDO
            else -> return super.onOptionsItemSelected(item)
        }
        appBarHandler.onHandleAction(actionType)
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSaveEditPictureResultEvent(event: SaveEditPictureResultEvent) = finish()

}