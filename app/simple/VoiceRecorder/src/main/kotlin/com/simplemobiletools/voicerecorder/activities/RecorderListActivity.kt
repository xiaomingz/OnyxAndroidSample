package com.simplemobiletools.voicerecorder.activities

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.simplemobiletools.voicerecorder.R
import com.simplemobiletools.voicerecorder.actions.FilesLoadAction
import com.simplemobiletools.voicerecorder.adapters.RecorderListAdapter
import com.simplemobiletools.voicerecorder.databinding.ActivityRecorderListBinding
import com.simplemobiletools.voicerecorder.dialogs.DialogMediaPlayer
import com.simplemobiletools.voicerecorder.helpers.MediaPlayerManager
import kotlinx.android.synthetic.main.dialog_media_player.*
import java.io.File
import com.simplemobiletools.voicerecorder.dialogs.DialogMediaPlayer.PlayListener as PlayListener

class RecorderListActivity : SimpleActivity() {

    private lateinit var binding: ActivityRecorderListBinding;
    private var adapter: RecorderListAdapter? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recorder_list)
        initView()
        loadData()
    }

    private fun initView() {
        binding.contentView.layoutManager = LinearLayoutManager(this)
        adapter = RecorderListAdapter(this, binding.contentView) {
            play(it as File)
        }
        binding.contentView.adapter = adapter
    }

    private fun loadData() {
        FilesLoadAction().execute(this) {
            updateContentView(it)
        }
    }

    private fun play(targetFile: File) {
        DialogMediaPlayer(this, targetFile).apply {
            listener = object : PlayListener {
                override fun prev() {
                    play(adapter?.getPrevItem(file))
                }

                override fun next() {
                    play(adapter?.getNextItem(file))
                }
            }
            show()
        }
    }

    private fun updateContentView(list: List<File>) {
        runOnUiThread {
            adapter?.addItems(list);
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MediaPlayerManager.INSTANCE.stopPlay()
    }
}