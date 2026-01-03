package com.example.udhaarpay.ui.scan

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.udhaarpay.data.model.CreditCard
import com.example.udhaarpay.data.model.PaymentMethod
import com.example.udhaarpay.data.model.Transaction
import com.example.udhaarpay.data.model.TransactionStatus
import com.example.udhaarpay.data.model.TransactionType
import com.example.udhaarpay.databinding.FragmentScanPayBinding
import com.example.udhaarpay.ui.viewmodel.CreditCardViewModel
import com.example.udhaarpay.ui.viewmodel.TransactionViewModel
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
@androidx.camera.core.ExperimentalGetImage
class ScanPayFragment : Fragment() {

    private var _binding: FragmentScanPayBinding? = null
    private val binding get() = _binding!!
    
    private val creditCardViewModel: CreditCardViewModel by viewModels()
    private val transactionViewModel: TransactionViewModel by viewModels()

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService
    private var qrCodeFound = false
    
    private var cards = mutableListOf<CreditCard>()
    private var selectedCardIndex = 0
    private var isCardsExpanded = false
    private val cardAdapter = CardStackAdapter { index, card ->
        selectCard(index, card)
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                try {
                    val image = InputImage.fromFilePath(requireContext(), uri)
                    scanQrCode(image)
                } catch (e: Exception) {
                    Log.e("ScanPayFragment", "Error processing gallery image", e)
                }
            }
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanPayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraExecutor = Executors.newSingleThreadExecutor()
        
        // Setup card UI
        setupCardUI()
        observeCreditCards()

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.galleryButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(intent)
        }
        
        // Setup pay button
        binding.btnPay.setOnClickListener {
            if (cards.isEmpty()) {
                Toast.makeText(requireContext(), "Please add a credit card first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showPaymentDialog()
        }
    }
    
    private fun setupCardUI() {
        binding.rvCardStack.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCardStack.adapter = cardAdapter
        
        binding.cardStackContainer.setOnClickListener {
            expandCards()
        }
    }
    
    private fun observeCreditCards() {
        lifecycleScope.launch {
            creditCardViewModel.creditCards.collect { creditCards ->
                cards = creditCards.toMutableList()
                
                if (cards.isEmpty()) {
                    binding.cardStackContainer.isVisible = false
                    binding.emptyCardsMessage.isVisible = true
                } else {
                    binding.cardStackContainer.isVisible = true
                    binding.emptyCardsMessage.isVisible = false
                    updateCardStack()
                }
            }
        }
    }
    
    private fun updateCardStack() {
        cardAdapter.submitList(cards)
        updateActiveCardDisplay()
    }
    
    private fun updateActiveCardDisplay() {
        if (cards.isNotEmpty()) {
            val activeCard = cards[selectedCardIndex]
            binding.tvCardNumber.text = activeCard.displayNumber
            binding.tvCardExpiry.text = activeCard.displayExpiry
            binding.tvCardBalance.text = "Available Balance"
            binding.tvCardBank.text = activeCard.issuerBank
        }
    }
    
    private fun expandCards() {
        if (isCardsExpanded) {
            collapseCards()
        } else {
            val slideUpAnimation = AnimationUtils.loadAnimation(requireContext(), android.R.anim.slide_in_bottom)
            binding.rvCardStack.startAnimation(slideUpAnimation)
            binding.rvCardStack.visibility = View.VISIBLE
            isCardsExpanded = true
            binding.cardStackContainer.alpha = 0.5f
        }
    }
    
    private fun collapseCards() {
        val slideDownAnimation = AnimationUtils.loadAnimation(requireContext(), android.R.anim.slide_out_bottom)
        binding.rvCardStack.startAnimation(slideDownAnimation)
        binding.rvCardStack.visibility = View.GONE
        isCardsExpanded = false
        binding.cardStackContainer.alpha = 1f
    }
    
    private fun selectCard(index: Int, card: CreditCard) {
        selectedCardIndex = index
        updateActiveCardDisplay()
        collapseCards()
    }
    
    private fun showPaymentDialog() {
        val etAmount = android.widget.EditText(requireContext()).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "Amount"
        }
        
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Simulate NFC Payment")
            .setMessage("Enter payment amount:")
            .setView(etAmount)
            .setPositiveButton("Pay") { _, _ ->
                val amountText = etAmount.text.toString()
                val amount = amountText.toDoubleOrNull() ?: 0.0
                if (amount > 0) {
                    processNFCPayment(amount)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun processNFCPayment(amount: Double) {
        val card = cards[selectedCardIndex]
        
        // Create and save transaction
        lifecycleScope.launch {
            val transaction = Transaction(
                userId = "user123",
                transactionId = "NFC_${System.currentTimeMillis()}",
                type = TransactionType.PAYMENT,
                description = "NFC Payment - ${card.issuerBank}",
                amount = amount,
                timestamp = Date(),
                status = TransactionStatus.SUCCESS,
                paymentMethod = PaymentMethod.BANK,
                category = "NFC Payment",
                isDebit = true
            )
            
            try {
                Toast.makeText(
                    requireContext(),
                    "Payment of ₹${String.format("%.2f", amount)} successful using ${card.issuerBank}!",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Log.e("ScanPayFragment", "Error processing payment", e)
                Toast.makeText(requireContext(), "Payment failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindCameraPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindCameraPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build()
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(binding.cameraPreview.surfaceProvider)

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
            val mediaImage = imageProxy.image
            if (mediaImage != null && !qrCodeFound) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                scanQrCode(image, onComplete = { imageProxy.close() })
            } else {
                imageProxy.close()
            }
        }

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageAnalysis)
    }

    private fun scanQrCode(image: InputImage, onComplete: (() -> Unit)? = null) {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        val scanner = BarcodeScanning.getClient(options)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    qrCodeFound = true
                    val qrCodeValue = barcodes[0].rawValue
                    // Handle the QR code value (e.g., navigate to payment screen)
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "QR Code Scanned Successfully!", Toast.LENGTH_SHORT).show()
                        navigateToPayment(qrCodeValue)
                    }
                }
            }
            .addOnFailureListener {
                Log.e("ScanPayFragment", "Error scanning QR code", it)
            }
            .addOnCompleteListener {
                onComplete?.invoke()
            }
    }

    private fun navigateToPayment(qrCodeValue: String?) {
        // Parse QR code to extract payee details (simplified for demo)
        val payeeName = "Merchant Store" // In real app, parse from QR
        val payeeUpiId = qrCodeValue ?: "" // Use actual QR value

        // For now, just show a toast with the scanned data
        Toast.makeText(requireContext(), "Payee: $payeeName\nUPI ID: $payeeUpiId", Toast.LENGTH_LONG).show()

        // TODO: Navigate to payment screen when implemented
        // This would typically navigate to a payment confirmation screen
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        _binding = null
    }
}
