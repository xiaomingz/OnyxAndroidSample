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
import com.onyx.android.sdk.api.device.epd.EpdController
import com.onyx.android.sdk.api.device.epd.UpdateMode
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.utils.Debug
import com.onyx.gallery.R
import com.onyx.gallery.action.crop.RedoCropAction
import com.onyx.gallery.action.crop.UndoCropAction
import com.onyx.gallery.action.shape.RedoShapeAction
import com.onyx.gallery.action.shape.UndoShapeAction
import com.onyx.gallery.databinding.FragmentEditContentBinding
import com.onyx.gallery.event.result.LoadImageResultEvent
import com.onyx.gallery.event.result.SaveCropTransformResultEvent
import com.onyx.gallery.event.ui.*
import com.onyx.gallery.extensions.hideSoftInput
import com.onyx.gallery.handler.CropHandler
import com.onyx.gallery.helpers.PATH_URI
import com.onyx.gallery.request.AttachNoteViewRequest
import com.onyx.gallery.request.RendererToScreenRequest
import com.onyx.gallery.touch.ScribbleTouchDistributor
import com.onyx.gallery.viewmodel.BaseViewModel
import com.onyx.gallery.viewmodel.EditContentViewModel
import com.onyx.gallery.views.crop.HighlightView
import com.onyx.gallery.views.crop.RotateBitmap
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * Created by Leung on 2020/4/30
 */
