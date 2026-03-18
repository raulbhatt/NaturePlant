package com.rahul.natureplant.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.rahul.natureplant.R

class ObjectDetectionActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var overlayView: ImageView
    private lateinit var sampleBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_object_detection)

        imageView = findViewById(R.id.imageView)
        overlayView = findViewById(R.id.overlayView)

        // Load a sample image from your resources
        // Using img_aloe which exists in the project
        sampleBitmap = BitmapFactory.decodeResource(resources, R.drawable.img_aloe)
        imageView.setImageBitmap(sampleBitmap)

        findViewById<Button>(R.id.btnDetect).setOnClickListener {
            runObjectDetection(sampleBitmap)
        }
    }

    private fun runObjectDetection(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE)
            .enableMultipleObjects()
            .enableClassification()
            .build()

        val objectDetector = ObjectDetection.getClient(options)

        objectDetector.process(image)
            .addOnSuccessListener { detectedObjects ->
                drawDetectionResult(detectedObjects, bitmap)
            }
    }

    private fun drawDetectionResult(results: List<DetectedObject>, bitmap: Bitmap) {
        val outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(outputBitmap)
        val pen = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 8f
        }
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = 60f
        }

        for (obj in results) {
            canvas.drawRect(obj.boundingBox, pen)
            val label = if (obj.labels.isNotEmpty()) obj.labels[0].text else "Unknown"
            canvas.drawText(label, obj.boundingBox.left.toFloat(), obj.boundingBox.top.toFloat(), textPaint)
        }

        overlayView.setImageBitmap(outputBitmap)
    }


}
