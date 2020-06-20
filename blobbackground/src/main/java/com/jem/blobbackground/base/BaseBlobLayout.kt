package com.jem.blobbackground.base

import com.jem.blobbackground.model.Blob

internal interface BaseBlobLayout {

    /**
     * Get blob count
     */
    fun getBlobCount(): Int

    /**
     * Add blob with specified configuration
     */
    fun addBlob(vararg blobConfig: Blob.Configuration)

    /**
     * Remove all blobs
     */
    fun removeBlobs()

    /**
     * Remove blob at index
     */
    fun removeBlob(index: Int)

    /**
     * Recreate all blobs with new shapes
     */
    fun recreateBlobs()

    /**
     * Recreate blob at index with new shape
     */
    fun recreateBlob(index: Int)

    /**
     * Get all blobConfigurations
     */
    fun getBlobConfigurations(): Array<Blob.Configuration>

    /**
     * Get configuration for Blob at index
     */
    fun getBlobConfiguration(index: Int): Blob.Configuration

    /**
     * Update all blobConfigurations
     */
    fun updateBlobConfigurations(blobConfigs: Array<Blob.Configuration>)

    /**
     * Update configuration for BLob at index
     */
    fun updateBlobConfiguration(blobConfig: Blob.Configuration, index: Int)

}