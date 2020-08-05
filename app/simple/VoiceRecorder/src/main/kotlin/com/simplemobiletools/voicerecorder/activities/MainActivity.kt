package com.simplemobiletools.voicerecorder.activities

import android.content.Intent
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

    private var bus: EventBus? = null
    private var status = Events.RecordingStatus.STATUS_STOP

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
        } else {
            resumeRecording()
        }
    }

    private fun updateRecordingDuration(duration: Int) {
        recording_duration.text = duration.getFormattedDuration()
    }

    private fun startRecording() {
        startRecorderService(null)
    }

    private fun stopRecording() {
        Intent(this@MainActivity, RecorderService::class.java).apply {
            stopService(this)
        }
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
    fun onRecordingStatusEvent(event: Events.RecordingStatus) {
        updateRecordingStatus(event.status)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun gotAmplitudeEvent(event: Events.RecordingAmplitude) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRecordingDoneEvent(event: Events.RecordingDone) {
        recording_status.setText(resources.getString(R.string.recording_saved_successfully, event.path))
    }

    private fun launchSettings() {
        startActivity(Intent(applicationContext, SettingsActivity::class.java))
    }

    private fun updateRecordingStatus(status: Int) {
        this.status = status
        var text = ""
        when (status) {
            Events.RecordingStatus.STATUS_RECORDING ->  {
                text = getString(R.string.recording)
                stop_recording_button.visibility = View.VISIBLE;
                toggle_recording_button.setImageResource(R.drawable.ic_pause_vector)
            }
            Events.RecordingStatus.STATUS_PAUSE -> {
                if (getString(R.string.recording).equals(recording_status.text)) {
                    text = getString(R.string.pause_recording)
                    stop_recording_button.visibility = View.VISIBLE;
                    toggle_recording_button.setImageResource(R.drawable.ic_start_vector)
                }
            }
            Events.RecordingStatus.STATUS_STOP -> {
                stop_recording_button.visibility = View.GONE;
                toggle_recording_button.setImageResource(R.drawable.ic_start_vector)
            }
        }
        recording_status.text = text
    }

    private fun isStopStatus() : Boolean {
        return status == Events.RecordingStatus.STATUS_STOP
    }

    private fun isRecording(): Boolean {
        return status == Events.RecordingStatus.STATUS_RECORDING
    }
}
