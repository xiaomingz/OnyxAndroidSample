package com.onyx.gallery.fragments

import android.content.Context
import android.graphics.PixelFormat
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.onyx.android.sdk.api.device.epd.EpdController
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.utils.Debug
import com.onyx.gallery.R
import com.onyx.gallery.databinding.FragmentEditContentBinding
import com.onyx.gallery.event.result.LoadImageResultEvent
import com.onyx.gallery.helpers.PATH_URI
import com.onyx.gallery.request.AttachNoteViewRequest
import com.onyx.gallery.touch.ScribbleTouchDistributor
import com.onyx.gallery.viewmodel.EditContentViewModel
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * Created by Leung on 2020/4/30
 */
class EditContentFragment : BaseFragment<FragmentEditContentBinding>() {

    private var uri: Uri? = null
    private var inFastMode = false
    private val surfaceCallback: SurfaceHolder.Callback by lazy { initSurfaceCallback() }

    private lateinit var viewModel: EditContentViewModel

    private val TAG: String = EditContentFragment::class.java.getSimpleName()

    companion object {
        fun instance(uri: Uri?): EditContentFragment {
            val fragment = EditContentFragment()
            val bundle = Bundle()
            bundle.putParcelable(PATH_URI, uri)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun useEventBus(): Boolean = true

    override fun getLayoutId(): Int = R.layout.fragment_edit_content

    override fun onHandleArguments(bundle: Bundle) {
        super.onHandleArguments(bundle)
        uri = bundle.getParcelable(PATH_URI)
    }

    override fun onInitView(binding: FragmentEditContentBinding, contentView: View) {
        initSurfaceView()
        val scribbleTouchDistributor = ScribbleTouchDistributor()
        binding.surfaceView.setOnTouchListener { _, event ->
            scribbleTouchDistributor.onTouchEvent(event)
        }
    }

    override fun onInitViewModel(context: Context, binding: FragmentEditContentBinding, rootView: View) {
        viewModel = ViewModelProvider(this).get(EditContentViewModel::class.java)
        binding.run {
            viewModel = viewModel
            lifecycleOwner = this@EditContentFragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        globalEditBundle.drawHandler.quit()
        ensureQuitFastMode()
        binding.surfaceView.holder.removeCallback(surfaceCallback)
    }

    private fun initSurfaceView() {
        val surfaceHolder: SurfaceHolder = binding.surfaceView.holder
        surfaceHolder.setFormat(PixelFormat.TRANSLUCENT)
        surfaceHolder.addCallback(surfaceCallback)
    }

    private fun initSurfaceCallback(): SurfaceHolder.Callback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            Debug.d(javaClass, "surfaceCreated")
            attachHostView()
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            Debug.d(javaClass, "surfaceChanged")
            onScribbleLayoutChange()
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {}
    }

    private fun attachHostView() {
        if (binding.surfaceView === drawHandler.surfaceView) {
            return
        }
        val request = AttachNoteViewRequest(binding.surfaceView)
        globalEditBundle.enqueue(request, object : RxCallback<AttachNoteViewRequest>() {
            override fun onNext(startScribbleRequest: AttachNoteViewRequest) {
                loadImage()
            }
        })
    }

    private fun onScribbleLayoutChange() {

    }

    private fun ensureQuitFastMode() {
        if (!inFastMode) {
            return
        }
        EpdController.applyApplicationFastMode(TAG, false, true)
        inFastMode = false
    }

    private fun loadImage() {
        binding.surfaceView.run {
            val rect = Rect(left, top, width, height)
            viewModel.loadImageToHostView(globalEditBundle.filePath, rect)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoadImageResultEvent(event: LoadImageResultEvent) {
        if (event.isSuccess()) {
            openHandwriting()
        }
    }

    private fun openHandwriting() = drawHandler.touchHelper?.setRawDrawingEnabled(true)


}