package com.jem.blobbackground.util

import android.graphics.PointF
import kotlin.math.cos
import kotlin.math.sin

object PointUtil {

    fun getPointOnCircle(
        radius: Float,
        angle: Float,
        positionOffset: PointF = PointF(0f, 0f)
    ): PointF {
        return PointF(
            (radius * cos(angle)) + positionOffset.x,
            (radius * sin(angle)) + positionOffset.y
        )
    }

    fun getIntermediatePoint(start: PointF, end: PointF, percent: Float): PointF {
        return PointF(
            lerp(start.x, end.x, percent),
            lerp(start.y, end.y, percent)
        )
    }

    private fun lerp(a: Float, b: Float, t: Float): Float {
        return a + t * (b - a)
    }

}