package com.example.udhaarpay.ui.creditcards

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.udhaarpay.data.model.CreditCard
import com.example.udhaarpay.databinding.ItemCreditCardBinding

class CreditCardsAdapter(
    private val onSetDefault: (Long) -> Unit,
    private val onDelete: (Long) -> Unit
) : ListAdapter<CreditCard, CreditCardsAdapter.CardViewHolder>(CardDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = ItemCreditCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CardViewHolder(binding, onSetDefault, onDelete)
    }
    
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class CardViewHolder(
        private val binding: ItemCreditCardBinding,
        private val onSetDefault: (Long) -> Unit,
        private val onDelete: (Long) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(card: CreditCard) {
            with(binding) {
                // Card display number
                tvCardNumber.text = card.displayNumber
                
                // Bank and cardholder
                tvBankName.text = card.issuerBank
                tvCardholderName.text = card.cardholderName
                
                // Expiry
                tvExpiry.text = "Expires: ${card.displayExpiry}"
                
                // Default badge
                chipDefault.isChecked = card.isDefault
                
                // Expired status
                if (card.isExpired) {
                    tvExpired.apply {
                        text = "EXPIRED"
                        setTextColor(0xFFE53935.toInt())
                        visibility = android.view.View.VISIBLE
                    }
                } else {
                    tvExpired.visibility = android.view.View.GONE
                }
                
                // Set default button
                btnSetDefault.setOnClickListener {
                    onSetDefault(card.id)
                }
                
                // Delete button
                btnDelete.setOnClickListener {
                    onDelete(card.id)
                }
                
                // Card color based on bank
                val backgroundColor = when {
                    card.issuerBank.contains("HDFC", ignoreCase = true) -> android.graphics.Color.parseColor("#003478")
                    card.issuerBank.contains("ICICI", ignoreCase = true) -> android.graphics.Color.parseColor("#FF6B35")
                    card.issuerBank.contains("Axis", ignoreCase = true) -> android.graphics.Color.parseColor("#0066CC")
                    card.issuerBank.contains("Kotak", ignoreCase = true) -> android.graphics.Color.parseColor("#6B4A8D")
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
