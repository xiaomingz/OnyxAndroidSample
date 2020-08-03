package com.simplemobiletools.clock.dialogs

import android.app.AlertDialog
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import com.simplemobiletools.clock.R
import com.simplemobiletools.clock.activities.SimpleActivity
import com.simplemobiletools.clock.adapters.SelectTimeZonesAdapter
import com.simplemobiletools.clock.extensions.config
import com.simplemobiletools.clock.extensions.getAllTimeZones
import com.simplemobiletools.clock.models.MyTimeZone
import com.simplemobiletools.commons.extensions.setupDialogStuff
import kotlinx.android.synthetic.main.dialog_select_time_zones.view.*
import java.text.Collator
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashSet

class AddTimeZonesDialog(val activity: SimpleActivity, private val callback: () -> Unit) {
    private var view = activity.layoutInflater.inflate(R.layout.dialog_select_time_zones, null)
    private var originList: List<MyTimeZone>? = null
    private var searchList: MutableList<MyTimeZone> = ArrayList()

    init {
        view.select_time_zones_list.adapter = SelectTimeZonesAdapter(activity, getAllTimeZones().toMutableList())
        AlertDialog.Builder(activity)
                .setPositiveButton(R.string.ok) { dialog, which -> dialogConfirmed() }
                .setNegativeButton(R.string.cancel, null)
                .create().apply {
                    activity.setupDialogStuff(view, this)
                    view.title.setText(R.string.select_time_zone)
                    view.edit_input.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
                        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                            search(v.text.toString())
                            return@OnEditorActionListener true
                        }
                        return@OnEditorActionListener false
                    })
                }
    }

    private fun search(text: String) {
        searchList.clear()
        for (timeZone in getAllTimeZones()) {
            if (timeZone.title.contains(text)) {
                searchList.add(timeZone);
            }
        }
        val adapter = view?.select_time_zones_list?.adapter as? SelectTimeZonesAdapter
        adapter?.updateTimeZoneData(searchList)
    }

    private fun getAllTimeZones(): MutableList<MyTimeZone> {
        if (originList == null) {
            val collator = Collator.getInstance()
            originList = activity.getAllTimeZones().sortedWith(Comparator { o1, o2 ->
                var result = collator.compare(o1.indexString, o2.indexString)
                if (result == 0) {
                    result = collator.compare(o1.title, o2.title)
                }
                result
            })
        }
        return originList as MutableList<MyTimeZone>
    }

    private fun dialogConfirmed() {
        val adapter = view?.select_time_zones_list?.adapter as? SelectTimeZonesAdapter
        val selectedTimeZones = adapter?.selectedKeys?.map { it.toString() }?.toHashSet() ?: LinkedHashSet()
        activity.config.selectedTimeZones = selectedTimeZones
        callback()
    }
}
