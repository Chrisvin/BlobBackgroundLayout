package com.jem.blobbackground.model

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.graphics.*
import android.view.animation.*
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnRepeat
import com.jem.blobbackground.util.PathUtil
import com.jem.blobbackground.util.PointUtil
import com.jem.blobbackground.util.RandomUtil

class Blob(private val updateView: () -> Unit) {

    companion object {
        private val RADIAN_MULTIPLIER = 2f * Math.PI

        private val DEFAULT_POINT_COUNT = 6
        private val DEFAULT_RADIUS = 1000f
        private val DEFAULT_ANIMATION_STATE = true
        private val DEFAULT_MAX_OFFSET = 0f
        private val DEFAULT_ANIMATION_DURATION = 3000L
    }

    private val radius = DEFAULT_RADIUS
    private val maxOffset = DEFAULT_MAX_OFFSET
    private val pointCount = DEFAULT_POINT_COUNT
    private val shouldAnimate = DEFAULT_ANIMATION_STATE

    private val offsetValues: ArrayList<Float> = arrayListOf()
    private val angleValues: ArrayList<Float> = arrayListOf()

    private val percentageAnimator = ValueAnimator().apply {
        setFloatValues(0f, 100f)
        interpolator = AccelerateDecelerateInterpolator()
        duration = DEFAULT_ANIMATION_DURATION
        repeatCount = INFINITE
        repeatMode = REVERSE
    }

    private val latestPath = Path()

    private val fillPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.RED
    }

    init {
        updateRandomizedValues()
        recreatePath()
        percentageAnimator.addUpdateListener {
            recreatePath()
            updateView.invoke()
        }
        percentageAnimator.start()
    }

    fun recreateBlob() {
        updateRandomizedValues()
        recreatePath()
        updateView.invoke()
    }

    private fun updateRandomizedValues() {
        offsetValues.clear()
        angleValues.clear()
        val baseTheta = RADIAN_MULTIPLIER / pointCount
        var previousAngle = 0f
        var currentAngle = 0f
        for (i in 0 until pointCount) {
            offsetValues.add((RandomUtil.getMultiplier() * maxOffset).coerceIn(-radius, radius))
            currentAngle = ((i * baseTheta) + (RandomUtil.getFloat() * baseTheta)).toFloat()
            currentAngle = if (currentAngle - previousAngle > baseTheta / 3) {
                currentAngle
            } else {
                currentAngle + (baseTheta.toFloat() / 3f)
            }
            angleValues.add(currentAngle)
            previousAngle = currentAngle
        }
    }

    private fun recreatePath(path: Path = latestPath, points: ArrayList<PointF> = getPoints()) {
        path.rewind()
        path.apply {
            var p0 = points[points.size - 1]
            var p1 = points[0]
            moveTo((p0.x + p1.x) / 2, (p0.y + p1.y) / 2)
            p0 = p1
            for (i in 1 until points.count()) {
                p1 = points[i]
                quadTo(p0.x, p0.y, (p0.x + p1.x) / 2f, (p0.y + p1.y) / 2f)
                p0 = p1
            }
            quadTo(p0.x, p0.y, (p0.x + points[0].x) / 2f, (p0.y + points[0].y) / 2f)
            close()
        }
        PathUtil.scalePath(path, 0.5f, 0.5f)
        PathUtil.translatePath(path, 750f, 1000f)
    }

    private fun getPoints(): ArrayList<PointF> {
        return arrayListOf<PointF>().apply {
            val animationMultiplier: Float =
                ((if (shouldAnimate) percentageAnimator.animatedValue else 100f) as Float) / 100f
            for (i in 0 until pointCount) {
                val offsetR = radius + (animationMultiplier * offsetValues[i])
                add(
                    PointF(
                        (offsetR * cos(angleValues[i])),
                        (offsetR * sin(angleValues[i]))
                    )
                )
            }
        }
    }

    fun drawPath(canvas: Canvas?) {
        canvas?.drawPath(latestPath, fillPaint)
    }

}