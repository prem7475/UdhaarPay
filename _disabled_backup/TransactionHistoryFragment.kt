package com.example.udhaarpay.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.udhaarpay.R
import com.example.udhaarpay.databinding.FragmentTransactionHistoryBinding
import com.example.udhaarpay.ui.dashboard.RecentTransactionAdapter
import com.example.udhaarpay.ui.dashboard.Transaction

class TransactionHistoryFragment : Fragment() {

    private var _binding: FragmentTransactionHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionAdapter: RecentTransactionAdapter
    private val allTransactions = listOf(
        Transaction("John Doe", "12 Jan 2024", "- ₹500", false),
        Transaction("Jane Smith", "11 Jan 2024", "+ ₹1200", true),
        Transaction("Grocery Store", "10 Jan 2024", "- ₹150", false),
        Transaction("Credit Card Bill", "09 Jan 2024", "- ₹5000", false),
        Transaction("Salary", "01 Jan 2024", "+ ₹50000", true)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupFilterChips()
    }

    private fun setupRecyclerView() {
        transactionAdapter = RecentTransactionAdapter(allTransactions)
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransactions.adapter = transactionAdapter
    }

    private fun setupFilterChips() {
        // Using the correct setOnCheckedStateChangeListener with explicit types
        binding.filterChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val checkedId = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            val filteredList = when (checkedId) {
                R.id.chipAll -> allTransactions
                R.id.chipByCard -> allTransactions.filter { it.name.contains("Card") }
                R.id.chipByAccount -> allTransactions.filter { !it.name.contains("Card") }
                R.id.chipByDate -> allTransactions // Placeholder for date filtering
                else -> allTransactions
            }
            transactionAdapter.updateTransactions(filteredList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}