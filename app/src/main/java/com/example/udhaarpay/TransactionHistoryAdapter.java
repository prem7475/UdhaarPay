package com.example.udhaarpay;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder> {
    
    private List<TransactionItem> transactions;
    
    public TransactionHistoryAdapter(List<TransactionItem> transactions) {
        this.transactions = transactions;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_history, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TransactionItem transaction = transactions.get(position);
        holder.tvDescription.setText(transaction.getDescription());
        holder.tvAmount.setText(transaction.getAmount());
        holder.tvTime.setText(transaction.getTime());
        
        if (transaction.isDebit()) {
            holder.tvAmount.setTextColor(0xFFFF0000); // Red for debit
        } else {
            holder.tvAmount.setTextColor(0xFF00AA00); // Green for credit
        }
    }
    
    @Override
    public int getItemCount() {
        return transactions.size();
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDescription;
        private TextView tvAmount;
        private TextView tvTime;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
    
    public static class TransactionItem {
        private String description;
        private String amount;
        private String time;
        private boolean isDebit;
        
        public TransactionItem(String description, String amount, String time, boolean isDebit) {
            this.description = description;
            this.amount = amount;
            this.time = time;
            this.isDebit = isDebit;
        }
        
        public String getDescription() {
            return description;
        }
        
        public String getAmount() {
            return amount;
        }
        
        public String getTime() {
            return time;
        }
        
        public boolean isDebit() {
            return isDebit;
        }
    }
}