class EditContentFragment : BaseFragment<FragmentEditContentBinding, EditContentViewModel>() {
    private var inFastMode = false
    private var isAttachHostView = false
    private var uri: Uri? = null
    private var maintainAspectRatio = false
    private val surfaceCallback by lazy { initSurfaceCallback() }

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
        val scribbleTouchDistributor = ScribbleTouchDistributor(editBundle)
        editBundle.insertTextHandler.bindEditText(binding.editText)
        binding.surfaceView.setOnTouchListener { _, event ->
            scribbleTouchDistributor.onTouchEvent(event)
        }
        binding.cropImageView.setOnCropRectChange(getCropHandler())
    }

    override fun onInitViewModel(context: Context, binding: FragmentEditContentBinding, rootView: View): EditContentViewModel {
        val editContentViewModel = ViewModelProvider(requireActivity(), BaseViewModel.ViewModeFactory(editBundle)).get(EditContentViewModel::class.java)
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
        ensureQuitFastMode()
        editBundle.release()
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
            drawHandler.isSurfaceCreated = true
            if (isAttachHostView) {
                editBundle.enqueue(RendererToScreenRequest(editBundle), null)
                return
            }
            attachHostView()
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            Debug.d(javaClass, "surfaceChanged")
            onScribbleLayoutChange()
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            Debug.d(javaClass, "surfaceDestroyed")
            drawHandler.isSurfaceCreated = false
        }
    }

    private fun attachHostView() {
        val request = AttachNoteViewRequest(editBundle, binding.surfaceView)
        editBundle.enqueue(request, object : RxCallback<AttachNoteViewRequest>() {
            override fun onNext(startScribbleRequest: AttachNoteViewRequest) {
                loadImage()
                isAttachHostView = true
            }
        })
    }

    private fun onScribbleLayoutChange() {

    }

    private fun loadImage() {
        binding.surfaceView.run {
            val rect = Rect(left, top, width, height)
            viewModel.loadImageToHostView(editBundle.imagePath, rect)
        }
    }

    private fun initMenu() {
        postEvent(InitMenuEvent())
    }

    private fun ensureQuitFastMode() {
        if (!inFastMode) {
            return
        }
        EpdController.applyApplicationFastMode(TAG, false, true)
        inFastMode = false
    }

    private fun openHandwriting() = drawHandler.touchHelper?.setRawDrawingEnabled(true)

    private fun getCropHandler(): CropHandler = editBundle.cropHandler

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onApplyFastModeEvent(event: ApplyFastModeEvent) {
        if (event.enable && !inFastMode) {
            EpdController.applyApplicationFastMode(TAG, true, false, UpdateMode.ANIMATION_QUALITY, Int.MAX_VALUE)
            inFastMode = true
        }
        if (!event.enable && inFastMode) {
            EpdController.applyApplicationFastMode(TAG, false, true, UpdateMode.ANIMATION_QUALITY, Int.MAX_VALUE)
            inFastMode = false
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onActivityWindowFocusChangedEvent(event: ActivityWindowFocusChangedEvent) {
        getTouchHandler()?.onActivityWindowFocusChanged(event.hasFocus)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onShowToastEvent(event: ShowToastEvent) {
        editBundle.enqueue(RendererToScreenRequest(editBundle), null)
        getTouchHandler()?.onShowToastEvent()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onHideToastEvent(event: HideToastEvent) {
        editBundle.enqueue(RendererToScreenRequest(editBundle), null)
        getTouchHandler()?.onHideToastEvent()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDirtyRenderNoteEvent(event: DirtyRenderNoteEvent) {
        editBundle.enqueue(RendererToScreenRequest(editBundle), null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNoFocusSystemDialogChanged(event: NoFocusSystemDialogChanged) {
        getTouchHandler()?.onNoFocusSystemDialogChanged(event.open)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSystemUIChangedEvent(event: SystemUIChangedEvent) {
        if (!event.open) {
            return
        }
        editBundle.enqueue(RendererToScreenRequest(editBundle), null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStatusBarChangedEvent(event: StatusBarChangedEvent) {
        if (!event.show) {
            return
        }
        editBundle.enqueue(RendererToScreenRequest(editBundle), null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFloatButtonChangedEvent(event: FloatButtonChangedEvent) {
        editBundle.enqueue(RendererToScreenRequest(editBundle), null)
        getTouchHandler()?.onFloatButtonChanged(event.active)
    }

    private fun getTouchHandler() = editBundle.touchHandlerManager.activateHandler

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFloatButtonMenuStateChangedEvent(event: FloatButtonMenuStateChangedEvent) {
        getTouchHandler()?.onFloatButtonMenuStateChanged(event.open)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUndoShapeEvent(event: UndoShapeEvent) {
        UndoShapeAction(editBundle).execute(null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRedoShapeEvent(event: RedoShapeEvent) {
        RedoShapeAction(editBundle).execute(null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUndoCropEvent(event: UndoCropEvent) {
        UndoCropAction(editBundle).execute(null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRedoCropEvent(event: RedoCropEvent) {
        RedoCropAction(editBundle).execute(null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoadImageResultEvent(event: LoadImageResultEvent) {
        if (event.isSuccess()) {
            initMenu()
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
        this.maintainAspectRatio = event.maintainAspectRatio
        binding.cropImageView.run {
            visibility = View.VISIBLE
            val imageRect = getCropHandler().getImageRect()
            val highlightView = makeCropBorder(imageRect, event.cropRect, maintainAspectRatio)
            highlightViews.clear()
            highlightViews.add(highlightView)
            highlightView?.setFocus(true)
            invalidate()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onStartRotateEvent(event: StartRotateEvent) {
        binding.cropImageView.run {
            onUpdateCropRectEvent(UpdateCropRectEvent(event.cropRect, maintainAspectRatio))
        }
    }

    private fun makeCropBorder(imageRect: Rect, cropRect: RectF, maintainAspectRatio: Boolean): HighlightView? {
        val imageBitmap = editBundle.drawHandler.getImageShapeBitmap()
        val rotateBitmap = RotateBitmap(imageBitmap, 0)
        binding.cropImageView.setImageRotateBitmapResetBase(rotateBitmap, false)
        val highlightView = HighlightView(binding.cropImageView)
        editBundle.cropHandler.xAxisMirror
        highlightView.setup(Matrix(), imageRect, cropRect, maintainAspectRatio)
        return highlightView
    }

}