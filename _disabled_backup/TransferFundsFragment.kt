package com.example.udhaarpay.ui.transfers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.udhaarpay.databinding.FragmentTransferFundsBinding
import com.example.udhaarpay.ui.viewmodel.TransferViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransferFundsFragment : Fragment() {

    private var _binding: FragmentTransferFundsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransferViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferFundsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setUserId(1)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.btnBankToBank.setOnClickListener {
            showTransferDialog("Bank", "Bank")
        }

        binding.btnBankToWallet.setOnClickListener {
            showTransferDialog("Bank", "Wallet")
        }

        binding.btnWalletToBank.setOnClickListener {
            showTransferDialog("Wallet", "Bank")
        }
    }

    private fun showTransferDialog(from: String, to: String) {
        val etAmount = com.google.android.material.textfield.TextInputEditText(requireContext()).apply {
            hint = "Enter amount"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Transfer $from to $to")
            .setView(etAmount)
            .setPositiveButton("Transfer") { _, _ ->
                val amount = etAmount.text.toString().toDoubleOrNull()
                if (amount != null && amount > 0) {
                    viewModel.transferFunds(from, to, amount)
                } else {
                    Toast.makeText(requireContext(), "Enter valid amount", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.success.collect { message ->
                message?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
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
