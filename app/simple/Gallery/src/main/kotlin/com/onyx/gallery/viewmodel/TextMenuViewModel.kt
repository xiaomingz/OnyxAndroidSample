package com.onyx.gallery.viewmodel

import android.widget.SeekBar
import androidx.lifecycle.MutableLiveData
import com.onyx.gallery.models.MenuAction

/**
 * Created by Leung on 2020/6/5
 */
class TextMenuViewModel : BaseMenuViewModel() {

    val seekBarMax = 20
    private val minFontSize = 10f
    val currFontSize: MutableLiveData<Int> = MutableLiveData(10)
    val onChangeListener: SeekBar.OnSeekBarChangeListener by lazy { initOnSeekBarChangeListener() }

    private fun initOnSeekBarChangeListener(): SeekBar.OnSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            currFontSize.value = progress.coerceAtMost(seekBarMax).coerceAtLeast(minFontSize.toInt())
            globalEditBundle.drawHandler.setStrokeWidth(progress.toFloat())
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
        }
    }

    override fun onHandleMenu(action: MenuAction): Boolean {
        super.onHandleMenu(action)
        when (action) {
            else -> return false
        }
        return true
    }


}