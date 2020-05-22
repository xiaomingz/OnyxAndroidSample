package com.simplemobiletools.voicerecorder.dialogs

import android.app.Dialog
import android.content.Context
import android.os.CountDownTimer
import android.view.*
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.databinding.DataBindingUtil
import com.simplemobiletools.commons.extensions.beInvisible
import com.simplemobiletools.commons.helpers.DATE_FORMAT_FOUR
import com.simplemobiletools.commons.helpers.TIME_FORMAT_24
import com.simplemobiletools.voicerecorder.R
import com.simplemobiletools.voicerecorder.databinding.DialogMediaPlayerBinding
import com.simplemobiletools.voicerecorder.helpers.MediaPlayerManager
import com.simplemobiletools.voicerecorder.models.Events
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * <pre>
 *     author : suicheng
 *     time   : 2020/5/6 12:20
 *     desc   :
 * </pre>
 */
class DialogMediaPlayer(context: Context, var file: File) : Dialog(context), View.OnClickListener {
    private val DATE_FORMAT = SimpleDateFormat("$DATE_FORMAT_FOUR $TIME_FORMAT_24", Locale.getDefault());

    private var timer: CountDownTimer? = null
    private lateinit var binding: DialogMediaPlayerBinding

    var listener: PlayListener? = null

    interface PlayListener {
        fun prev()
        fun next()
    }

    init {
        initView()
        fitDialogToWindow()
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_media_player, null, false)
        setContentView(binding.root)
        binding.play.setOnClickListener(this)
        binding.prev.setOnClickListener(this)
        binding.next.setOnClickListener(this)
        binding.mediaSeekBar.progress = 0
        binding.mediaSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                seekTo(seekBar.progress)
            }
        })
        setOnDismissListener {
            binding.mediaSeekBar.beInvisible()
            cancelTimer()
            MediaPlayerManager.INSTANCE.stopPlay()
        }
        setOnShowListener {
            binding.play.post { onMediaPlay() }
        }
        initFileInfo()
    }

    private fun initFileInfo() {
        binding.name.text = file.name
        binding.date.text = DATE_FORMAT.format(Date(file.lastModified()))
    }

    private fun seekTo(msec: Int) {
        if (MediaPlayerManager.INSTANCE.isPlaying()) {
            MediaPlayerManager.INSTANCE.seekTo(msec)
            countTime()
        }
    }

    override fun onClick(v: View) {
        when (v) {
            binding.play -> onMediaPlay()
            binding.prev -> listener?.prev()
            binding.next -> listener?.next()
        }
    }

    private fun onMediaPlay() {
        if (MediaPlayerManager.INSTANCE.isPlaying()) {
            MediaPlayerManager.INSTANCE.pausePlay()
            cancelTimer()
        } else if (MediaPlayerManager.INSTANCE.isPause()) {
            MediaPlayerManager.INSTANCE.resumePlay()
            countTime()
        } else {
            MediaPlayerManager.INSTANCE.startPlay(file.absolutePath)
        }
        onMediaPlayStateChanged()
    }

    private fun countTime() {
        cancelTimer()
        binding.mediaSeekBar.max = MediaPlayerManager.INSTANCE.getDuration()
        binding.mediaSeekBar.progress = MediaPlayerManager.INSTANCE.getCurrentPosition()
        val length: Int = MediaPlayerManager.INSTANCE.getRemainingDuration()
        timer = object : CountDownTimer(length.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.mediaSeekBar.progress = MediaPlayerManager.INSTANCE.getCurrentPosition()
                updateTime(MediaPlayerManager.INSTANCE.getCurrentPosition())
            }

            override fun onFinish() {
                binding.mediaSeekBar.progress = MediaPlayerManager.INSTANCE.getDuration()
                updateTime(MediaPlayerManager.INSTANCE.getDuration())
                onMediaPlayStateChanged()
            }
        }.start()
    }

    private fun cancelTimer() {
        timer?.cancel()
        timer = null
    }

    private fun updateTime(p: Int) {
        binding.progress.text = formatTime(p)
        binding.length.text = formatTime(MediaPlayerManager.INSTANCE.getDuration())
    }

    private fun onMediaPlayStateChanged() {
        if (MediaPlayerManager.INSTANCE.isPlaying()) {
            binding.play.setImageResource(R.drawable.ic_pause_vector)
        } else {
            binding.play.setImageResource(R.drawable.ic_play_vector)
        }
    }

    override fun onStart() {
        super.onStart()
        MediaPlayerManager.INSTANCE.getEventBus().register(this)
    }

    override fun onStop() {
        super.onStop()
        MediaPlayerManager.INSTANCE.getEventBus().unregister(this)
    }

    private fun fitDialogToWindow() {
        val mWindow: Window? = window
        mWindow ?: return
        val mParams = mWindow.attributes
        mParams.width = WindowManager.LayoutParams.MATCH_PARENT
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        mParams.gravity = Gravity.BOTTOM
        mWindow.attributes = mParams
        mWindow.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    private fun formatTime(millisecond: Int): String? {
        val totalSeconds = millisecond / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes,
                    seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMediaPlayerPrepared(event: Events.MediaPlayingPreparedEvent) {
        if (!MediaPlayerManager.INSTANCE.isPlaying()) {
            updateTime(0)
            return
        }
        countTime()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMediaPlayingFinishEvent(event: Events.MediaPlayingFinishEvent) {
        binding.progress.setText(R.string.init_time)
        binding.mediaSeekBar.progress = 0
        onMediaPlayStateChanged()
    }

    fun play(file: File?) {
        file ?: return
        this.file = file
        initFileInfo()
        cancelTimer()
        MediaPlayerManager.INSTANCE.startPlay(file.absolutePath)
        onMediaPlay()
    }
}