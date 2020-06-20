package com.jem.blobbackground.handler

import android.graphics.Canvas
import com.jem.blobbackground.base.BaseBlobLayout
import com.jem.blobbackground.model.Blob

class BlobLayoutHandler : BaseBlobLayout {
    private val blobs: ArrayList<Blob> = arrayListOf()
    private var onViewUpdateCallback: (() -> Unit)? = null

    fun setOnViewUpdateListener(onViewUpdate: () -> Unit) {
        onViewUpdateCallback = onViewUpdate
    }

    fun onDraw(canvas: Canvas?) {
        blobs.forEach {
            it.drawPath(canvas)
        }
    }

    override fun getBlobCount(): Int {
        return blobs.size
    }

    override fun addBlob(vararg blobConfig: Blob.Configuration) {
        for (i in blobConfig.indices) {
            blobs.add(Blob({
                onViewUpdateCallback?.invoke()
            }, blobConfig[i]))
        }
    }

    override fun removeBlobs() {
        blobs.clear()
    }

    override fun removeBlob(index: Int) {
        blobs.removeAt(index)
    }

    override fun recreateBlobs() {
        blobs.forEach {
            it.recreateBlob()
        }
    }

    override fun recreateBlob(index: Int) {
        blobs[index].recreateBlob()
    }

    override fun getBlobConfigurations(): Array<Blob.Configuration> {
        return Array(blobs.size) { index ->
            blobs[index].getConfiguration()
        }
    }

    override fun getBlobConfiguration(index: Int): Blob.Configuration {
        return blobs[index].getConfiguration()
    }

    override fun updateBlobConfigurations(blobConfigs: Array<Blob.Configuration>) {
        for (i in blobConfigs.indices) {
            if (i < blobs.size) {
                blobs[i].updateConfiguration(blobConfigs[i])
            }
        }
    }

    override fun updateBlobConfiguration(blobConfig: Blob.Configuration, index: Int) {
        if (index < blobs.size) {
            blobs[index].updateConfiguration(blobConfig)
        }
    }
}