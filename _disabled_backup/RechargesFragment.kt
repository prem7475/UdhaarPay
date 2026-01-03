package com.example.udhaarpay.ui.recharges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.udhaarpay.data.model.RechargeOperator
import com.example.udhaarpay.data.model.mockRechargePlans
import com.example.udhaarpay.databinding.FragmentRechargesBinding
import com.example.udhaarpay.ui.viewmodel.RechargeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RechargesFragment : Fragment() {

    private var _binding: FragmentRechargesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RechargeViewModel by viewModels()
    private var selectedOperator: RechargeOperator? = null
    private var selectedAmount: Double = 0.0
    private var selectedPlanId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRechargesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setUserId(1)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        // Operator selection
        val operators = RechargeOperator.values().map { it.name }
        val operatorAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, operators)
        binding.spinnerOperator.adapter = operatorAdapter

        binding.spinnerOperator.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedOperator = RechargeOperator.values()[position]
                updatePlans()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        }

        // Plan selection
        binding.btnProceed.setOnClickListener {
            val phoneNumber = binding.etPhoneNumber.text.toString().trim()

            if (phoneNumber.isEmpty()) {
                Toast.makeText(requireContext(), "Enter phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedOperator == null) {
                Toast.makeText(requireContext(), "Select operator", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Open operator website in WebView
            openOperatorWebsite()

            // Save recharge record
            viewModel.saveRecharge(
                phoneNumber = phoneNumber,
                operator = selectedOperator!!,
                planAmount = selectedAmount,
                planId = selectedPlanId
            )
        }
    }

    private fun updatePlans() {
        selectedOperator?.let { operator ->
            val plans = mockRechargePlans[operator] ?: emptyList()
            val planDescriptions = plans.map { "${it.amount}₹ - ${it.data} Data - ${it.validity}" }
            val planAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, planDescriptions)
            binding.spinnerPlan.adapter = planAdapter

            binding.spinnerPlan.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                    selectedAmount = plans[position].amount
                    selectedPlanId = plans[position].planId
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
            }
        }
    }

    private fun openOperatorWebsite() {
        val url = when (selectedOperator) {
            RechargeOperator.JIO -> "https://www.jio.com"
            RechargeOperator.AIRTEL -> "https://www.airtel.in"
            RechargeOperator.VODAFONE -> "https://www.vodafoneidea.in"
            RechargeOperator.IDEA -> "https://www.vodafoneidea.in"
            else -> "https://www.jio.com"
        }

        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
        startActivity(intent)
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
