package com.jem.blobbackground.util

import java.util.*

object RandomUtil {

    private val random = Random()

    fun getInt(bound: Int): Int {
        return random.nextInt(bound)
    }

    fun getLong(): Long {
        return random.nextLong()
    }

    fun getFloat(): Float {
        return random.nextFloat()
    }

    fun getMultiplier(): Float {
        return 2f * (0.5f - random.nextFloat())
    }

}