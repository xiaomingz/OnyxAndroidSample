package com.onyx.gallery.activities

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.onyx.android.sdk.utils.EventBusUtils
import com.onyx.gallery.R
import com.onyx.gallery.action.SaveEditPictureAction
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.event.result.SaveEditPictureResultEvent
import com.onyx.gallery.extensions.replaceLoadFragment
import com.onyx.gallery.fragments.EditContentFragment
import com.onyx.gallery.fragments.EditMenuFragment
import com.simplemobiletools.commons.extensions.checkAppSideloading
import com.simplemobiletools.commons.extensions.getColoredDrawableWithColor
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Leung on 2020/4/30
 */
class NewEditActivity : SimpleActivity() {

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
        // todo R.id.undo ->  ,   R.id.redo , R.id.delete
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.save -> globalEditBundle.filePath?.let { SaveEditPictureAction(it).execute(null) }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSaveEditPictureResultEvent(event: SaveEditPictureResultEvent) = finish()

}