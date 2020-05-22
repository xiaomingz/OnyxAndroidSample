package com.simplemobiletools.voicerecorder.helpers

import android.media.MediaPlayer
import com.simplemobiletools.voicerecorder.models.Events
import org.greenrobot.eventbus.EventBus
import java.io.IOException

/**
 * <pre>
 *     author : suicheng
 *     time   : 2020/4/2 14:50
 *     desc   :
 * </pre>
 */
class MediaPlayerManager private constructor() {
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var isPause = false
    private var eventBus = EventBus();

    fun getEventBus(): EventBus {
        return eventBus
    }

    companion object {
        val INSTANCE: MediaPlayerManager = MediaPlayerManager()
    }

    private fun getMediaPlayer(path: String): MediaPlayer? {
        try {
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setDataSource(path)
            mediaPlayer!!.setOnCompletionListener {
                mediaPlayer?.release()
                isPlaying = false
                mediaPlayer = null
                getEventBus().post(Events.MediaPlayingFinishEvent())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return mediaPlayer
    }

    fun prepareAsync(path: String, listener: MediaPlayer.OnPreparedListener?) {
        if (isPlaying) {
            stopPlay()
        }
        try {
            mediaPlayer = getMediaPlayer(path)
            if (mediaPlayer == null) {
                return
            }
            mediaPlayer!!.setOnPreparedListener { mp ->
                eventBus.post(Events.MediaPlayingPreparedEvent())
                listener?.onPrepared(mp)
            }
            mediaPlayer!!.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun prepareAsync(path: String) {
        prepareAsync(path, null)
    }

    fun startPlay(path: String) {
        prepareAsync(path, MediaPlayer.OnPreparedListener { mp -> mp.start() })
        isPlaying = true
    }

    fun stopPlay() {
        if (mediaPlayer?.isPlaying() ?: false) {
            mediaPlayer?.stop()
        }
        mediaPlayer?.release()
        isPlaying = false
        isPause = false
        mediaPlayer = null
    }

    fun pausePlay() {
        if (mediaPlayer == null) {
            return
        }
        if (mediaPlayer!!.isPlaying()) {
            mediaPlayer!!.pause()
            isPlaying = false
            isPause = true
        }
    }

    fun resumePlay() {
        if (mediaPlayer == null) {
            return
        }
        if (isPause) {
            mediaPlayer?.start()
            isPause = false
            isPlaying = true
        }
    }

    fun isPlaying(): Boolean {
        return isPlaying
    }

    fun isPause(): Boolean {
        return isPause
    }

    fun getDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    fun getRemainingDuration(): Int {
        return getDuration() - getCurrentPosition()
    }

    fun seekTo(msec: Int) {
        mediaPlayer?.seekTo(msec)
    }
}