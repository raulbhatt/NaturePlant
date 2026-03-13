package com.rahul.natureplant.ui

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rahul.natureplant.databinding.FragmentEReceiptBinding
import com.rahul.natureplant.ui.adapter.CheckoutAdapter
import com.rahul.natureplant.viewmodel.PlantViewModel
import java.io.OutputStream

class EReceiptFragment : Fragment() {

    private var _binding: FragmentEReceiptBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlantViewModel by activityViewModels()
    private lateinit var adapter: CheckoutAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEReceiptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeData()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnDownload.setOnClickListener {
            downloadReceiptAsPdf()
        }
    }

    private fun setupRecyclerView() {
        adapter = CheckoutAdapter()
        binding.rvItems.adapter = adapter
    }

    private fun observeData() {
        viewModel.lastOrderItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
        }
    }

    private fun downloadReceiptAsPdf() {
        val view = binding.llReceiptContent
        val width = view.width
        val height = view.height

        if (width <= 0 || height <= 0) {
            Toast.makeText(requireContext(), "Receipt content is not ready", Toast.LENGTH_SHORT).show()
            return
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(width, height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        page.canvas.drawBitmap(bitmap, 0f, 0f, null)
        pdfDocument.finishPage(page)

        val fileName = "Receipt_${System.currentTimeMillis()}.pdf"

        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
            }

            val uri = requireContext().contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            )

            uri?.let {
                val outputStream: OutputStream? = requireContext().contentResolver.openOutputStream(it)
                outputStream?.use { stream ->
                    pdfDocument.writeTo(stream)
                }
                pdfDocument.close()
                Toast.makeText(requireContext(), "PDF saved to Downloads", Toast.LENGTH_LONG).show()
            } ?: run {
                pdfDocument.close()
                Toast.makeText(requireContext(), "Failed to create PDF file", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            pdfDocument.close()
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error saving PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
