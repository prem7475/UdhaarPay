package com.example.udhaarpay.ui.insurance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.udhaarpay.data.model.InsuranceCategory
import com.example.udhaarpay.data.model.insuranceProviders
import com.example.udhaarpay.databinding.FragmentInsuranceBinding
import com.example.udhaarpay.ui.viewmodel.InsuranceViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InsuranceFragment : Fragment() {

    private var _binding: FragmentInsuranceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InsuranceViewModel by viewModels()
    private var selectedCategory: InsuranceCategory? = null
    private var selectedProviderWebsite: String = ""
    private var selectedProviderName: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInsuranceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setUserId(1)
        setupUI()
    }

    private fun setupUI() {
        // Category selection
        val categories = InsuranceCategory.values().map { it.name }
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        binding.spinnerInsuranceCategory.adapter = categoryAdapter

        binding.spinnerInsuranceCategory.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedCategory = InsuranceCategory.values()[position]
                updateProviders()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }

        // Provider selection and navigation
        binding.btnGetInsurance.setOnClickListener {
            if (selectedCategory == null) {
                Toast.makeText(requireContext(), "Select insurance category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedProviderWebsite.isEmpty()) {
                Toast.makeText(requireContext(), "Select provider", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Open provider website
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(selectedProviderWebsite))
            startActivity(intent)

            // Save insurance record
            viewModel.saveInsuranceRecord(selectedCategory!!, selectedProviderName)
        }
    }

    private fun updateProviders() {
        selectedCategory?.let { category ->
            val providers = insuranceProviders.filter { it.category == category }
            val providerNames = providers.map { "${it.emoji} ${it.name}" }
            val providerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, providerNames)
            binding.spinnerProvider.adapter = providerAdapter

            binding.spinnerProvider.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                    selectedProviderWebsite = providers[position].website
                    selectedProviderName = providers[position].name
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
