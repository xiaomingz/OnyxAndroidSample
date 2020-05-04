package com.simplemobiletools.voicerecorder.activities

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.simplemobiletools.voicerecorder.R
import com.simplemobiletools.voicerecorder.actions.FilesLoadAction
import com.simplemobiletools.voicerecorder.adapters.RecorderListAdapter
import com.simplemobiletools.voicerecorder.databinding.ActivityRecorderListBinding
import com.simplemobiletools.voicerecorder.helpers.MediaPlayerManager
import java.io.File

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

    private fun play(file: File) {
        MediaPlayerManager.INSTANCE.startPlay(file.absolutePath)
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