package com.simplemobiletools.clock.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.simplemobiletools.clock.R

/**
 * <pre>
 *     author : suicheng
 *     time   : 2020/4/30 14:44
 *     desc   :
 * </pre>
 */
class StopwatchView : View {
    private val UPDATE_INTERVAL = 50L
    private val PROGRESS_STEP = 2f
    private val LENGTH_SCALE = 0.85f

    private lateinit var backgroundPaint: Paint
    private lateinit var circlePaint: Paint
    private lateinit var pointPaint: Paint

    private var clockLength = 0f;
    private var centerX = 0f
    private var centerY = 0f
    private var progressValue = 0f
    private var pointRadius = 18f

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        backgroundPaint.style = Paint.Style.STROKE
        backgroundPaint.strokeWidth = resources.getDimension(R.dimen.stopwatch_stoke)

        circlePaint = Paint(backgroundPaint)
        circlePaint.strokeWidth = resources.getDimension(R.dimen.stopwatch_circle)

        pointPaint = Paint(circlePaint)
        pointPaint.style = Paint.Style.FILL
        pointPaint.color = Color.WHITE
        pointRadius = resources.getDimension(R.dimen.stopwatch_point_radius)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measureWidth = measureDimension(widthMeasureSpec);
        val measureHeight = measureDimension(heightMeasureSpec)
        setMeasuredDimension(measureWidth, measureHeight)
    }

    private fun measureDimension(measureSpec: Int): Int {
        val defaultSize: Int = resources.getDimensionPixelSize(R.dimen.analog_clock_view_size)
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        return when (mode) {
            MeasureSpec.EXACTLY -> size
            MeasureSpec.AT_MOST -> Math.min(size, defaultSize)
            MeasureSpec.UNSPECIFIED -> defaultSize
            else -> defaultSize
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        clockLength = Math.min(w, h) / 2f * LENGTH_SCALE
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) {
            return
        }
        canvas.drawCircle(centerX, centerY, clockLength, backgroundPaint)
        canvas.translate(centerX, centerY)
        canvas.rotate(progressValue)
        canvas.translate(0f, -clockLength)
        canvas.drawCircle(0f, 0f, pointRadius, pointPaint)
        canvas.drawCircle(0f, 0f, pointRadius, circlePaint)
    }

    private fun update() {
        progressValue += PROGRESS_STEP
        if (progressValue >= 360f) {
            progressValue = 0f
        }
        invalidate()
    }

    private fun running() {
        postDelayed(Runnable {
            update()
            running()
        }, UPDATE_INTERVAL)
    }

    fun pause() {
        handler?.removeCallbacksAndMessages(null)
    }

    fun stop() {
        pause()
        progressValue = 0f
        invalidate()
    }

    fun resume() {
        running()
    }
}