package com.jem.blobbackgrounddemo

import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jem.blobbackground.model.Blob
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        blobLayout.viewTreeObserver.addOnGlobalLayoutListener {
            val layoutWidth = blobLayout.width.toFloat()
            val layoutHeight = blobLayout.height.toFloat()
            val minVal = min(layoutWidth, layoutHeight)
            blobLayout.removeBlobs()
            blobLayout.addBlob(
                Blob.Configuration(
                    pointCount = 32,
                    blobCenterPosition = PointF(0f, 0f),
                    radius = minVal - minVal / 4,
                    maxOffset = (minVal - minVal / 4) / 12,
                    paint = getPaint(Color.parseColor("#710627"))
                ),
                Blob.Configuration(
                    pointCount = 12,
                    blobCenterPosition = PointF(
                        layoutWidth,
                        if (layoutWidth < layoutHeight) layoutHeight / 4 else 0f
                    ),
                    radius = minVal / 4,
                    maxOffset = (minVal / 4) / 8,
                    shapeAnimationDuration = 1500,
                    paint = getPaint(Color.parseColor("#0F7173"))
                ),
                Blob.Configuration(
                    pointCount = 24,
                    blobCenterPosition = PointF((layoutWidth * 2) / 3, layoutHeight),
                    radius = minVal - minVal / 8,
                    maxOffset = (minVal - minVal / 8) / 8,
                    paint = getPaint(Color.parseColor("#907AD6"))
                )
            )
        }

        animateBlobSwitch.setOnCheckedChangeListener { _, isChecked ->
            val blobConfigs = blobLayout.getBlobConfigurations()
            for (i in blobConfigs.indices) {
                blobConfigs[i].shouldAnimateShape = isChecked
            }
            blobLayout.updateBlobConfigurations(blobConfigs)
        }

        recreateBlobButton.setOnClickListener {
            blobLayout.recreateBlobs()
        }
    }

    private fun getPaint(paintColor: Int): Paint {
        return Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = paintColor
        }
    }
}