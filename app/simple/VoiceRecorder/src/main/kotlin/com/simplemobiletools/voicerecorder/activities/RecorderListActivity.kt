package com.simplemobiletools.voicerecorder.activities

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.simplemobiletools.commons.extensions.toast
import com.simplemobiletools.commons.views.DashLineItemDivider
import com.simplemobiletools.voicerecorder.R
import com.simplemobiletools.voicerecorder.actions.FilesLoadAction
import com.simplemobiletools.voicerecorder.databinding.ActivityRecorderListBinding
import com.simplemobiletools.voicerecorder.dialogs.DialogMediaPlayer
import com.simplemobiletools.voicerecorder.dialogs.DialogMediaPlayer.PlayListener
import com.simplemobiletools.voicerecorder.helpers.MediaPlayerManager
import com.simplemobiletools.commons.views.DisableScrollLinearManager
import com.simplemobiletools.voicerecorder.adapters.RecorderListAdapter
import com.simplemobiletools.voicerecorder.view.PageRecyclerView
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
        binding.contentView.layoutManager = DisableScrollLinearManager(this)
        binding.contentView.addItemDecoration(DashLineItemDivider(this))
        adapter = RecorderListAdapter(this, binding.contentView) {
            play(it as File)
        }
        binding.contentView.adapter = adapter
        binding.contentView.setOnPagingListener(object : PageRecyclerView.OnPagingListener(){
            override fun onPageChange(position: Int, itemCount: Int, pageSize: Int) {
                updateIndicator()
            }
        })
    }

    fun updateCountText(size: Int) {
        binding.count.text = resources.getString(R.string.total, size)
    }

    fun updateIndicator() {
        val page: Int = binding.contentView.paginator.currentPage + 1
        val pageInfo = page.toString() + "/" + binding.contentView.paginator.pages()
        binding.indicator.text = if (binding.contentView.pageAdapter.dataCount > 0) pageInfo else ""
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
                    if (adapter?.isFirstOne(file) == true) {
                        toast(R.string.current_the_first_recording)
                        return
                    }
                    play(adapter?.getPrevItem(file))
                }

                override fun next() {
                    if (adapter?.isLastOne(file) == true) {
                        toast(R.string.current_the_last_recording)
                        return
                    }
                    play(adapter?.getNextItem(file))
                }


            }
            show()
        }
    }

    private fun updateContentView(list: List<File>) {
        runOnUiThread {
            adapter?.addItems(list);
            updateCountText(list.size)
            updateIndicator()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MediaPlayerManager.INSTANCE.stopPlay()
    }
}