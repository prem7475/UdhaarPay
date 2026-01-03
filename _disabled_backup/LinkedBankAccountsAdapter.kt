package com.example.udhaarpay.ui.account

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.udhaarpay.R
import com.example.udhaarpay.data.model.BankAccount

class LinkedBankAccountsAdapter(
    private var accounts: List<BankAccount>,
    private val onDeleteClick: (BankAccount) -> Unit
) : RecyclerView.Adapter<LinkedBankAccountsAdapter.AccountViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_linked_bank_account, parent, false)
        return AccountViewHolder(view)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(accounts[position])
    }

    override fun getItemCount(): Int = accounts.size

    fun updateData(newAccounts: List<BankAccount>) {
        this.accounts = newAccounts
        notifyDataSetChanged()
    }

    inner class AccountViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bankName: TextView = itemView.findViewById(R.id.bankName)
        private val accountNumber: TextView = itemView.findViewById(R.id.accountNumber)
        private val deleteButton: ImageView = itemView.findViewById(R.id.delete_icon)

        fun bind(account: BankAccount) {
            bankName.text = account.bankName
            accountNumber.text = "Acct No: •••• ${account.accountNumber.takeLast(4)}"
            deleteButton.setOnClickListener { onDeleteClick(account) }
        }
    }
}