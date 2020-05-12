package com.simplemobiletools.clock.dialogs

import androidx.appcompat.app.AlertDialog
import com.simplemobiletools.clock.R
import com.simplemobiletools.clock.activities.SimpleActivity
import com.simplemobiletools.clock.adapters.SelectTimeZonesAdapter
import com.simplemobiletools.clock.extensions.config
import com.simplemobiletools.clock.extensions.getAllTimeZones
import com.simplemobiletools.clock.models.MyTimeZone
import com.simplemobiletools.commons.extensions.setupDialogStuff
import kotlinx.android.synthetic.main.dialog_select_time_zones.view.*
import java.text.Collator
import kotlin.collections.LinkedHashSet

class AddTimeZonesDialog(val activity: SimpleActivity, private val callback: () -> Unit) {
    private var view = activity.layoutInflater.inflate(R.layout.dialog_select_time_zones, null)

    init {
        view.select_time_zones_list.adapter = SelectTimeZonesAdapter(activity, getAllTimeZones())
        AlertDialog.Builder(activity)
                .setPositiveButton(R.string.ok) { dialog, which -> dialogConfirmed() }
                .setNegativeButton(R.string.cancel, null)
                .create().apply {
                    activity.setupDialogStuff(view, this, R.string.select_time_zone)
                }
    }

    private fun getAllTimeZones(): List<MyTimeZone> {
        val collator = Collator.getInstance()
        return activity.getAllTimeZones().sortedWith(Comparator { o1, o2 ->
            var result = collator.compare(o1.indexString, o2.indexString)
            if (result == 0) {
                result = collator.compare(o1.title, o2.title)
            }
            result
        })
    }

    private fun dialogConfirmed() {
        val adapter = view?.select_time_zones_list?.adapter as? SelectTimeZonesAdapter
        val selectedTimeZones = adapter?.selectedKeys?.map { it.toString() }?.toHashSet() ?: LinkedHashSet()
        activity.config.selectedTimeZones = selectedTimeZones
        callback()
    }
}
