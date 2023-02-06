package ru.netology.statsview.ui

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import ru.netology.statsview.R
import ru.netology.statsview.utils.AndroidUtils
import kotlin.math.min
import kotlin.random.Random
import ru.netology.statsview.R.color.empty_color

class StatsView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(
    context,
    attributeSet,
    defStyleAttr,
    defStyleRes
) {

    private var fontSize = AndroidUtils.dp(context, 20).toFloat()
    private var lineWidth = AndroidUtils.dp(context, 5)
    private var colors = emptyList<Int>()

    init {
        context.withStyledAttributes(attributeSet, R.styleable.StatsView) {
            fontSize = getDimension(R.styleable.StatsView_textSize, fontSize)
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth.toFloat()).toInt()

            colors = listOf(
                getColor(R.styleable.StatsView_color1, randomColor()),
                getColor(R.styleable.StatsView_color2, randomColor()),
                getColor(R.styleable.StatsView_color3, randomColor()),
                getColor(R.styleable.StatsView_color4, randomColor()),
            )

        }
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            invalidate()
            reInvalidate()
        }
    private var fullCircleDegrees = 360F
    private var radius = 0F
    private var center = PointF()
    private var oval = RectF()
    private var animator: Animator? = null
    private var move = 0F





    private val paint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        strokeWidth = lineWidth.toFloat()
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    @SuppressLint("ResourceAsColor")
    private val paintEmpty = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        strokeWidth = lineWidth.toFloat()
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        color =  empty_color //white
        alpha = 10
    }

    private val textPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = fontSize


    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) {
            return
        }

        var startAngle = -90F

        canvas.drawArc(oval, startAngle, fullCircleDegrees, false, paintEmpty)
        data.forEachIndexed { index, datum ->
             val angle = (datum / data.maxOrNull()!!.times(data.count())) * fullCircleDegrees
            paint.color = colors.getOrElse(index) { randomColor() }
            canvas.drawArc(oval, startAngle, angle, false, paint)
            startAngle += angle
        }

        val text = (data.sum() / data.maxOrNull()!!.times(data.count())) * 100F

        canvas.drawText(
            "%.2f%%".format(text),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint
        )
        if (text == 100F) {
            paint.color = colors[0]
            canvas.drawArc(oval, startAngle, 1F, false, paint)
        }
    }

    private fun randomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())


}