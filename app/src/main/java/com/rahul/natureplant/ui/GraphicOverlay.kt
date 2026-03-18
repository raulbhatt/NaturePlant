package com.rahul.natureplant.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.google.mlkit.vision.objects.DetectedObject

class GraphicOverlay(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val lock = Any()
    private var imageWidth: Int = 0
    private var imageHeight: Int = 0
    private var scaleX: Float = 1f
    private var scaleY: Float = 1f
    private var detectedObjects = mutableListOf<DetectedObject>()

    private val boxPaint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = 6f
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE // High contrast
        textSize = 45f      // Make it large enough to see
    }

    private val labelBackgroundPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        alpha = 180 // Semi-transparent black background
    }

    // Example for a standard COCO-trained model
    val labelMap = listOf(
        "person", "bicycle", "car", "motorcycle", "airplane",
        "bus", "train", "truck", "boat", "traffic light",
        "fire hydrant", "stop sign", "parking meter", "bench", "bird",
        "cat", "dog", "horse", "sheep", "cow", "elephant", "bear",
        "zebra", "giraffe", "backpack", "umbrella", "handbag", "tie",
        "suitcase", "frisbee", "skis", "snowboard", "sports ball",
        "kite", "baseball bat", "baseball glove", "skateboard",
        "surfboard", "tennis racket", "bottle", "wine glass", "cup",
        "fork", "knife", "spoon", "bowl", "banana", "apple",
        "sandwich", "orange", "broccoli", "carrot", "hot dog", "pizza",
        "donut", "cake", "chair", "couch", "potted plant", "bed",
        "dining table", "toilet", "tv", "laptop", "mouse", "remote",
        "keyboard", "cell phone", "microwave", "oven", "toaster",
        "sink", "refrigerator", "book", "clock", "vase", "scissors",
        "teddy bear", "hair drier", "toothbrush"
    )

    // Call this from the Analyzer to update the data
    fun updateResults(objects: List<DetectedObject>, width: Int, height: Int) {
        synchronized(lock) {
            imageWidth = width
            imageHeight = height
            detectedObjects.clear()
            detectedObjects.addAll(objects)
        }
        postInvalidate() // Redraw the view
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        synchronized(lock) {
            if (imageWidth == 0 || imageHeight == 0) return

            // Calculate scale factors
            scaleX = width.toFloat() / imageHeight.toFloat()
            scaleY = height.toFloat() / imageWidth.toFloat()

            for (obj in detectedObjects) {
                // Scale the bounding box to fit the screen
                val rect = RectF(
                    obj.boundingBox.left * scaleX,
                    obj.boundingBox.top * scaleY,
                    obj.boundingBox.right * scaleX,
                    obj.boundingBox.bottom * scaleY
                )
                
                canvas.drawRect(rect, boxPaint)

                // 2. Check for Labels
                if (obj.labels.isNotEmpty()) {
                    /*val label = obj.labels[0]
                    val text = "${label.text} ${(label.confidence * 100).toInt()}%"

                    // Measure text for background size
                    val textWidth = textPaint.measureText(text)
                    val textHeight = 45f // Based on textSize

                    // 3. Draw a Background for the Text (makes it visible!)
                    val bgRect = RectF(
                        rect.left,
                        rect.top - textHeight - 20f,
                        rect.left + textWidth + 20f,
                        rect.top
                    )
                    canvas.drawRect(bgRect, labelBackgroundPaint)
                    // 4. Draw the Text
                    canvas.drawText(text, rect.left + 10f, rect.top - 15f, textPaint)*/

                    val label = obj.labels[0]
                    val text = "${label.text} ${(label.confidence * 100).toInt()}%"

                    // Measure text for background size
                    val textWidth = textPaint.measureText(text)
                    val textHeight = 45f // Based on textSize

                    // 3. Draw a Background for the Text (makes it visible!)
                    val bgRect = RectF(
                        rect.left,
                        rect.top - textHeight - 20f,
                        rect.left + textWidth + 20f,
                        rect.top
                    )
                    // Try to get the name. If text is empty or a number string, use the map.
                    val displayName = if (label.text.toIntOrNull() != null) {
                        val index = label.text.toInt()
                        if (index in labelMap.indices) labelMap[index] else "Unknown ($index)"
                    } else {
                        label.text // Use the text if the model already provides it
                    }

                    val textToDraw = "$displayName ${(label.confidence * 100).toInt()}%"

                    // Draw the background and text as we did before
                    canvas.drawRect(bgRect, labelBackgroundPaint)
                    canvas.drawText(textToDraw, rect.left + 10f, rect.top - 15f, textPaint)
                } else {
                    // Optional: Show "Object" if no classification is found
                    canvas.drawText("Object", rect.left, rect.top - 10f, textPaint)
                }

            }
        }
    }
}