package com.example.loadapp.ui.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.withStyledAttributes
import com.example.loadapp.R

class CustomButton @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet,
    defStyleRes: Int = 0
) : View(context, attributeSet, defStyleRes) {

    private val rect = RectF()
    private val buttonColor = context.getColor(R.color.tiffanyBlue)
    private val textColor = context.getColor(R.color.white)
    private val rectColor = context.getColor(R.color.tealBlue)
    private val circleColor = context.getColor(R.color.crayola)
    private var progress = 0
    private val paint = Paint().apply {
        color = rectColor
        textSize = resources.getDimension(R.dimen.buttonTextSize)
        textAlign = Paint.Align.CENTER
    }

    private var clickEnabled = true
    private var clickDisabled = false

    init {
        context.withStyledAttributes(attributeSet, R.styleable.CustomButton) {
            clickEnabled = getBoolean(R.styleable.CustomButton_isEnabled, true)
            clickDisabled = getBoolean(R.styleable.CustomButton_isDisabled, false)
        }
    }

    fun changeButtonProgress(downloadProgress: Int) {
        progress = downloadProgress
        isClickable = if (progress == 0) clickEnabled else clickDisabled
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(buttonColor)
        rect.set(0f, 0f, progress * width / 100f % width, height.toFloat())
        paint.color = rectColor
        canvas.drawRect(rect, paint)
        drawText(
            canvas, if (progress == 0) {
                context.getString(R.string.download)
            } else {
                context.getString(R.string.we_are_loading)
            }
        )
        downloadProgressCircle(canvas)
    }

    private fun drawText(canvas: Canvas, text: String) {
        paint.color = textColor
        canvas.drawText(
            text,
            width / 2f,
            height / 2f + paint.descent(),
            paint
        )
    }

    private fun downloadProgressCircle(canvas: Canvas) {
        paint.color = circleColor
        paint.measureText(context.getString(R.string.we_are_loading))
        rect.set(
            width / 2f + paint.measureText(context.getString(R.string.we_are_loading)) / 2 + height / 8f,
            height / 2f - height / 4f,
            width / 2f + paint.measureText(context.getString(R.string.we_are_loading)) / 2 + 5 * height / 8f,
            height / 2f + height / 4f
        )
        canvas.drawArc(rect, 0f, (progress * 360) / 100f % 360, true, paint)
    }
}