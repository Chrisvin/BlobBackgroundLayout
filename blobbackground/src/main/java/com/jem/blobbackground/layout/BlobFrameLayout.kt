package com.jem.blobbackground.layout

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.FrameLayout
import com.jem.blobbackground.model.Blob

class BlobFrameLayout : FrameLayout {

    private var blobs: ArrayList<Blob> = arrayListOf()

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        //TODO: Do stuff to the blobs based on the input attributes.
        blobs.add(Blob {
            invalidate()
        })
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        blobs.forEach {
            it.drawPath(canvas)
        }
    }

    fun recreateBlobs() {
        blobs.forEach {
            it.recreateBlob()
        }
    }

    fun recreateBlob(index: Int) {
        if (index < 0 || index >= blobs.size) {
            recreateBlobs()
        } else {
            blobs[index].recreateBlob()
        }
    }

}