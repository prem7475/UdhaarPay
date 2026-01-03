package com.example.udhaarpay.ui.creditcards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.udhaarpay.databinding.FragmentAddCreditCardBinding
import com.example.udhaarpay.ui.viewmodel.CreditCardViewModel
import com.example.udhaarpay.utils.CardValidator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddCreditCardFragment : Fragment() {
    
    private val viewModel: CreditCardViewModel by viewModels()
    private var _binding: FragmentAddCreditCardBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddCreditCardBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupListeners()
        observeViewModel()
    }
    
    private fun setupListeners() {
        binding.btnAdd.setOnClickListener {
            addCard()
        }
        
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
        
        // Real-time card type detection
        binding.etCardNumber.setOnTextChangedListener { text ->
            val cardType = CardValidator.getCardType(text)
            binding.tvCardType.text = cardType.name
            
            // Visual feedback for RuPay
            val isRuPay = CardValidator.isValidRuPayCard(text)
            binding.tvValidation.apply {
                isVisible = true
                text = when {
                    text.isEmpty() -> ""
                    isRuPay -> "✓ Valid RuPay card"
                    else -> "✗ Only RuPay cards are accepted"
                }
                setTextColor(
                    if (isRuPay) 0xFF4CAF50.toInt()
                    else 0xFFE53935.toInt()
                )
            }
        }
        
        // Expiry validation
        binding.etExpiryMonth.setOnTextChangedListener { text ->
            if (text.isNotEmpty()) {
                val month = text.toIntOrNull() ?: 0
                if (month > 12) {
                    binding.etExpiryMonth.setText("12")
                }
            }
        }
    }
    
    private fun addCard() {
        val cardNumber = binding.etCardNumber.text.toString().trim()
        val cardholderName = binding.etCardholderName.text.toString().trim()
        val expiryMonth = binding.etExpiryMonth.text.toString().trim().toIntOrNull() ?: 0
        val expiryYear = binding.etExpiryYear.text.toString().trim().toIntOrNull() ?: 0
        val cvv = binding.etCVV.text.toString().trim()
        
        // Validation
        when {
            cardNumber.isEmpty() -> {
                binding.tilCardNumber.error = "Card number is required"
                return
            }
            !CardValidator.isValidRuPayCard(cardNumber) -> {
                binding.tilCardNumber.error = "Only RuPay cards are accepted"
                return
            }
            cardholderName.isEmpty() -> {
                binding.tilCardholderName.error = "Cardholder name is required"
                return
            }
            expiryMonth == 0 || expiryMonth > 12 -> {
                binding.tilExpiryMonth.error = "Invalid month"
                return
            }
            expiryYear == 0 -> {
                binding.tilExpiryYear.error = "Expiry year is required"
                return
            }
            !CardValidator.isValidExpiry(expiryMonth, expiryYear) -> {
                binding.tilExpiryYear.error = "Card has expired"
                return
            }
            !CardValidator.isValidCVV(cvv) -> {
                binding.tilCVV.error = "CVV must be 3-4 digits"
                return
            }
        }
        
        // Clear errors
        binding.tilCardNumber.error = null
        binding.tilCardholderName.error = null
        binding.tilExpiryMonth.error = null
        binding.tilExpiryYear.error = null
        binding.tilCVV.error = null
        
        // Add card
        viewModel.addCard(
            cardNumber = cardNumber,
            cardholderName = cardholderName,
            expiryMonth = expiryMonth,
            expiryYear = expiryYear,
            cvv = cvv
        )
    }
    
    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.isVisible = isLoading
                binding.btnAdd.isEnabled = !isLoading
                binding.btnCancel.isEnabled = !isLoading
            }
        }
        
        lifecycleScope.launch {
            viewModel.success.collect { message ->
                message?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                    viewModel.clearMessages()
                }
            }
        }
        
        lifecycleScope.launch {
            viewModel.error.collect { message ->
                message?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    viewModel.clearMessages()
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Extension function for EditText
private fun android.widget.EditText.setOnTextChangedListener(
    onTextChanged: (String) -> Unit
) {
    addTextChangedListener(object : android.text.TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged(s?.toString() ?: "")
        }
        override fun afterTextChanged(s: android.text.Editable?) {}
    })
}
