package com.example.udhaarpay.ui.debt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.udhaarpay.R
import com.example.udhaarpay.data.model.Debt
import com.example.udhaarpay.data.model.DebtCategory
import com.example.udhaarpay.data.model.DebtType
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@AndroidEntryPoint
class DebtFragment : Fragment() {

    private val viewModel: DebtViewModel by viewModels()
    private lateinit var debtRecyclerView: RecyclerView
    private lateinit var debtAdapter: DebtAdapter
    private lateinit var tvNetAmount: TextView
    private lateinit var tvTotalLent: TextView
    private lateinit var tvTotalBorrowed: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_debt, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI(view)
        observeViewModel()
    }

    private fun setupUI(view: View) {
        debtRecyclerView = view.findViewById(R.id.rvDebts)
        debtAdapter = DebtAdapter { debt ->
            showDebtActions(debt)
        }
        debtRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        debtRecyclerView.adapter = debtAdapter

        tvNetAmount = view.findViewById(R.id.tvNetAmount)
        tvTotalLent = view.findViewById(R.id.tvTotalLent)
        tvTotalBorrowed = view.findViewById(R.id.tvTotalBorrowed)

        view.findViewById<MaterialButton>(R.id.btnAddDebt).setOnClickListener {
            showAddDebtDialog()
        }

        view.findViewById<MaterialButton>(R.id.btnLentMoney).setOnClickListener {
            showAddDebtDialog(DebtType.LENT_TO)
        }

        view.findViewById<MaterialButton>(R.id.btnBorrowedMoney).setOnClickListener {
            showAddDebtDialog(DebtType.BORROWED_FROM)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pendingDebts.collect { debts ->
                debtAdapter.submitList(debts)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.debtSummary.collect { summary ->
                val df = DecimalFormat("#,##0.00")
                tvNetAmount.text = "₹${df.format(summary.netAmount)}"
                tvTotalLent.text = "Lent: ₹${df.format(summary.totalLent)}"
                tvTotalBorrowed.text = "Borrowed: ₹${df.format(summary.totalBorrowed)}"
            }
        }
    }

    private fun showAddDebtDialog(debtType: DebtType? = null) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_debt, null)
        
        val etName = view.findViewById<TextInputEditText>(R.id.etPersonName)
        val etAmount = view.findViewById<TextInputEditText>(R.id.etAmount)
        val etPhone = view.findViewById<TextInputEditText>(R.id.etPhone)
        val spType = view.findViewById<Spinner>(R.id.spDebtType)
        val spCategory = view.findViewById<Spinner>(R.id.spCategory)

        // Setup spinners
        val typeAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            listOf("Lent Money", "Borrowed Money")
        )
        spType.adapter = typeAdapter
        if (debtType != null) {
            spType.setSelection(if (debtType == DebtType.LENT_TO) 0 else 1)
        }

        val categoryAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            DebtCategory.values().map { it.name }
        )
        spCategory.adapter = categoryAdapter

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Debt Record")
            .setView(view)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString()
                val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
                val phone = etPhone.text.toString()
                val type = if (spType.selectedItemPosition == 0) DebtType.LENT_TO else DebtType.BORROWED_FROM
                val category = DebtCategory.values()[spCategory.selectedItemPosition]

                if (name.isNotBlank() && amount > 0) {
                    viewModel.addDebt(
                        personName = name,
                        amount = amount,
                        debtType = type,
                        category = category,
                        phoneNumber = phone
                    )
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDebtActions(debt: Debt) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Debt Options")
            .setItems(arrayOf("Mark as Settled", "Partial Settlement", "Delete", "Cancel")) { _, which ->
                when (which) {
                    0 -> viewModel.settleDebt(debt.id)
                    1 -> showPartialSettlementDialog(debt)
                    2 -> viewModel.deleteDebt(debt)
                }
            }
            .show()
    }

    private fun showPartialSettlementDialog(debt: Debt) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_settlement, null)
        val etAmount = view.findViewById<TextInputEditText>(R.id.etSettlementAmount)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Partial Settlement")
            .setView(view)
            .setPositiveButton("Settle") { _, _ ->
                val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
                if (amount > 0 && amount <= debt.remainingAmount) {
                    viewModel.partialSettlement(debt.id, amount)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    inner class DebtAdapter(
        private val onDebtClick: (Debt) -> Unit
    ) : RecyclerView.Adapter<DebtAdapter.DebtViewHolder>() {

        private var debts: List<Debt> = emptyList()

        fun submitList(newList: List<Debt>) {
            debts = newList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebtViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_debt, parent, false)
            return DebtViewHolder(view)
        }

        override fun onBindViewHolder(holder: DebtViewHolder, position: Int) {
            holder.bind(debts[position])
        }

        override fun getItemCount() = debts.size

        inner class DebtViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(debt: Debt) {
                itemView.apply {
                    setOnClickListener { onDebtClick(debt) }
                    // Bind debt details
                }
            }
        }
    }
}
