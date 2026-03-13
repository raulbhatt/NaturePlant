package com.rahul.natureplant.ui

import VisualSearchManager
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.common.util.concurrent.ListenableFuture
import com.rahul.natureplant.databinding.ActivityCameraBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutionException

class CameraActivity : AppCompatActivity() {

    private lateinit var binding : ActivityCameraBinding

    private val visualSearchManager by lazy { VisualSearchManager("AIzaSyDfmfem5S8_7VDS8d2_s0D2qImZxD5P4I0") }

    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            cameraPermissionRequest.launch(Manifest.permission.CAMERA)
        }

        binding.captureButton.setOnClickListener {
            takePhoto()
        }

    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                bindPreview(cameraProvider)
                binding.cameraPreview.visibility = View.VISIBLE
                binding.captureButton.visibility = View.VISIBLE
            } catch (e: ExecutionException) {
                Log.e("HomeFragment", "Error starting camera: ${e.message}")
            } catch (e: InterruptedException) {
                Log.e("HomeFragment", "Error starting camera: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build()
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
        imageCapture = ImageCapture.Builder().build()
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val bitmap = image.toBitmap()
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            val result = visualSearchManager.performVisualSearch(bitmap)
                            withContext(Dispatchers.Main) {
                                updateUI(result)
                            }
                            image.close()
                        }
                    }
                    binding.cameraPreview.visibility = View.GONE
                    binding.captureButton.visibility = View.GONE
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("HomeFragment", "Image capture failed: ${exception.message}", exception)
                }
            })
    }

    private fun updateUI(result: String?) {
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
    }


}
