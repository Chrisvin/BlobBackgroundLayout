package com.jem.blobbackground.model

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.graphics.*
import android.view.animation.*
import android.view.animation.Interpolator
import androidx.core.animation.doOnRepeat
import com.jem.blobbackground.util.PathUtil
import com.jem.blobbackground.util.PointUtil
import com.jem.blobbackground.util.RandomUtil

class Blob(private val updateView: () -> Unit) {

    companion object {
        private val RADIAN_MULTIPLIER = 2f * Math.PI

        private val DEFAULT_POINT_COUNT = 6
        private val DEFAULT_RADIUS = 1000f
        private val DEFAULT_MAX_OFFSET = 0f
        private val DEFAULT_ANIMATION_STATE = true
        private val DEFAULT_ANIMATION_DURATION = 2000L
        private val DEFAULT_ANIMATION_INTERPOLATOR = LinearInterpolator()
        private val DEFAULT_POSITION = PointF(0f, 0f)
        private val DEFAULT_PAINT = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = Color.RED
        }
    }

    private val radius = DEFAULT_RADIUS
    private val maxOffset = DEFAULT_MAX_OFFSET
    private val pointCount = DEFAULT_POINT_COUNT
    private val shouldAnimate = DEFAULT_ANIMATION_STATE

    private val startPoints: ArrayList<PointF> = arrayListOf()
    private val endPoints: ArrayList<PointF> = arrayListOf()

    private var _latestPoints: ArrayList<PointF> = arrayListOf()

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
        percentageAnimator.doOnRepeat {
            if (!shouldAnimate) {
                return@doOnRepeat
            }
            if (percentageAnimator.animatedValue as Float > 50f) {
                updateRandomizedValues(startPoints)
            } else {
                updateRandomizedValues(endPoints)
            }
        }
        percentageAnimator.start()
    }

    fun recreateBlob() {
        updateRandomizedValues()
        recreatePath()
        updateView.invoke()
    }

    private fun updateRandomizedValues() {
        updateRandomizedValues(startPoints)
        updateRandomizedValues(endPoints)
    }

    private fun updateRandomizedValues(points: ArrayList<PointF>) {
        points.clear()
        val baseTheta = RADIAN_MULTIPLIER / pointCount
        var previousAngle = 0f
        var currentAngle = 0f
        for (i in 0 until pointCount) {
            val offsetR =
                radius + ((RandomUtil.getMultiplier() * maxOffset).coerceIn(-radius, radius))
            currentAngle = ((i * baseTheta) + (RandomUtil.getFloat() * baseTheta)).toFloat()
            currentAngle = if (currentAngle - previousAngle > baseTheta / 3) {
                currentAngle
            } else {
                currentAngle + (baseTheta.toFloat() / 3f)
            }
            points.add(PointUtil.getPointOnCircle(offsetR, currentAngle))
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
        if (startPoints.size != pointCount || endPoints.size != pointCount) {
            return _latestPoints
        }
        _latestPoints = arrayListOf<PointF>().apply {
            val animationMultiplier: Float =
                ((if (shouldAnimate) percentageAnimator.animatedValue else 100f) as Float) / 100f
            for (i in 0 until pointCount) {
                add(
                    PointUtil.getIntermediatePoint(
                        startPoints[i], endPoints[i], animationMultiplier
                    )
                )
            }
        }
        return _latestPoints
    }

    fun drawPath(canvas: Canvas?) {
        canvas?.drawPath(latestPath, fillPaint)
    }

    data class Configuration(
        val pointCount: Int = DEFAULT_POINT_COUNT,
        val radius: Float = DEFAULT_RADIUS,
        val maxOffset: Float = DEFAULT_MAX_OFFSET,
        val paint: Paint = DEFAULT_PAINT,
        val shouldAnimateShape: Boolean = DEFAULT_ANIMATION_STATE,
        val shapeAnimationDuration: Long = DEFAULT_ANIMATION_DURATION,
        val shapeAnimationInterpolator: Interpolator = DEFAULT_ANIMATION_INTERPOLATOR,
        val blobCenterPosition: PointF = DEFAULT_POSITION
    )
}