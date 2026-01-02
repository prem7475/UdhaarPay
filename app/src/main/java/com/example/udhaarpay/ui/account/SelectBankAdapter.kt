package com.example.udhaarpay.ui.account

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.udhaarpay.R

class SelectBankAdapter(
    private val banks: List<String>,
    private val onBankSelected: (String) -> Unit
) : RecyclerView.Adapter<SelectBankAdapter.BankViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_select_bank, parent, false)
        return BankViewHolder(view)
    }

    override fun onBindViewHolder(holder: BankViewHolder, position: Int) {
        val bankName = banks[position]
        holder.bind(bankName)
        holder.itemView.setOnClickListener { onBankSelected(bankName) }
    }

    override fun getItemCount(): Int = banks.size

    class BankViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bankNameTextView: TextView = itemView.findViewById(R.id.bankName)

        fun bind(bankName: String) {
            bankNameTextView.text = bankName
        }
    }
}