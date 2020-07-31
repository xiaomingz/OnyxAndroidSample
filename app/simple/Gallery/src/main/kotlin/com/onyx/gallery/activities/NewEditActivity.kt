package com.onyx.gallery.activities

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.onyx.gallery.R
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.databinding.ActivityNewEditBinding
import com.onyx.gallery.extensions.replaceLoadFragment
import com.onyx.gallery.fragments.EditContentFragment
import com.onyx.gallery.fragments.EditMenuFragment
import com.onyx.gallery.handler.ActionType
import com.onyx.gallery.handler.AppBarHandler
import com.simplemobiletools.commons.extensions.checkAppSideloading
import com.simplemobiletools.commons.extensions.getColoredDrawableWithColor

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
            replaceLoadFragment(R.id.content_layout, EditContentFragment.instance(uri))
            replaceLoadFragment(R.id.menu_layout, EditMenuFragment())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        globalEditBundle = null
    }

    private fun configActionBar() {
        appBarHandler = AppBarHandler(this)
        window.statusBarColor = Color.WHITE
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
        val actionType: ActionType = when (item.itemId) {
            android.R.id.home -> ActionType.BACK
            R.id.ok -> ActionType.OK
            R.id.save -> ActionType.SAVE_EDIT
            R.id.delete -> ActionType.DELETE
            R.id.undo -> ActionType.UNDO
            R.id.redo -> ActionType.REDO
            else -> return super.onOptionsItemSelected(item)
        }
        appBarHandler.onHandleAction(actionType)
        return true
    }

}