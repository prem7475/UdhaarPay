package com.example.udhaarpay.ui.transaction

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.udhaarpay.R
import com.example.udhaarpay.ui.dashboard.Transaction

class TransactionHistoryAdapter(
    private var transactions: List<Transaction>
) : RecyclerView.Adapter<TransactionHistoryAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int = transactions.size

    fun updateTransactions(filteredTransactions: List<Transaction>) {
        transactions = filteredTransactions
        notifyDataSetChanged()
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val transactionName: TextView = itemView.findViewById(R.id.tvTransactionDescription)
        private val transactionDate: TextView = itemView.findViewById(R.id.tvTransactionTime)
        private val transactionAmount: TextView = itemView.findViewById(R.id.tvTransactionAmount)

        fun bind(transaction: Transaction) {
            transactionName.text = transaction.name
            transactionDate.text = transaction.date
            transactionAmount.text = transaction.amount
            transactionAmount.setTextColor(if (transaction.isCredit) Color.GREEN else Color.RED)
        }
    }
}