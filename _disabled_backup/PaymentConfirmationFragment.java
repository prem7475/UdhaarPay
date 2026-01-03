package com.example.udhaarpay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ExperimentalGetImage;
import androidx.fragment.app.Fragment;

import com.example.udhaarpay.data.model.Transaction;
import com.example.udhaarpay.data.model.TransactionType;

@androidx.camera.core.ExperimentalGetImage

public class PaymentConfirmationFragment extends Fragment {

    private TextView tvPaymentAmount, tvPaymentTo, tvPaymentFrom, tvTransactionId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_confirmation, container, false);

        // Initialize views
        tvPaymentAmount = view.findViewById(R.id.tvPaymentAmount);
        tvPaymentTo = view.findViewById(R.id.tvPaymentTo);
        tvPaymentFrom = view.findViewById(R.id.tvPaymentFrom);
        tvTransactionId = view.findViewById(R.id.tvTransactionId);

        // Get payment details from arguments
        Bundle args = getArguments();
        if (args != null) {
            String amount = args.getString("amount", "0.00");
            String payeeName = args.getString("payeeName", "Unknown");
            String account = args.getString("account", "Unknown");

            tvPaymentAmount.setText("₹ " + amount);
            tvPaymentTo.setText("Paid to: " + payeeName);
            tvPaymentFrom.setText("From: " + account);

            // Generate a random transaction ID
            String transactionId = "TXN" + System.currentTimeMillis();
            tvTransactionId.setText("Transaction ID: " + transactionId);
        }

        // Set up button click listeners
        view.findViewById(R.id.btnPayAgain).setOnClickListener(v -> {
            // Navigate back to scan/pay
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new com.example.udhaarpay.ui.scan.ScanPayFragment());
            }
        });

        view.findViewById(R.id.btnCashback).setOnClickListener(v -> {
            // Navigate to cashback fragment
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new CashbackFragment());
            }
        });

        view.findViewById(R.id.btnHome).setOnClickListener(v -> {
            // Navigate to home fragment
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new HomeFragment());
            }
        });

        return view;
    }

    private void saveTransaction(String amount, String payeeName, String account, String transactionId) {
        try {
            double transactionAmount = Double.parseDouble(amount);

            Transaction transaction = new Transaction(
                System.currentTimeMillis(),
                TransactionType.SEND_MONEY,
                "Payment to " + payeeName,
                transactionAmount,
                new Date(),
                "Payment",
                true
            );

            new Thread(() -> {
                // Note: In a real app, you'd inject the repository
                // For now, we'll assume it's available
            }).start();

        } catch (Exception e) {
            // Handle error
        }
    }
}
