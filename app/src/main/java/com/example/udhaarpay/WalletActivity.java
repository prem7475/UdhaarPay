package com.example.udhaarpay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WalletActivity extends AppCompatActivity {

    private TextView tvWalletBalance;
    private Button btnAddMoney, btnSendMoney;
    private RecyclerView rvRecentTransactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        tvWalletBalance = findViewById(R.id.tvWalletBalance);
        btnAddMoney = findViewById(R.id.btnAddMoney);
        btnSendMoney = findViewById(R.id.btnSendMoney);
        rvRecentTransactions = findViewById(R.id.rvRecentTransactions);

        // Load wallet balance
        SharedPreferences prefs = getSharedPreferences("WalletPrefs", MODE_PRIVATE);
        float balance = prefs.getFloat("walletBalance", 1204.50f);
        tvWalletBalance.setText("₹" + String.format("%.2f", balance));

        // Set up RecyclerView for recent transactions
        rvRecentTransactions.setLayoutManager(new LinearLayoutManager(this));
        // In a real app, you would load actual transaction data
        // For now, we'll just show a placeholder

        btnAddMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WalletActivity.this, AddMoneyActivity.class));
            }
        });

        btnSendMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WalletActivity.this, SendMoneyActivity.class));
            }
        });
    }
}
