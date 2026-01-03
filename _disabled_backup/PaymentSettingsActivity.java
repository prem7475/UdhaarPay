package com.example.udhaarpay;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class PaymentSettingsActivity extends AppCompatActivity {

    private RecyclerView rvLinkedAccounts;
    private Button btnAddBankAccount;
    private LinkedBankAccountsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_settings);

        // Initialize views
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        rvLinkedAccounts = findViewById(R.id.rvLinkedAccounts);
        btnAddBankAccount = findViewById(R.id.btnAddBankAccount);

        // Setup toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Payment Settings");
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Setup RecyclerView
        rvLinkedAccounts.setLayoutManager(new LinearLayoutManager(this));
        List<BankAccount> accounts = getLinkedAccounts();
        adapter = new LinkedBankAccountsAdapter(accounts, this::onAccountClick);
        rvLinkedAccounts.setAdapter(adapter);

        // Setup Add Bank Account button
        btnAddBankAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, BankSelectionActivity.class);
            startActivity(intent);
        });
    }

    private void onAccountClick(BankAccount account) {
        Intent intent = new Intent(this, AccountDetailsActivity.class);
        intent.putExtra("account_id", account.getId());
        intent.putExtra("bank_name", account.getBankName());
        intent.putExtra("account_number", account.getAccountNumber());
        intent.putExtra("is_default", account.isDefault());
        startActivity(intent);
    }

    private List<BankAccount> getLinkedAccounts() {
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new BankAccount("1", "State Bank of India", "****1234", true));
        accounts.add(new BankAccount("2", "HDFC Bank", "****5678", false));
        accounts.add(new BankAccount("3", "ICICI Bank", "****9012", false));
        return accounts;
    }

    // Bank Account model class
    public static class BankAccount {
        private String id;
        private String bankName;
        private String accountNumber;
        private boolean isDefault;

        public BankAccount(String id, String bankName, String accountNumber, boolean isDefault) {
            this.id = id;
            this.bankName = bankName;
            this.accountNumber = accountNumber;
            this.isDefault = isDefault;
        }

        public String getId() { return id; }
        public String getBankName() { return bankName; }
        public String getAccountNumber() { return accountNumber; }
        public boolean isDefault() { return isDefault; }
    }
}
