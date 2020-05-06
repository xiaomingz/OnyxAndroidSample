package com.simplemobiletools.voicerecorder.activities

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.*
import com.simplemobiletools.voicerecorder.BuildConfig
import com.simplemobiletools.voicerecorder.R
import com.simplemobiletools.voicerecorder.helpers.GET_RECORDER_INFO
import com.simplemobiletools.voicerecorder.helpers.PAUSE_RECORDER
import com.simplemobiletools.voicerecorder.helpers.RESUME_RECORDER
import com.simplemobiletools.voicerecorder.helpers.STOP_AMPLITUDE_UPDATE
import com.simplemobiletools.voicerecorder.models.Events
import com.simplemobiletools.voicerecorder.services.RecorderService
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : SimpleActivity() {
    private val STATUS_STOP = 0
    private val STATUS_RECORDING = 1
    private val STATUS_PAUSE = 2

    private var bus: EventBus? = null
    private var status = STATUS_STOP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appLaunched(BuildConfig.APPLICATION_ID)

        handlePermission(PERMISSION_RECORD_AUDIO) {
            if (it) {
                tryInitVoiceRecorder()
            } else {
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val adjustedPrimaryColor = getAdjustedPrimaryColor()
        toggle_recording_button.apply {
            setImageDrawable(getToggleButtonIcon())
            background?.applyColorFilter(adjustedPrimaryColor)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bus?.unregister(this)

        Intent(this@MainActivity, RecorderService::class.java).apply {
            action = STOP_AMPLITUDE_UPDATE
            startService(this)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> launchSettings()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun tryInitVoiceRecorder() {
        if (isQPlus()) {
            initVoiceRecorder()
        } else {
            handlePermission(PERMISSION_WRITE_STORAGE) {
                if (it) {
                    initVoiceRecorder()
                } else {
                    finish()
                }
            }
        }
    }

    private fun initVoiceRecorder() {
        bus = EventBus.getDefault()
        bus!!.register(this)

        updateRecordingDuration(0)
        toggle_recording_button.setOnClickListener {
            toggleRecording()
        }
        recorder_list_button.setOnClickListener {
            startActivity(Intent(this, RecorderListActivity::class.java));
        }
        stop_recording_button.setOnClickListener {
            stopRecording()
        }

        Intent(this@MainActivity, RecorderService::class.java).apply {
            action = GET_RECORDER_INFO
            startService(this)
        }
    }

    private fun toggleRecording() {
        if (isStopStatus()) {
            startRecording()
            return
        }
        if (isRecording()) {
            pauseRecording()
            status = STATUS_PAUSE
        } else {
            resumeRecording()
            status = STATUS_RECORDING
        }
        updateRecordingButton()
    }

    private fun updateRecordingDuration(duration: Int) {
        recording_duration.text = duration.getFormattedDuration()
    }

    private fun startRecording() {
        status = STATUS_RECORDING
        stop_recording_button.visibility = View.VISIBLE
        startRecorderService(null)
    }

    private fun stopRecording() {
        Intent(this@MainActivity, RecorderService::class.java).apply {
            stopService(this)
        }
        status = STATUS_STOP
        stop_recording_button.visibility = View.GONE
        updateRecordingButton()
    }

    private fun pauseRecording() {
        startRecorderService(PAUSE_RECORDER)
    }

    private fun resumeRecording() {
        startRecorderService(RESUME_RECORDER)
    }

    private fun startRecorderService(setAction: String?) {
        Intent(this@MainActivity, RecorderService::class.java).apply {
            action = setAction
            startService(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun gotDurationEvent(event: Events.RecordingDuration) {
        updateRecordingDuration(event.duration)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun gotStatusEvent(event: Events.RecordingStatus) {
        status = if(event.isRecording) STATUS_RECORDING else STATUS_STOP
        updateRecordingButton()
        if (isRecording()) {
            recording_status.setText(R.string.recording)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun gotAmplitudeEvent(event: Events.RecordingAmplitude) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordingDoneEvent(event: Events.RecordingDone) {
        recording_status.setText(resources.getString(R.string.recording_saved_successfully, event.path))
    }

    private fun getToggleButtonIcon(): Drawable {
        var drawable = R.drawable.ic_start_vector
        if(isRecording()) {
            drawable  = R.drawable.ic_pause_vector
        }
        return resources.getDrawable(drawable)
    }

    private fun launchSettings() {
        startActivity(Intent(applicationContext, SettingsActivity::class.java))
    }

    private fun updateRecordingButton() {
        toggle_recording_button.setImageDrawable(getToggleButtonIcon())
    }

    private fun isStopStatus() : Boolean {
        return status == STATUS_STOP
    }

    private fun isRecording(): Boolean {
        return status == STATUS_RECORDING
    }
}
