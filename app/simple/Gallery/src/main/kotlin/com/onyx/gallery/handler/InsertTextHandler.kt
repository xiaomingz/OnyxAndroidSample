package com.onyx.gallery.handler

import android.graphics.PointF
import android.text.Editable
import android.text.TextUtils
import android.view.View
import android.view.ViewConfiguration
import android.widget.EditText
import androidx.annotation.NonNull
import com.onyx.android.sdk.data.FontInfo
import com.onyx.android.sdk.pen.data.TouchPoint
import com.onyx.android.sdk.rx.ObservableHolder
import com.onyx.android.sdk.rx.RxCallback
import com.onyx.android.sdk.rx.SingleThreadScheduler
import com.onyx.android.sdk.scribble.data.SelectionRect
import com.onyx.android.sdk.scribble.data.ShapeTransformAction
import com.onyx.android.sdk.scribble.shape.EditTextShape
import com.onyx.android.sdk.scribble.shape.Shape
import com.onyx.android.sdk.utils.DimenUtils
import com.onyx.android.sdk.utils.ResManager
import com.onyx.android.sdk.utils.StringUtils
import com.onyx.gallery.R
import com.onyx.gallery.action.textInput.CreateCursorShapeByOffsetAction
import com.onyx.gallery.action.textInput.CreateCursorShapeByTouchPointAction
import com.onyx.gallery.bundle.GlobalEditBundle
import com.onyx.gallery.extensions.hideSoftInput
import com.onyx.gallery.extensions.showSoftInput
import com.onyx.gallery.helpers.InsertTextConfig
import com.onyx.gallery.helpers.TextWatcherAdapter
import com.onyx.gallery.request.textInput.AdjustTextInputWidthRequest
import com.onyx.gallery.request.textInput.RenderInputTextShapeRequest
import com.onyx.gallery.request.textInput.SaveTextShapesRequest
import com.onyx.gallery.request.transform.TranslateRequest
import com.onyx.gallery.views.EditTextShapeExpand
import java.util.*
import kotlin.math.abs

/**
 * Created by Leung on 2020/6/8
 */
class InsertTextHandler(val globalEditBundle: GlobalEditBundle) : TextWatcherAdapter() {

    companion object {
        val TEXT_INPUT_SELECTION_BUFFER_TIME: Int = ResManager.getInteger(R.integer.selection_buffer_time)
    }

    var transformed = false
    var textShape: EditTextShape? = null
        set(value) {
            field = value
            val text = field?.text
            editTextView?.setText(text)
            editTextView?.setSelection(getTextSelection(text))
        }

    var selectionRect: SelectionRect? = null

    var lastPoint: TouchPoint? = null
    val insertTextConfig = InsertTextConfig()

    private var cursorOffset = 0
    private var cursorShape: Shape? = null
    private var editTextView: EditText? = null

    private var transformAction = ShapeTransformAction.Undefined
    private var selectionObservable: ObservableHolder<TouchPoint>? = null

    fun bindEditText(editText: EditText) {
        editTextView = editText
        editTextView?.addTextChangedListener(this)
    }

    private fun unBindEditText() = editTextView?.removeTextChangedListener(this)

    override fun afterTextChanged(s: Editable?) {
        onTextInput(s.toString(), editTextView!!.selectionStart)
    }

    private fun onTextInput(text: String?, selection: Int) {
        if (!canEdit() || text == null) {
            return
        }
        textShape!!.text = text
        cursorOffset = selection
        setCursorOffset(cursorOffset)
        updateCursorShapeByOffset(cursorOffset)
        renderInputTextShape(textShape!!)
    }

    fun onTouchDown(point: TouchPoint) {
        val downPoint = TouchPoint(point)
        lastPoint = downPoint
        transformAction = getTransformActionByPoint(downPoint)
        if (transformAction.isUndefined) {
            return
        }
        transformed = false
        updateCursorShapeByTouchPoint(point)
        selectionObservable = ObservableHolder<TouchPoint>()
        selectionObservable!!.setDisposable(selectionObservable!!.observable.buffer(TEXT_INPUT_SELECTION_BUFFER_TIME)
                .observeOn(SingleThreadScheduler.scheduler())
                .subscribeOn(SingleThreadScheduler.scheduler())
                .subscribe { touchPoints ->
                    transformed = true
                    val movedPoint = TouchPoint(touchPoints[touchPoints.size - 1])
                    transform(transformAction, movedPoint)
                })
    }

