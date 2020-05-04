package com.simplemobiletools.voicerecorder.services

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.*
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.os.Build
import android.os.IBinder
import android.provider.MediaStore
import androidx.core.app.NotificationCompat
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.ensureBackgroundThread
import com.simplemobiletools.commons.helpers.isOreoPlus
import com.simplemobiletools.commons.helpers.isQPlus
import com.simplemobiletools.voicerecorder.R
import com.simplemobiletools.voicerecorder.activities.MainActivity
import com.simplemobiletools.voicerecorder.extensions.config
import com.simplemobiletools.voicerecorder.extensions.getDefaultSaveFolder
import com.simplemobiletools.voicerecorder.helpers.GET_RECORDER_INFO
import com.simplemobiletools.voicerecorder.helpers.RECORDER_RUNNING_NOTIF_ID
import com.simplemobiletools.voicerecorder.helpers.STOP_AMPLITUDE_UPDATE
import com.simplemobiletools.voicerecorder.models.Events
import org.greenrobot.eventbus.EventBus
import java.io.IOException
import java.util.*

class RecorderService : Service() {
    private val AMPLITUDE_UPDATE_MS = 75L

    private var currFilePath = ""
    private var duration = 0
    private var isRecording = false
    private var durationTimer = Timer()
    private var amplitudeTimer = Timer()
    private var recorder: MediaRecorder? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent.action) {
            GET_RECORDER_INFO -> broadcastRecorderInfo()
            STOP_AMPLITUDE_UPDATE -> amplitudeTimer.cancel()
            else -> startRecording()
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRecording()
    }

    // mp4 output format with aac encoding should produce good enough mp3 files according to https://stackoverflow.com/a/33054794/1967672
    private fun startRecording() {
        val baseFolder = getDefaultSaveFolder()

        currFilePath = "$baseFolder/${getCurrentFormattedDateTime()}.mp3"
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(currFilePath)

            try {
                prepare()
                start()
                duration = 0
                isRecording = true
                broadcastRecorderInfo()
                startForeground(RECORDER_RUNNING_NOTIF_ID, showNotification())

                durationTimer = Timer()
                durationTimer.scheduleAtFixedRate(getDurationUpdateTask(), 1000, 1000)

                startAmplitudeUpdates()
            } catch (e: IOException) {
                showErrorToast(e)
                stopRecording()
            }
        }
    }

    private fun stopRecording() {
        durationTimer.cancel()
        amplitudeTimer.cancel()
        isRecording = false

        recorder?.apply {
            stop()
            release()

            ensureBackgroundThread {
                if (isQPlus()) {
                    addFileInNewMediaStore()
                } else {
                    addFileInLegacyMediaStore()
                }
            }
        }
        recorder = null
    }

    private fun broadcastRecorderInfo() {
        broadcastDuration()
        broadcastStatus()

        if (isRecording) {
            startAmplitudeUpdates()
        }
    }

    private fun startAmplitudeUpdates() {
        amplitudeTimer.cancel()
        amplitudeTimer = Timer()
        amplitudeTimer.scheduleAtFixedRate(getAmplitudeUpdateTask(), 0, AMPLITUDE_UPDATE_MS)
    }

    @SuppressLint("InlinedApi")
    private fun addFileInNewMediaStore() {
        //MediaStore.VOLUME_EXTERNAL_PRIMARY
        val audioCollection = MediaStore.Audio.Media.getContentUri("external_primary")

        val storeFilename = currFilePath.getFilenameFromPath()
        val newSongDetails = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, storeFilename)
            put(MediaStore.Audio.Media.TITLE, storeFilename)
            put(MediaStore.Audio.Media.MIME_TYPE, storeFilename.getMimeType())
        }

        val newUri = contentResolver.insert(audioCollection, newSongDetails)
        if (newUri == null) {
            toast(R.string.unknown_error_occurred)
            return
        }

        val outputStream = contentResolver.openOutputStream(newUri)
        val inputStream = getFileInputStreamSync(currFilePath)
        inputStream!!.copyTo(outputStream!!, DEFAULT_BUFFER_SIZE)
        recordingSavedSuccessfully(true)
    }

    private fun addFileInLegacyMediaStore() {
        MediaScannerConnection.scanFile(
            this,
            arrayOf(currFilePath),
            arrayOf(currFilePath.getMimeType())
        ) { _, _ -> recordingSavedSuccessfully(false) }
    }

    private fun recordingSavedSuccessfully(showFilenameOnly: Boolean) {
        broadcastRecordingDone(currFilePath)
    }

    private fun getDurationUpdateTask() = object : TimerTask() {
        override fun run() {
            duration++
            broadcastDuration()
        }
    }

    private fun getAmplitudeUpdateTask() = object : TimerTask() {
        override fun run() {
            if (recorder != null) {
                EventBus.getDefault().post(Events.RecordingAmplitude(recorder!!.maxAmplitude))
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun showNotification(): Notification {
        val hideNotification = config.hideNotification
        val channelId = "simple_recorder"
        val label = getString(R.string.app_name)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (isOreoPlus()) {
            val importance = if (hideNotification) NotificationManager.IMPORTANCE_MIN else NotificationManager.IMPORTANCE_DEFAULT
            NotificationChannel(channelId, label, importance).apply {
                setSound(null, null)
                notificationManager.createNotificationChannel(this)
            }
        }

        var priority = Notification.PRIORITY_DEFAULT
        var icon = R.drawable.ic_microphone_small
        var title = label
        var text = getString(R.string.recording)
        var visibility = NotificationCompat.VISIBILITY_PUBLIC

        if (hideNotification) {
            priority = Notification.PRIORITY_MIN
            icon = R.drawable.ic_empty
            title = ""
            text = ""
            visibility = NotificationCompat.VISIBILITY_SECRET
        }

        val builder = NotificationCompat.Builder(this)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(icon)
            .setContentIntent(getOpenAppIntent())
            .setPriority(priority)
            .setVisibility(visibility)
            .setSound(null)
            .setOngoing(true)
            .setAutoCancel(true)
            .setChannelId(channelId)

        return builder.build()
    }

    private fun getOpenAppIntent(): PendingIntent {
        val intent = getLaunchIntent() ?: Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(this, RECORDER_RUNNING_NOTIF_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun broadcastDuration() {
        EventBus.getDefault().post(Events.RecordingDuration(duration))
    }

    private fun broadcastStatus() {
        EventBus.getDefault().post(Events.RecordingStatus(isRecording))
    }

    private fun broadcastRecordingDone(path: String) {
        EventBus.getDefault().post(Events.RecordingDone(path))
    }
}
