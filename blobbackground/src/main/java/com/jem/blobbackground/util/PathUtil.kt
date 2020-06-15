package com.jem.blobbackground.util

import android.graphics.Matrix
import android.graphics.Path

object PathUtil {

    fun translatePath(path: Path, dx: Float, dy: Float) {
        val matrix = Matrix()
        matrix.setTranslate(dx, dy)
        path.transform(matrix)
    }

    fun scalePath(path: Path, sx: Float, sy: Float, px: Float? = null, py: Float? = null) {
        val matrix = Matrix()
        if (px != null && py != null) {
            matrix.setScale(sx, sy, px, py)
        } else {
            matrix.setScale(sx, sy)
        }
        path.transform(matrix)
    }

    fun rotatePath(path: Path, degree: Float, px: Float? = null, py: Float? = null) {
        val matrix = Matrix()
        if (px != null && py != null) {
            matrix.setRotate(degree, px, py)
        } else {
            matrix.setRotate(degree)
        }
        path.transform(matrix)
    }

}