package com.example.udhaarpay.ui.bills

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.udhaarpay.data.model.BillCategory
import com.example.udhaarpay.data.model.billProviders
import com.example.udhaarpay.databinding.FragmentBillPaymentsBinding
import com.example.udhaarpay.ui.viewmodel.BillPaymentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BillPaymentsFragment : Fragment() {

    private var _binding: FragmentBillPaymentsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BillPaymentViewModel by viewModels()
    private var selectedCategory: BillCategory? = null
    private var selectedProvider: String = ""
    private var selectedWebsite: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBillPaymentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setUserId(1)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // Category selection
        val categories = BillCategory.values().map { it.name }
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        binding.spinnerBillCategory.adapter = categoryAdapter

        binding.spinnerBillCategory.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCategory = BillCategory.values()[position]
                updateProviders()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }

        // Provider selection
        binding.btnPayBill.setOnClickListener {
            if (selectedCategory == null) {
                Toast.makeText(requireContext(), "Select bill category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = binding.etAmount.text.toString().toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(requireContext(), "Enter valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Open provider website
            if (selectedWebsite.isNotEmpty()) {
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(selectedWebsite))
                startActivity(intent)
            }

            // Save bill record
            viewModel.saveBillRecord(
                category = selectedCategory!!,
                provider = selectedProvider,
                amount = amount
            )
        }
    }

    private fun updateProviders() {
        selectedCategory?.let { category ->
            val providers = billProviders[category] ?: emptyList()
            val providerNames = providers.map { "${it.icon} ${it.name}" }
            val providerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, providerNames)
            binding.spinnerProvider.adapter = providerAdapter

            binding.spinnerProvider.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                    selectedProvider = providers[position].name
                    selectedWebsite = providers[position].website
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.success.collect { message ->
                message?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    viewModel.clearMessages()
                    binding.etAmount.text?.clear()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
