package com.example.udhaarpay

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.udhaarpay.databinding.FragmentBankBinding
import com.example.udhaarpay.ui.SharedViewModel
import com.example.udhaarpay.ui.SharedViewModelFactory
import com.example.udhaarpay.ui.account.LinkedBankAccountsAdapter
import com.example.udhaarpay.data.model.BankAccount
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// Renamed to avoid DEX clash with the old Java file
class BankFragmentKt : Fragment() {

    private var _binding: FragmentBankBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels {
        SharedViewModelFactory(requireActivity().application)
    }
    private lateinit var bankAccountsAdapter: LinkedBankAccountsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBankBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.btnAddBank.setOnClickListener {
            findNavController().navigate(R.id.action_nav_bank_to_selectBankFragment)
        }

        sharedViewModel.bankAccounts.observe(viewLifecycleOwner) { accounts ->
            bankAccountsAdapter.updateData(accounts)
        }
    }

    private fun setupRecyclerView() {
        bankAccountsAdapter = LinkedBankAccountsAdapter(emptyList()) { bankAccount ->
            // Show confirmation dialog before deleting
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Bank Account")
                .setMessage("Are you sure you want to delete this bank account?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete") { _, _ ->
                    sharedViewModel.deleteBankAccount(bankAccount)
                }
                .show()
        }
        binding.linkedAccountsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bankAccountsAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}