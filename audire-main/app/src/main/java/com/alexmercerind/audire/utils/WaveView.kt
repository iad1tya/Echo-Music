package com.alexmercerind.audire.utils

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.google.android.material.color.MaterialColors
import kotlin.math.sin

class WaveView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var amplitude = 30.0F.toDp() // scale
    private var animator: ValueAnimator? = null
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    private var speed = 0.0F

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        animator?.cancel()
        animator = createAnimator().apply { start() }
    }

    override fun onDraw(c: Canvas) = c.drawPath(path, paint)

    private fun createAnimator(): ValueAnimator {
        return ValueAnimator.ofFloat(0f, Float.MAX_VALUE).apply {
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                speed -= WAVE_SPEED
                createPath()
                invalidate()
            }
        }
    }

    private fun createPath() {
        path.reset()
        // https://stackoverflow.com/a/64509627/12825435
        paint.color = MaterialColors.getColor(
            context, com.google.android.material.R.attr.colorSurfaceVariant, Color.BLACK
        )

        path.moveTo(0f, height.toFloat())
        path.lineTo(0f, amplitude)
        for (i in 0..width step 10) {
            val x = i.toFloat()
            val y =
                sin((i + 10) * Math.PI / WAVE_AMOUNT_ON_SCREEN + speed).toFloat() * amplitude + amplitude * 2
            path.lineTo(x, y)
        }
        path.lineTo(width.toFloat(), height.toFloat())
        path.close()
    }

    override fun onDetachedFromWindow() {
        animator?.cancel()
        super.onDetachedFromWindow()
    }

    companion object {
        const val WAVE_SPEED = 0.1F
        const val WAVE_AMOUNT_ON_SCREEN = 300
    }

    private fun Float.toDp() = this * context.resources.displayMetrics.density
}
