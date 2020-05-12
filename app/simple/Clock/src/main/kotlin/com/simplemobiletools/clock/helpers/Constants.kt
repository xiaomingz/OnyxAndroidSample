package com.simplemobiletools.clock.helpers

import android.content.Context
import com.simplemobiletools.clock.extensions.getAllTimeZones
import java.util.*

// shared preferences
const val SHOW_SECONDS = "show_seconds"
const val SELECTED_TIME_ZONES = "selected_time_zones"
const val EDITED_TIME_ZONE_TITLES = "edited_time_zone_titles"
const val TIMER_SECONDS = "timer_seconds"
const val TIMER_START_TIMESTAMP = "timer_timetamp"
const val TIMER_STATE = "timer_state"
const val TIMER_VIBRATE = "timer_vibrate"
const val TIMER_SOUND_URI = "timer_sound_uri"
const val TIMER_SOUND_TITLE = "timer_sound_title"
const val TIMER_CHANNEL_ID = "timer_channel_id"
const val TIMER_LABEL = "timer_label"
const val TIMER_MAX_REMINDER_SECS = "timer_max_reminder_secs"
const val ALARM_MAX_REMINDER_SECS = "alarm_max_reminder_secs"
const val ALARM_LAST_CONFIG = "alarm_last_config"
const val USE_TEXT_SHADOW = "use_text_shadow"
const val INCREASE_VOLUME_GRADUALLY = "increase_volume_gradually"

const val TABS_COUNT = 4
const val EDITED_TIME_ZONE_SEPARATOR = ":"
const val ALARM_ID = "alarm_id"
const val DEFAULT_ALARM_MINUTES = 480
const val DEFAULT_MAX_ALARM_REMINDER_SECS = 300
const val DEFAULT_MAX_TIMER_REMINDER_SECS = 60

const val PICK_AUDIO_FILE_INTENT_ID = 9994
const val REMINDER_ACTIVITY_INTENT_ID = 9995
const val OPEN_ALARMS_TAB_INTENT_ID = 9996
const val UPDATE_WIDGET_INTENT_ID = 9997
const val OPEN_APP_INTENT_ID = 9998
const val ALARM_NOTIF_ID = 9998
const val TIMER_NOTIF_ID = 9999
const val TIMER_RUNNING_NOTIF_ID = 10000

const val OPEN_TAB = "open_tab"
const val TAB_CLOCK = 0
const val TAB_ALARM = 1
const val TAB_STOPWATCH = 2
const val TAB_TIMER = 3

const val SORT_BY_LAP = 1
const val SORT_BY_LAP_TIME = 2
const val SORT_BY_TOTAL_TIME = 4

fun getDefaultTimeZoneTitle(context: Context, id: Int) = context.getAllTimeZones().firstOrNull { it.id == id }?.title ?: ""

fun getMSTillNextMinute(): Long {
    val calendar = Calendar.getInstance()
    return 60000L - calendar.get(Calendar.MILLISECOND) - calendar.get(Calendar.SECOND) * 1000
}

fun getPassedSeconds(): Int {
    val calendar = Calendar.getInstance()
    val isDaylightSavingActive = TimeZone.getDefault().inDaylightTime(Date())
    var offset = calendar.timeZone.rawOffset
    if (isDaylightSavingActive) {
        offset += TimeZone.getDefault().dstSavings
    }
    return ((calendar.timeInMillis + offset) / 1000).toInt()
}

fun formatTime(showSeconds: Boolean, use24HourFormat: Boolean, hours: Int, minutes: Int, seconds: Int): String {
    val hoursFormat = if (use24HourFormat) "%02d" else "%01d"
    var format = "$hoursFormat:%02d"

    return if (showSeconds) {
        format += ":%02d"
        String.format(format, hours, minutes, seconds)
    } else {
        String.format(format, hours, minutes)
    }
}