    fun onTouchMove(point: TouchPoint) {
        selectionObservable?.apply { onNext(point) }
    }

    fun onTouchUp(point: TouchPoint) {
        selectionObservable?.apply { dispose() }
        selectionObservable = null
        if (transformAction == ShapeTransformAction.Translate && !transformed) {
            onSingleTapUp(point)
            updateCursorShapeByTouchPoint(point)
            textShape?.let { renderInputTextShape(it) }
        }
    }

    private fun getTransformActionByPoint(point: TouchPoint): ShapeTransformAction {
        return if (selectionRect == null) {
            ShapeTransformAction.Undefined
        } else selectionRect!!.touchPointHitTest(point)
    }

    private fun onSingleTapUp(up: TouchPoint) {
        val touchSlop = ViewConfiguration.get(ResManager.getAppContext()).scaledTouchSlop
        val disX = abs(up.x - lastPoint!!.x).toInt()
        val disY = abs(up.y - lastPoint!!.y).toInt()
        if (disX < touchSlop && disY < touchSlop) {
            showSoftInput()
        }
    }

    private fun transform(transformAction: ShapeTransformAction, movedPoint: TouchPoint) {
        if (textShape == null) {
            return
        }
        when (transformAction) {
            ShapeTransformAction.ScaleX -> adjustTextInputWidth(textShape!!, movedPoint)
            ShapeTransformAction.Translate -> onTranslate(textShape!!, movedPoint)
        }
    }

    private fun adjustTextInputWidth(shape: Shape, movedPoint: TouchPoint) {
        val request = AdjustTextInputWidthRequest(shape, movedPoint, cursorShape!!, lastPoint!!)
        globalEditBundle.enqueue(request, object : RxCallback<AdjustTextInputWidthRequest?>() {
            override fun onNext(adjustTextInputWidthRequest: AdjustTextInputWidthRequest) {
                lastPoint = movedPoint
                updateCursorShapeByOffset(cursorOffset)
            }
        })
    }

    private fun updateCursorShapeByOffset(cursorOffset: Int) {
        val action: CreateCursorShapeByOffsetAction = CreateCursorShapeByOffsetAction()
                .setCursorOffset(cursorOffset)
                .setNormalizeScale(getNormalizeScale())
                .setTextShape(textShape)
                .setSelectionRect(selectionRect)
        action.execute(null)
        val cursorShape: Shape = action.cursorShape ?: return
        this.cursorShape = cursorShape
    }

    private fun onTranslate(shape: Shape, movedPoint: TouchPoint) {
        val dx = movedPoint.x - lastPoint!!.x
        val dy = movedPoint.y - lastPoint!!.y
        lastPoint = TouchPoint(movedPoint)
        val shapes: MutableList<Shape> = ArrayList()
        shapes.add(shape)
        if (!TextUtils.isEmpty(shape.text) && cursorShape != null) {
            shapes.add(cursorShape!!)
        }
        val request = TranslateRequest(shapes, PointF(dx, dy))
        globalEditBundle.enqueue(request, null)
    }

    private fun updateCursorShapeByTouchPoint(touchPoint: TouchPoint) {
        val touchPoint = getScreenTouchPoint(touchPoint)
        val action = CreateCursorShapeByTouchPointAction()
                .setNormalizeScale(getNormalizeScale())
                .setTextShape(textShape)
                .setSelectionRect(selectionRect)
                .setTouchPoint(touchPoint)
        action.execute(null)
        val cursorShape: Shape = action.cursorShape ?: return
        setCursorOffset(action.cursorOffset)
        this.cursorShape = cursorShape
    }

