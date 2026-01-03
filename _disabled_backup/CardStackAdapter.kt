package com.example.udhaarpay.ui.scan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.udhaarpay.data.model.CreditCard
import com.example.udhaarpay.databinding.ItemCardStackBinding

class CardStackAdapter(
    private val onCardSelected: (index: Int, card: CreditCard) -> Unit
) : ListAdapter<CreditCard, CardStackAdapter.CardViewHolder>(CardDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = ItemCardStackBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CardViewHolder(binding, onCardSelected)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    class CardViewHolder(
        private val binding: ItemCardStackBinding,
        private val onCardSelected: (index: Int, card: CreditCard) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(card: CreditCard, position: Int) {
            with(binding) {
                // Card number (last 4 digits)
                tvCardNumber.text = card.displayNumber
                
                // Bank name
                tvBankName.text = card.issuerBank
                
                // Cardholder name
                tvCardholderName.text = card.cardholderName
                
                // Expiry
                tvExpiry.text = card.displayExpiry
                
                // Balance placeholder
                tvBalance.text = "RuPay Card"
                
                // Click listener
                cardView.setOnClickListener {
                    onCardSelected(position, card)
                }
                
                // Card gradient based on bank
                val backgroundColor = when {
                    card.issuerBank.contains("HDFC", ignoreCase = true) -> android.graphics.Color.parseColor("#003478")
                    card.issuerBank.contains("ICICI", ignoreCase = true) -> android.graphics.Color.parseColor("#FF6B35")
                    card.issuerBank.contains("Axis", ignoreCase = true) -> android.graphics.Color.parseColor("#0066CC")
                    else -> android.graphics.Color.parseColor("#2E7D32")
                }
                cardView.setBackgroundColor(backgroundColor)
            }
        }
    }

    private class CardDiffCallback : DiffUtil.ItemCallback<CreditCard>() {
        override fun areItemsTheSame(oldItem: CreditCard, newItem: CreditCard) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CreditCard, newItem: CreditCard) =
            oldItem == newItem
    }
}
