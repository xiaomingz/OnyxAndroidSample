package com.onyx.gallery.fragments

import android.content.Context
import android.graphics.Matrix
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.utils.Debug
import com.onyx.gallery.R
import com.onyx.gallery.action.RedoMosaicAction
import com.onyx.gallery.action.RedoShapeAction
import com.onyx.gallery.action.UndoMosaicAction
import com.onyx.gallery.action.UndoShapeAction
import com.onyx.gallery.databinding.FragmentEditContentBinding
import com.onyx.gallery.event.result.LoadImageResultEvent
import com.onyx.gallery.event.result.SaveCropTransformResultEvent
import com.onyx.gallery.event.ui.*
import com.onyx.gallery.extensions.hideSoftInput
import com.onyx.gallery.handler.CropHandler
import com.onyx.gallery.helpers.PATH_URI
import com.onyx.gallery.request.AttachNoteViewRequest
import com.onyx.gallery.touch.ScribbleTouchDistributor
import com.onyx.gallery.viewmodel.EditContentViewModel
import com.onyx.gallery.views.crop.HighlightView
import com.onyx.gallery.views.crop.RotateBitmap
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * Created by Leung on 2020/4/30
 */
class EditContentFragment : BaseFragment<FragmentEditContentBinding, EditContentViewModel>() {

    private var uri: Uri? = null
    private val surfaceCallback: SurfaceHolder.Callback by lazy { initSurfaceCallback() }

    private val TAG: String = EditContentFragment::class.java.simpleName

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
        globalEditBundle.insertTextHandler.bindEditText(binding.editText)
        binding.surfaceView.setOnTouchListener { _, event ->
            scribbleTouchDistributor.onTouchEvent(event)
        }
        binding.cropImageView.setOnCropRectChange(getCropHandler())
    }

    override fun onInitViewModel(context: Context, binding: FragmentEditContentBinding, rootView: View): EditContentViewModel {
        val editContentViewModel = ViewModelProvider(requireActivity()).get(EditContentViewModel::class.java)
        binding.run {
            viewModel = editContentViewModel
            lifecycleOwner = this@EditContentFragment
        }
        return editContentViewModel
    }

    override fun onPause() {
        super.onPause()
        requireActivity().hideSoftInput(binding.editText)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        globalEditBundle.release()
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
        val request = AttachNoteViewRequest(binding.surfaceView)
        globalEditBundle.enqueue(request, object : RxCallback<AttachNoteViewRequest>() {
            override fun onNext(startScribbleRequest: AttachNoteViewRequest) {
                loadImage()
            }
        })
    }

    private fun onScribbleLayoutChange() {

    }

    private fun loadImage() {
        binding.surfaceView.run {
            val rect = Rect(left, top, width, height)
            viewModel.loadImageToHostView(globalEditBundle.filePath, rect)
        }
    }

    private fun openHandwriting() = drawHandler.touchHelper?.setRawDrawingEnabled(true)

    private fun getCropHandler(): CropHandler = globalEditBundle.cropHandler

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUndoShapeEvent(event: UndoShapeEvent) {
        UndoShapeAction().execute(null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRedoShapeEvent(event: RedoShapeEvent) {
        RedoShapeAction().execute(null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUndoShapeEvent(event: UndoMosaicEvent) {
        UndoMosaicAction().execute(null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRedoShapeEvent(event: RedoMosaicEvent) {
        RedoMosaicAction().execute(null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoadImageResultEvent(event: LoadImageResultEvent) {
        if (event.isSuccess()) {
            openHandwriting()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSaveCropTransformResultEvent(event: SaveCropTransformResultEvent) {
        if (event.isSuccess()) {
            onCloseCropEvent(CloseCropEvent())
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOpenCrop(event: OpenCropEvent) {
        binding.cropImageView.visibility = View.VISIBLE
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCloseCropEvent(event: CloseCropEvent) {
        binding.cropImageView.run {
            visibility = View.INVISIBLE
            highlightViews.clear()
            invalidate()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateCropRectEvent(event: UpdateCropRectEvent) {
        binding.cropImageView.run {
            visibility = View.VISIBLE
            val imageRect = getCropHandler().getImageRect()
            val highlightView = makeCropBorder(imageRect, event.cropRect)
            highlightViews.clear()
            highlightViews.add(highlightView)
            highlightView?.setFocus(true)
            invalidate()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStartRotateEvent(event: StartRotateEvent) {
        binding.cropImageView.run {
            onUpdateCropRectEvent(UpdateCropRectEvent(event.cropRect))
        }
    }

    private fun makeCropBorder(imageRect: Rect, cropRect: RectF): HighlightView? {
        val imageBitmap = globalEditBundle.drawHandler.getImageBitmap()
        val rotateBitmap = RotateBitmap(imageBitmap, 0)
        binding.cropImageView.setImageRotateBitmapResetBase(rotateBitmap, false)
        val highlightView = HighlightView(binding.cropImageView)
        highlightView.setup(Matrix(), imageRect, cropRect, false)
        return highlightView
    }

}