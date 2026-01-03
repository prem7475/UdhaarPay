package com.example.udhaarpay.ui.investments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.udhaarpay.data.model.InvestmentType
import com.example.udhaarpay.data.model.investmentBrokers
import com.example.udhaarpay.databinding.FragmentInvestmentsBinding
import com.example.udhaarpay.ui.viewmodel.InvestmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InvestmentsFragment : Fragment() {

    private var _binding: FragmentInvestmentsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InvestmentViewModel by viewModels()
    private var selectedType: InvestmentType? = null
    private var selectedBrokerWebsite: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInvestmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setUserId(1)
        setupUI()
    }

    private fun setupUI() {
        // Investment type selection
        val types = InvestmentType.values().map { it.name }
        val typeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        binding.spinnerInvestmentType.adapter = typeAdapter

        binding.spinnerInvestmentType.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedType = InvestmentType.values()[position]
                updateBrokers()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }

        // Broker selection
        binding.btnInvest.setOnClickListener {
            if (selectedType == null) {
                Toast.makeText(requireContext(), "Select investment type", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedBrokerWebsite.isEmpty()) {
                Toast.makeText(requireContext(), "Select broker", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Open broker website
            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(selectedBrokerWebsite))
            startActivity(intent)
        }
    }

    private fun updateBrokers() {
        selectedType?.let { type ->
            val brokers = investmentBrokers.filter { broker ->
                broker.types.contains(type)
            }

            val brokerNames = brokers.map { "${it.emoji} ${it.name}" }
            val brokerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, brokerNames)
            binding.spinnerBroker.adapter = brokerAdapter

            binding.spinnerBroker.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                    selectedBrokerWebsite = brokers[position].website
                    viewModel.saveInvestmentRecord(type, brokers[position].name)
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
