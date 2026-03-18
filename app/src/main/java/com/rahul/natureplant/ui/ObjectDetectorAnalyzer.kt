package com.rahul.natureplant.ui

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetector

@ExperimentalGetImage
class ObjectDetectorAnalyzer(
    private val detector: ObjectDetector,
    private val onResults: (objects: List<DetectedObject>, width: Int, height: Int) -> Unit
) : ImageAnalysis.Analyzer {

    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return
        val rotation = imageProxy.imageInfo.rotationDegrees

        // Determine dimensions based on rotation
        val width = if (rotation == 90 || rotation == 270) imageProxy.height else imageProxy.width
        val height = if (rotation == 90 || rotation == 270) imageProxy.width else imageProxy.height

        val image = InputImage.fromMediaImage(mediaImage, rotation)

        detector.process(image)
            .addOnSuccessListener { objects ->
                // Pass the objects AND the dimensions to the UI
                onResults(objects, width, height)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }

        }

}