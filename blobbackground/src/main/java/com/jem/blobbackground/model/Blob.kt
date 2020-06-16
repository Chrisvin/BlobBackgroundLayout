package com.jem.blobbackground.model

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.graphics.*
import com.jem.blobbackground.util.PathUtil
import com.jem.blobbackground.util.RandomUtil
import kotlin.math.cos
import kotlin.math.sin

class Blob {

    companion object {
        private val RADIAN_MULTIPLIER = 2f * Math.PI

        private val DEFAULT_POINT_COUNT = 13
        private val DEFAULT_RADIUS = 1000f
        private val DEFAULT_MAX_OFFSET = 400f
        private val DEFAULT_ANIMATION_STATE = true
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
        interpolator = BounceInterpolator()
        duration =
            DEFAULT_ANIMATION_DURATION
        repeatCount = INFINITE
        repeatMode = REVERSE
    }

    private val latestPath = Path().apply {
        createPath(this)
    }

    private val fillPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.RED
    }

    constructor(updateView: () -> Unit) {
        percentageAnimator.addUpdateListener {
            recreatePath()
            updateView.invoke()
        }
        percentageAnimator.start()
    }

    private fun recreatePath(path: Path = latestPath) {
        path.rewind()
        createPath(path)
    }

    private fun createPath(path: Path = latestPath, points: ArrayList<PointF> = getPoints()) {
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
    }

    private fun getPoints(): ArrayList<PointF> {
        return arrayListOf<PointF>().apply {
            val baseTheta = RADIAN_MULTIPLIER / pointCount
            val animationMultiplier: Float =
                ((if (shouldAnimate) percentageAnimator.animatedValue else 100f) as Float) / 100f
            for (i in 0..pointCount) {
                val offsetR = radius + (
                        animationMultiplier *
                                (RandomUtil.getMultiplier() * maxOffset).coerceIn(-radius, radius)
//                                ((maxOffset * i) * (if (i % 2 == 0) -1f else 1f))
                        )
                add(
                    PointF(
                        (offsetR * cos(i * baseTheta)).toFloat(),
                        (offsetR * sin(i * baseTheta)).toFloat()
                    )
                )
            }
        }
    }

    fun drawPath(canvas: Canvas?) {
        canvas?.drawPath(latestPath, fillPaint)
    }

}