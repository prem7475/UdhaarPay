package com.example.udhaarpay.ui.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.udhaarpay.R
import com.example.udhaarpay.data.model.BankAccount

class LinkedAccountAdapter(
    private var accounts: List<BankAccount>,
    private val onAccountClick: (BankAccount) -> Unit
) : RecyclerView.Adapter<LinkedAccountAdapter.LinkedAccountViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkedAccountViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_linked_account, parent, false)
        return LinkedAccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: LinkedAccountViewHolder, position: Int) {
        val account = accounts[position]
        holder.bind(account)
        holder.itemView.setOnClickListener { onAccountClick(account) }
    }

    override fun getItemCount(): Int = accounts.size

    fun updateAccounts(newAccounts: List<BankAccount>) {
        accounts = newAccounts
        notifyDataSetChanged()
    }

    fun showBalance(account: BankAccount) {
        // In a real app, you'd update a specific item.
        // For simplicity, we'll just refresh the whole list.
        notifyDataSetChanged()
    }

    class LinkedAccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bankName: TextView = itemView.findViewById(R.id.bankName)
        private val accountBalance: TextView = itemView.findViewById(R.id.accountBalance)

        fun bind(account: BankAccount) {
            bankName.text = account.bankName
            // In a real app, you would fetch and display the balance.
            // For now, we'll just show the balance if a flag is set.
            accountBalance.visibility = if (account.showBalance) View.VISIBLE else View.GONE
        }
    }
}