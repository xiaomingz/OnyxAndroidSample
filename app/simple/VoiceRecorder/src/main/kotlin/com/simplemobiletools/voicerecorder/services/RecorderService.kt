package com.simplemobiletools.voicerecorder.services

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.*
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.media.MediaScannerConnection
import android.os.*
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
import com.simplemobiletools.voicerecorder.helpers.*
import com.simplemobiletools.voicerecorder.models.Events
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.IOException
import java.util.*


class RecorderService : Service() {
    private val AMPLITUDE_UPDATE_MS = 75L
    private val SAMPLING_RATE = 44100
    private val ENCODING_BITRATE = 128000

    private var currFilePath = ""
    private var duration = 0
    private var isRecording = false
    private var durationTimer = Timer()
    private var amplitudeTimer = Timer()
    private var recorder: MediaRecorder? = null
    private var handler = Handler(Looper.getMainLooper())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent.action) {
            GET_RECORDER_INFO -> broadcastRecorderInfo()
            STOP_AMPLITUDE_UPDATE -> amplitudeTimer.cancel()
            PAUSE_RECORDER -> pauseRecorder()
            RESUME_RECORDER -> resumeRecorder()
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
            setAudioSamplingRate(SAMPLING_RATE)
            setAudioEncodingBitRate(ENCODING_BITRATE)

            try {
                prepare()
                start()
                duration = 0
                isRecording = true
                recorder = this
                broadcastRecorderInfo()
                updateNotification(false, isRecording)

                durationTimer = Timer()
                durationTimer.scheduleAtFixedRate(getDurationUpdateTask(), 1000, 1000)

                startAmplitudeUpdates()
            } catch (e: IOException) {
                e.printStackTrace()
                showErrorToast(e)
                stopRecording()
            }
        }
    }

    private fun stopRecording() {
        durationTimer.cancel()
        amplitudeTimer.cancel()
        isRecording = false
        try {
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
        } catch (ignored: Exception) {
        }
        recorder = null
        updateNotification(true, isRecording)
        broadcastStatus()
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

    @TargetApi(Build.VERSION_CODES.N)
    private fun pauseRecorder() {
        if(!isRecording) {
            return
        }
        recorder?.pause()
        isRecording = false
        broadcastStatus()
        updateNotification(recorder == null, isRecording)
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun resumeRecorder() {
        recorder?.resume()
        isRecording = true
        broadcastStatus()
        updateNotification(recorder == null, isRecording)
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
            if(!isRecording) {
                return
            }
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
    private fun updateNotification(stop: Boolean, recording: Boolean) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (stop) {
            notificationManager.cancel(RECORDER_RUNNING_NOTIF_ID)
            return
        }
        val hideNotification = config.hideNotification
        val channelId = "simple_recorder"
        val label = getString(R.string.app_name)
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
        var text = if (recording) getString(R.string.recording) else getString(R.string.pause_recording)
        var visibility = NotificationCompat.VISIBILITY_PUBLIC

        if (hideNotification) {
            priority = Notification.PRIORITY_MIN
            icon = R.drawable.ic_empty
            title = ""
            text = ""
            visibility = NotificationCompat.VISIBILITY_SECRET
        }

        val pendIntent = PendingIntent.getActivity(this, System.currentTimeMillis().toInt(),
                Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(icon)
                .setContentIntent(getOpenAppIntent())
                .setPriority(priority)
                .setVisibility(visibility)
                .setSound(null)
                .setOngoing(true)
                .setAutoCancel(false)
                .setChannelId(channelId)
                .setContentIntent(pendIntent)

        val notification = builder.build()
        startForeground(RECORDER_RUNNING_NOTIF_ID, notification)
        handler.post {
            notificationManager.notify(RECORDER_RUNNING_NOTIF_ID, notification)
        }
    }

    private fun getOpenAppIntent(): PendingIntent {
        val intent = getLaunchIntent() ?: Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(this, RECORDER_RUNNING_NOTIF_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun broadcastDuration() {
        EventBus.getDefault().post(Events.RecordingDuration(duration))
    }

    private fun broadcastStatus() {
        var status = Events.RecordingStatus.STATUS_STOP
        if (recorder != null) {
            status = if (isRecording) Events.RecordingStatus.STATUS_RECORDING else Events.RecordingStatus.STATUS_PAUSE
        }
        EventBus.getDefault().post(Events.RecordingStatus(status))
    }

    private fun broadcastRecordingDone(path: String) {
        EventBus.getDefault().post(Events.RecordingDone(path))
    }
}
