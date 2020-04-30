package com.simplemobiletools.clock.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec.*
import android.view.ViewGroup
import com.simplemobiletools.clock.R
import java.util.*


/**
 * <pre>
 *     author : suicheng
 *     time   : 2020/4/28 22:23
 *     desc   :
 * </pre>
 */
class AnalogClock : View {
    private val ONE_SECOND = 1000L
    private val RADIAN = 180
    private val SECOND_DEGREES = 6.0
    private val SCALE_HOUR_COUNT = 12
    private val SCALE_SECOND_COUNT = 5

    private val HOUR_POINTER_LENGTH = 0.5
    private val MIN_POINTER_LENGTH = 0.65
    private val SECOND_POINTER_LENGTH = 0.85
    private val TEXT_LENGTH = 0.78f

    private val CLOCK_LENGTH_SCALE = 2.2f
    private val HOUR_SCALE_LENGTH = 0.85f
    private val MIN_SCALE_LENGTH = 0.92f

    private lateinit var clockPaint: Paint
    private lateinit var hourScalePaint: Paint
    private lateinit var secondScalePaint: Paint
    private lateinit var digitalPaint: TextPaint
    private lateinit var pointPaint: Paint
    private lateinit var hourPaint: Paint
    private lateinit var minPaint: Paint
    private lateinit var secondPaint: Paint

    private var pointRadius = 20f
    private var clockLength = 0f
    private var centerX = 0f
    private var centerY = 0f
    private var hour = 0
    private var minute = 0
    private var second = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init()
    }

    private fun init() {
        clockPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        clockPaint.style = Paint.Style.STROKE
        clockPaint.strokeWidth = resources.getDimension(R.dimen.analog_clock_stoke)

        secondScalePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        secondScalePaint.style = (Paint.Style.STROKE)

        hourScalePaint = Paint(secondScalePaint)
        hourScalePaint.strokeWidth = clockPaint.strokeWidth;

        pointPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        pointPaint.style = Paint.Style.FILL

        hourPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        hourPaint.style = Paint.Style.STROKE
        hourPaint.strokeCap = Paint.Cap.ROUND
        hourPaint.strokeWidth = resources.getDimension(R.dimen.analog_clock_hour_stoke_width)

        minPaint = Paint(hourPaint)
        minPaint.strokeWidth = resources.getDimension(R.dimen.analog_clock_minutes_stoke_width)

        secondPaint = Paint(hourPaint)
        secondPaint.strokeWidth = resources.getDimension(R.dimen.analog_clock_second_stoke_width)

        digitalPaint = TextPaint(secondScalePaint)
        digitalPaint.textSize = resources.getDimension(R.dimen.analog_clock_text_width)
        digitalPaint.textAlign = Paint.Align.CENTER
        updateTime()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measureWidth = measureDimension(widthMeasureSpec);
        val measureHeight = measureDimension(heightMeasureSpec)
        setMeasuredDimension(measureWidth, measureHeight)
        if (measureWidth == AT_MOST && measureHeight == AT_MOST) {
            val layoutParams = layoutParams
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            setLayoutParams(layoutParams)
        }
    }

    private fun measureDimension(measureSpec: Int): Int {
        val defaultSize: Int = resources.getDimensionPixelSize(R.dimen.analog_clock_view_size)
        val mode = getMode(measureSpec)
        val size = getSize(measureSpec)
        return when (mode) {
            EXACTLY -> size
            AT_MOST -> Math.min(size, defaultSize)
            UNSPECIFIED -> defaultSize
            else -> defaultSize
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        clockLength = Math.min(w, h) / CLOCK_LENGTH_SCALE
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) {
            return
        }
        drawClock(canvas)
    }

    private fun drawClock(canvas: Canvas) {
        drawScale(canvas)
        drawTimeDigital(canvas)
        drawHourPoint(canvas)
        drawMinutePoint(canvas)
        drawSecondPoint(canvas)
    }

    private fun drawScale(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        canvas.drawCircle(centerX, centerY, clockLength, clockPaint)
        canvas.drawCircle(centerX, centerY, pointRadius, pointPaint)
        canvas.translate(centerX, centerY)
        canvas.save()
        for (i in 0..SCALE_HOUR_COUNT) {
            canvas.drawLine(0f, clockLength * HOUR_SCALE_LENGTH * -1, 0f, clockLength * -1, hourScalePaint)
            for (j in 1..SCALE_SECOND_COUNT) {
                canvas.drawLine(0f, clockLength * MIN_SCALE_LENGTH * -1, 0f, clockLength * -1, secondScalePaint)
                canvas.rotate(SECOND_DEGREES.toFloat())
            }
        }
        canvas.restore()
    }

    private fun drawTimeDigital(canvas: Canvas) {
        val fontMetrics = digitalPaint.getFontMetrics()
        val offset = (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
        val degree = 360f / SCALE_HOUR_COUNT;
        for (i in 0..SCALE_HOUR_COUNT) {
            val y = (Math.cos(Math.PI * i * degree / RADIAN) * -clockLength * TEXT_LENGTH).toFloat()
            val x = (Math.sin(Math.PI * i * degree / RADIAN) * clockLength * TEXT_LENGTH).toFloat()
            canvas.drawText("${if (i == 0) SCALE_HOUR_COUNT else i}", x, y + offset, digitalPaint)
        }
    }

    private fun drawHourPoint(canvas: Canvas) {
        val value: Double = hour % SCALE_HOUR_COUNT * (360.0 / SCALE_HOUR_COUNT) + minute / 2.0
        drawPointer(canvas, value, clockLength * HOUR_POINTER_LENGTH, hourPaint)
    }

    private fun drawMinutePoint(canvas: Canvas) {
        val value: Double = minute * SECOND_DEGREES + second * SECOND_DEGREES / 60.0
        drawPointer(canvas, value, clockLength * MIN_POINTER_LENGTH, minPaint)
    }

    private fun drawSecondPoint(canvas: Canvas) {
        drawPointer(canvas, second * SECOND_DEGREES, clockLength * SECOND_POINTER_LENGTH, secondPaint)
    }

    private fun drawPointer(canvas: Canvas, time: Double, clockLength: Double, paint: Paint) {
        val y = (Math.cos(Math.PI * time / RADIAN) * -clockLength).toFloat()
        val x = (Math.sin(Math.PI * time / RADIAN) * clockLength).toFloat()
        canvas.drawLine(0f, 0f, x, y, paint)
    }

    private fun updateTime() {
        hour = Calendar.getInstance().get(Calendar.HOUR)
        minute = Calendar.getInstance().get(Calendar.MINUTE)
        second = Calendar.getInstance().get(Calendar.SECOND)
        invalidate()
        postDelayed({
            updateTime()
        }, ONE_SECOND)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler?.removeCallbacksAndMessages(null)
    }
}