package com.example.udhaarpay.ui.transaction

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.udhaarpay.R
import com.example.udhaarpay.databinding.FragmentTransactionHistoryBinding
import com.example.udhaarpay.ui.dashboard.Transaction

class TransactionHistoryFragment : Fragment() {

    private var _binding: FragmentTransactionHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var transactionAdapter: TransactionHistoryAdapter
    private var allTransactions = mutableListOf<Transaction>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadTransactionsFromPrefs()
        setupRecyclerView()
        setupFilterChips()
    }

    private fun loadTransactionsFromPrefs() {
        val prefs = requireContext().getSharedPreferences("TransactionHistory", android.content.Context.MODE_PRIVATE)
        val allPrefs = prefs.all

        allTransactions.clear()

        // Group transaction keys by timestamp
        val transactionKeys = allPrefs.keys.filter { it.startsWith("transaction_") }
            .groupBy { it.substringBefore("_type") }

        for ((key, fields) in transactionKeys) {
            val type = prefs.getString("${key}_type", "")
            val description = prefs.getString("${key}_description", "")
            val amount = prefs.getString("${key}_amount", "")
            val timestamp = prefs.getString("${key}_timestamp", "")
            val category = prefs.getString("${key}_category", "")

            if (!type.isNullOrEmpty() && !description.isNullOrEmpty() && !amount.isNullOrEmpty() && !timestamp.isNullOrEmpty()) {
                val isCredit = amount.startsWith("+")
                allTransactions.add(Transaction(description, timestamp, amount, isCredit))
            }
        }

        // Sort by timestamp (most recent first)
        allTransactions.sortByDescending { it.date }
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionHistoryAdapter(allTransactions)
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransactions.adapter = transactionAdapter
    }

    private fun setupFilterChips() {
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
