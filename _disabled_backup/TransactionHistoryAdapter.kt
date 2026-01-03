package com.example.udhaarpay.ui.transaction

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.udhaarpay.data.model.Transaction
import com.example.udhaarpay.databinding.ItemTransactionHistoryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionHistoryAdapter : ListAdapter<Transaction, TransactionHistoryAdapter.TransactionViewHolder>(
    TransactionDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TransactionViewHolder(private val binding: ItemTransactionHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        private val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

        fun bind(transaction: Transaction) {
            with(binding) {
                // Description/Category
                tvTransactionDescription.text = transaction.description
                tvTransactionCategory.text = transaction.category

                // Amount with sign
                tvTransactionAmount.text = transaction.displayAmount
                tvTransactionAmount.setTextColor(
                    if (transaction.isDebit) Color.parseColor("#E53935") else Color.parseColor("#43A047")
                )

                // Date and time
                tvTransactionDate.text = dateFormatter.format(transaction.timestamp)
                tvTransactionTime.text = timeFormatter.format(transaction.timestamp)

                // Status badge
                tvTransactionStatus.text = transaction.status.name
                tvTransactionStatus.setBackgroundColor(
                    when {
                        transaction.isSuccessful -> Color.parseColor("#4CAF50")
                        transaction.isPending -> Color.parseColor("#FFA726")
                        transaction.isFailed -> Color.parseColor("#EF5350")
                        else -> Color.parseColor("#9E9E9E")
                    }
                )

                // Payment method/Source
                tvTransactionSource.text = transaction.paymentMethod.name

                // Merchant name if available
                if (!transaction.merchantName.isNullOrEmpty()) {
                    tvMerchantName.text = transaction.merchantName
                } else {
                    tvMerchantName.text = transaction.senderName ?: transaction.receiverName ?: "-"
                }
            }
        }
    }

    private class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction) =
            oldItem == newItem
    }
}