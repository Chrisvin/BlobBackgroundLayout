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

class Blob(
    private val updateView: () -> Unit,
    private val blobConfig: Configuration = Configuration()
) {

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

    private val startPoints: ArrayList<PointF> = arrayListOf()
    private val endPoints: ArrayList<PointF> = arrayListOf()

    private var _latestPoints: ArrayList<PointF> = arrayListOf()

    private val percentageAnimator = ValueAnimator().apply {
        setFloatValues(0f, 100f)
        interpolator = blobConfig.shapeAnimationInterpolator
        duration = blobConfig.shapeAnimationDuration
        repeatCount = INFINITE
        repeatMode = REVERSE
    }

    private val latestPath = Path()

    init {
        updateRandomizedValues()
        recreatePath()
        percentageAnimator.addUpdateListener {
            recreatePath()
            updateView.invoke()
        }
        percentageAnimator.doOnRepeat {
            if (!blobConfig.shouldAnimateShape) {
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
        val baseTheta = RADIAN_MULTIPLIER / blobConfig.pointCount
        var previousAngle = 0f
        var currentAngle = 0f
        for (i in 0 until blobConfig.pointCount) {
            val offsetR = blobConfig.radius +
                    ((RandomUtil.getMultiplier() * blobConfig.maxOffset)
                        .coerceIn(-blobConfig.radius, blobConfig.radius))
            currentAngle = ((i * baseTheta) + (RandomUtil.getFloat() * baseTheta)).toFloat()
            currentAngle = if (currentAngle - previousAngle > baseTheta / 3) {
                currentAngle
            } else {
                currentAngle + (baseTheta.toFloat() / 3f)
            }
            points.add(PointUtil.getPointOnCircle(offsetR, currentAngle, blobConfig.blobCenterPosition))
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
        if (startPoints.size != blobConfig.pointCount || endPoints.size != blobConfig.pointCount) {
            return _latestPoints
        }
        _latestPoints = arrayListOf<PointF>().apply {
            val animationMultiplier: Float =
                ((if (blobConfig.shouldAnimateShape) percentageAnimator.animatedValue else 100f) as Float) / 100f
            for (i in 0 until blobConfig.pointCount) {
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
        canvas?.drawPath(latestPath, blobConfig.paint)
    }

    /**
     * Get a cloned blob config to ensure that value changes don't reflect on the blob
     */
    fun getConfiguration(): Configuration {
        return Configuration(
            pointCount = blobConfig.pointCount,
            radius = blobConfig.radius,
            maxOffset = blobConfig.maxOffset,
            paint = blobConfig.paint,
            shouldAnimateShape = blobConfig.shouldAnimateShape,
            shapeAnimationDuration = blobConfig.shapeAnimationDuration,
            shapeAnimationInterpolator = blobConfig.shapeAnimationInterpolator,
            blobCenterPosition =  blobConfig.blobCenterPosition
        )
    }

    /**
     * Update the config of the blob
     */
    fun updateConfiguration(config: Configuration) {
        blobConfig.apply {
            this.pointCount = config.pointCount
            this.radius = config.radius
            this.maxOffset = config.maxOffset
            this.paint = config.paint
            this.shouldAnimateShape = config.shouldAnimateShape
            this.shapeAnimationDuration = config.shapeAnimationDuration
            this.shapeAnimationInterpolator = config.shapeAnimationInterpolator
            this.blobCenterPosition =  config.blobCenterPosition
        }
    }

    data class Configuration(
        var pointCount: Int = DEFAULT_POINT_COUNT,
        var radius: Float = DEFAULT_RADIUS,
        var maxOffset: Float = DEFAULT_MAX_OFFSET,
        var paint: Paint = DEFAULT_PAINT,
        var shouldAnimateShape: Boolean = DEFAULT_ANIMATION_STATE,
        var shapeAnimationDuration: Long = DEFAULT_ANIMATION_DURATION,
        var shapeAnimationInterpolator: Interpolator = DEFAULT_ANIMATION_INTERPOLATOR,
        var blobCenterPosition: PointF = DEFAULT_POSITION
    )
}