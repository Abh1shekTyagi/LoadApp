package com.example.loadapp.ui.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Region
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.loadapp.R

class DownloadIcon(context: Context, attrs: AttributeSet? = null) :
    View(context, attrs) {
    private val clippedPath = Path()
    private val backgroundColor = context.getColor(R.color.tealBlue)
    private val paintColor = context.getColor(R.color.helioTrope)
    private var clippedRectangleWidth = 0f
    private var clippedRectangleHeight = 0f
    private var clippedRectangleLeft = 0f
    private var clippedRectangleRight = 0f
    private var clippedRectangleTop = 0f
    private var clippedRectangleBottom = 0f
    private val rect = RectF()
    private var horizontalCenter = 0f
    private var verticalCenter = 0f
    private var actionUp = true
    private val paint = Paint().apply {
        color = paintColor
        isAntiAlias = true
    }
    private var backgroundHeight = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (actionUp) {
            backgroundHeight = height / 3f
            clippedRectangleWidth = width / 10f
            clippedRectangleHeight = backgroundHeight / 7f
        }
        canvas.drawRect(0f, 0f, width.toFloat(), backgroundHeight, paint.apply { color = backgroundColor })
        canvas.save()
        canvas.translate(horizontalCenter, verticalCenter)
        clipDownloadIcon(canvas)
        paint.color = paintColor
        canvas.drawCircle(
            width / 2f,
            backgroundHeight / 2f + clippedRectangleWidth / 5,
            clippedRectangleHeight * 2.5f,
            paint
        )
        canvas.restore()
    }


    private fun clipDownloadIcon(canvas: Canvas) {
        clippedRectangleLeft = (width - clippedRectangleWidth) / 2
        clippedRectangleRight = (width + clippedRectangleWidth) / 2
        clippedRectangleTop = (backgroundHeight - clippedRectangleHeight) / 2
        clippedRectangleBottom = (backgroundHeight + clippedRectangleHeight) / 2

        rect.set(
            clippedRectangleLeft,
            clippedRectangleTop,
            clippedRectangleRight,
            clippedRectangleBottom
        )

        clippedPath.addRect(rect, Path.Direction.CCW)

        clippedPath.moveTo(clippedRectangleLeft - clippedRectangleWidth / 2, clippedRectangleBottom)
        clippedPath.lineTo(width / 2f, clippedRectangleBottom + clippedRectangleHeight)
        clippedPath.lineTo(
            clippedRectangleRight + clippedRectangleWidth / 2, clippedRectangleBottom
        )

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            canvas.clipPath(clippedPath, Region.Op.DIFFERENCE)
        } else {
            canvas.clipOutPath(clippedPath)
        }
        clippedPath.reset()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    horizontalCenter = 0f
                    verticalCenter = 0f
                    actionUp = true
                }

                else -> {
                    horizontalCenter = event.x - width / 2
                    verticalCenter = event.y - backgroundHeight / 2
                    clippedRectangleWidth = width / 13f
                    clippedRectangleHeight = backgroundHeight / 10f
                    actionUp = false
                }
            }
        }
        invalidate()
        return true
    }
}