    private fun renderInputTextShape(shape: EditTextShape) {
        val request = RenderInputTextShapeRequest(shape)
        if (!TextUtils.isEmpty(shape.text)) {
            request.setCursorShape(cursorShape)
        }
        globalEditBundle.enqueue(request, null)
    }

    private fun setCursorOffset(cursorOffset: Int) {
        this.cursorOffset = cursorOffset
        editTextView?.setSelection(cursorOffset)
    }

    private fun getScreenTouchPoint(touchPoint: TouchPoint): TouchPoint {
        val point = TouchPoint(touchPoint)
        point.scale(getRendererScale())
        return point
    }

    private fun getRendererScale(): Float = globalEditBundle.drawHandler.drawingArgs.getRendererScale()

    private fun getNormalizeScale(): Float = globalEditBundle.drawHandler.drawingArgs.normalizeScale

    fun isUndefinedTransform(): Boolean = transformAction == ShapeTransformAction.Undefined

    fun isTranslateTransform(): Boolean = transformAction == ShapeTransformAction.Translate

    private fun canEdit(): Boolean = textShape?.textStyle != null

    fun showSoftInput() = editTextView?.let {
        it.visibility = View.VISIBLE
        ResManager.getAppContext().showSoftInput(it)
    }

    fun hideSoftInput() = editTextView?.let {
        ResManager.getAppContext().hideSoftInput(it)
        it.visibility = View.GONE
    }

    private fun getTextSelection(text: String?): Int = if (TextUtils.isEmpty(text)) {
        0
    } else {
        text!!.length
    }

    fun onTextSizeEvent(textSize: Float) {
        if (!canEdit()) {
            return
        }
        insertTextConfig.textSize = textSize
        val textStyle = textShape!!.textStyle
        textStyle.textSize = DimenUtils.pt2px(ResManager.getAppContext(), textSize)
        updateCursorShapeByOffset(cursorOffset)
        renderInputTextShape(textShape!!)
    }

    fun onTextBoldEvent(enableBold: Boolean) {
        if (!canEdit()) {
            return
        }
        insertTextConfig.bold = enableBold
        val textStyle = textShape!!.textStyle
        textStyle.isTextBold = enableBold
        renderInputTextShape(textShape!!)
    }

    fun onChangeColorEvent(color: Int) {
        if (!canEdit()) {
            return
        }
        insertTextConfig.textColor = color
        textShape!!.color = color
        renderInputTextShape(textShape!!)
    }

    fun onTextFontFaceEvent(fontInfo: FontInfo) {
        if (!canEdit()) {
            return
        }
        insertTextConfig.fontId = fontInfo.id
        insertTextConfig.fontFace = fontInfo.name
        val textStyle = textShape!!.textStyle
        textStyle.fontFace = fontInfo.id
        renderInputTextShape(textShape!!)
    }

    fun onTextIndentationEvent(isIndentation: Boolean) {
        if (!canEdit()) {
            return
        }
        (textShape!! as EditTextShapeExpand).isIndentation = isIndentation
        renderInputTextShape(textShape!!)
    }

    fun saveTextShape(clear: Boolean) {
        val textShape: Shape = textShape ?: return
        if (StringUtils.isNullOrEmpty(textShape.text)) {
            return
        }
        saveTextShape(textShape, clear)
    }

    private fun saveTextShape(textShape: Shape, clear: Boolean) {
        val request = SaveTextShapesRequest(mutableListOf(textShape))
        globalEditBundle.enqueue(request, object : RxCallback<SaveTextShapesRequest?>() {
            override fun onNext(@NonNull saveTextShapesRequest: SaveTextShapesRequest) {
                if (clear) {
                    clear()
                }
            }
        })
    }

    fun release() {
        unBindEditText()
        clear()
        insertTextConfig.reset()
    }

    fun clear() {
        lastPoint = null
        cursorShape = null
        hideSoftInput()
        clearTextShape()
        editTextView?.setText("")
        globalEditBundle.drawHandler.clearSelectionRect()
        transformAction = ShapeTransformAction.Undefined
    }

    private fun clearTextShape() {
        textShape = null
    }

}