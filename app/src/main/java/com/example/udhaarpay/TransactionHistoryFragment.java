package com.example.udhaarpay;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionHistoryFragment extends Fragment {

    private RecyclerView rvTransactions;
    private TransactionHistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction_history, container, false);

        rvTransactions = view.findViewById(R.id.rvTransactions);
        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TransactionHistoryAdapter(new ArrayList<>());
        rvTransactions.setAdapter(adapter);

        loadTransactions();

        return view;
    }

    private void loadTransactions() {
        // For now, load dummy data. In a real app, this would use ViewModel and Repository
        List<TransactionHistoryAdapter.TransactionItem> transactions = new ArrayList<>();
        transactions.add(new TransactionHistoryAdapter.TransactionItem("Payment to Merchant A", "₹ 250.00", "2 hours ago", true));
        transactions.add(new TransactionHistoryAdapter.TransactionItem("Money Added", "₹ 500.00", "1 day ago", false));
        transactions.add(new TransactionHistoryAdapter.TransactionItem("Bill Payment - Electricity", "₹ 1,200.00", "2 days ago", true));
        transactions.add(new TransactionHistoryAdapter.TransactionItem("Refund Received", "₹ 150.00", "3 days ago", false));
        transactions.add(new TransactionHistoryAdapter.TransactionItem("Payment to Merchant B", "₹ 75.00", "5 days ago", true));
        transactions.add(new TransactionHistoryAdapter.TransactionItem("Cashback Earned", "₹ 25.00", "1 week ago", false));

        adapter.updateTransactions(transactions);
    }
}
