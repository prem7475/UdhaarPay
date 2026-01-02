package com.example.udhaarpay.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.udhaarpay.data.model.BankAccount
import com.example.udhaarpay.databinding.FragmentSelectBankBinding
import com.example.udhaarpay.ui.SharedViewModel
import com.example.udhaarpay.ui.SharedViewModelFactory

class SelectBankFragment : Fragment() {

    private var _binding: FragmentSelectBankBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels {
        SharedViewModelFactory(requireActivity().application)
    }

    // A dummy list of banks for demonstration
    private val bankList = listOf(
        "State Bank of India",
        "HDFC Bank",
        "ICICI Bank",
        "Axis Bank",
        "Kotak Mahindra Bank",
        "Punjab National Bank",
        "Bank of Baroda"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectBankBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectBankAdapter = SelectBankAdapter(bankList) { selectedBank ->
            // Create a dummy bank account object
            val accountHolderName = "" // TODO: Get from user input or context
            val userId = "" // TODO: Get from user/session
            val newAccount = BankAccount(
                bankName = selectedBank,
                accountNumber = (1000..9999).random().toString(), // Dummy account number
                ifscCode = "IFSC" + (100..999).random().toString(), // Dummy IFSC
                accountHolderName = accountHolderName,
                userId = userId
            )
            // Add the new account using the ViewModel
            sharedViewModel.addBankAccount(newAccount)
            // Go back to the previous screen
            findNavController().popBackStack()
        }

        binding.banksRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = selectBankAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}