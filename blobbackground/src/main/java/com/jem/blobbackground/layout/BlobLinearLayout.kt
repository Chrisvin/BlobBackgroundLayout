package com.jem.blobbackground.layout

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.widget.LinearLayout
import com.jem.blobbackground.base.BaseBlobLayout
import com.jem.blobbackground.handler.BlobLayoutHandler
import com.jem.blobbackground.model.Blob

class BlobLinearLayout : LinearLayout, BaseBlobLayout {

    private val blobLayoutHandler = BlobLayoutHandler()

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        setWillNotDraw(false)
        blobLayoutHandler.setOnViewUpdateListener {
            invalidate()
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        blobLayoutHandler.onDraw(canvas)
    }

    override fun getBlobCount(): Int {
        return blobLayoutHandler.getBlobCount()
    }

    override fun addBlob(vararg blobConfig: Blob.Configuration) {
        blobLayoutHandler.addBlob(*blobConfig)
    }

    override fun removeBlobs() {
        blobLayoutHandler.removeBlobs()
    }

    override fun removeBlob(index: Int) {
        blobLayoutHandler.removeBlob(index)
    }

    override fun recreateBlobs() {
        blobLayoutHandler.recreateBlobs()
    }

    override fun recreateBlob(index: Int) {
        blobLayoutHandler.recreateBlob(index)
    }

    override fun getBlobConfigurations(): Array<Blob.Configuration> {
        return blobLayoutHandler.getBlobConfigurations()
    }

    override fun getBlobConfiguration(index: Int): Blob.Configuration {
        return blobLayoutHandler.getBlobConfiguration(index)
    }

    override fun updateBlobConfigurations(blobConfigs: Array<Blob.Configuration>) {
        blobLayoutHandler.updateBlobConfigurations(blobConfigs)
    }

    override fun updateBlobConfiguration(blobConfig: Blob.Configuration, index: Int) {
        blobLayoutHandler.updateBlobConfiguration(blobConfig, index)
    }
}