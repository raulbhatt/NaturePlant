package com.rahul.natureplant.ui

import android.Manifest
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.rahul.natureplant.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ObjectDetectionVidActivity : AppCompatActivity() {

    private lateinit var viewFinder: PreviewView

    private lateinit var graphicOverlay: GraphicOverlay
    private lateinit var cameraExecutor: ExecutorService

    // Add this to your Activity
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_camera1)

        cameraExecutor = Executors.newSingleThreadExecutor()

        viewFinder = findViewById(R.id.viewFinder)
        graphicOverlay = findViewById(R.id.graphicOverlay)

        startCamera()

        // In onCreate, trigger the request
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)

    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown() // Prevent memory leaks
    }


    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {

        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.STREAM_MODE) // Optimizes for video
            .enableMultipleObjects()
            .enableClassification() // Identifies what the object is
            .build()

        val objectDetector = ObjectDetection.getClient(options)

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // 1. Preview View
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }

            // 2. Image Analysis
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                // Force a standard resolution to ensure stability
                .setTargetResolution(Size(1280, 720))
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, ObjectDetectorAnalyzer(objectDetector) { objects, width, height ->
                        // Use the UI Thread to update the overlay
                        runOnUiThread {
                            graphicOverlay.updateResults(objects, width, height)
                        }
                    })
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis)
            } catch (e: Exception) {
                Log.e("CameraX", "Binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }
}