package com.example.udhaarpay.ui.accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.udhaarpay.data.model.BankAccount
import com.example.udhaarpay.databinding.FragmentAddBankAccountBinding
import com.example.udhaarpay.ui.SharedViewModel
import com.example.udhaarpay.ui.SharedViewModelFactory
import com.google.android.material.snackbar.Snackbar

class AddBankAccountFragment : Fragment() {

    private var _binding: FragmentAddBankAccountBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels {
        SharedViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBankAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveButton.setOnClickListener {
            val bankName = binding.bankNameEditText.text.toString()
            val accountNumber = binding.accountNumberEditText.text.toString()
            val ifscCode = binding.ifscCodeEditText.text.toString()
            val accountHolderName = "" // TODO: Get from user input or context
            val userId = "" // TODO: Get from user/session

            if (bankName.isNotEmpty() && accountNumber.isNotEmpty() && ifscCode.isNotEmpty()) {
                val bankAccount = BankAccount(
                    bankName = bankName,
                    accountNumber = accountNumber,
                    ifscCode = ifscCode,
                    accountHolderName = accountHolderName,
                    userId = userId
                )
                sharedViewModel.addBankAccount(bankAccount)
                Snackbar.make(view, "Bank Account Added!", Snackbar.LENGTH_LONG).show()
                findNavController().navigateUp()
            } else {
                Snackbar.make(view, "Please fill all fields", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}