package com.example.udhaarpay;

import android.os.Bundle;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AccountDetailsActivity extends AppCompatActivity {

    private TextView tvBankName, tvAccountNumber, tvIfscCode, tvAccountHolderName, tvAccountStatus;
    private Button btnSetAsDefault, btnRemoveAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);

        // Initialize views
        Toolbar toolbar = findViewById(R.id.toolbar);
        tvBankName = findViewById(R.id.tvBankName);
        tvAccountNumber = findViewById(R.id.tvAccountNumber);
        tvIfscCode = findViewById(R.id.tvIfscCode);
        tvAccountHolderName = findViewById(R.id.tvAccountHolderName);
        tvAccountStatus = findViewById(R.id.tvAccountStatus);
        btnSetAsDefault = findViewById(R.id.btnSetAsDefault);
        btnRemoveAccount = findViewById(R.id.btnRemoveAccount);

        // Setup toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Account Details");
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Get data from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String bankName = extras.getString("bank_name", "Unknown Bank");
            String accountNumber = extras.getString("account_number", "N/A");
            String ifscCode = extras.getString("ifsc_code", "N/A");
            String accountHolderName = extras.getString("account_holder_name", "N/A");
            boolean isDefault = extras.getBoolean("is_default", false);
            boolean isNewAccount = extras.getBoolean("is_new_account", false);

            // Display account details
            tvBankName.setText(bankName);
            tvAccountNumber.setText(accountNumber);
            tvIfscCode.setText(ifscCode);
            tvAccountHolderName.setText(accountHolderName);

            if (isNewAccount) {
                // Save new bank account
                saveBankAccount(bankName, accountNumber, ifscCode, accountHolderName);
                tvAccountStatus.setText("Account Linked Successfully");
                tvAccountStatus.setTextColor(getColor(R.color.success_green));
                btnSetAsDefault.setText("Set as Default");
                btnSetAsDefault.setEnabled(true);
            } else if (isDefault) {
                tvAccountStatus.setText("Default Account");
                tvAccountStatus.setTextColor(getColor(R.color.success_green));
                btnSetAsDefault.setText("Default Account");
                btnSetAsDefault.setEnabled(false);
            } else {
                tvAccountStatus.setText("Linked Account");
                tvAccountStatus.setTextColor(getColor(R.color.text_secondary));
                btnSetAsDefault.setEnabled(true);
            }
        }

        // Setup button listeners
        btnSetAsDefault.setOnClickListener(v -> {
            // TODO: Implement set as default functionality
            Toast.makeText(this, "Set as default account", Toast.LENGTH_SHORT).show();
            tvAccountStatus.setText("Default Account");
            tvAccountStatus.setTextColor(getColor(R.color.success_green));
            btnSetAsDefault.setText("Default Account");
            btnSetAsDefault.setEnabled(false);
        });

        btnRemoveAccount.setOnClickListener(v -> {
            // TODO: Implement remove account functionality
            Toast.makeText(this, "Remove account functionality coming soon", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveBankAccount(String bankName, String accountNumber, String ifscCode, String accountHolderName) {
        SharedPreferences prefs = getSharedPreferences("BankAccounts", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String accountId = "account_" + System.currentTimeMillis();
        editor.putString(accountId + "_bankName", bankName);
        editor.putString(accountId + "_accountNumber", accountNumber);
        editor.putString(accountId + "_ifscCode", ifscCode);
        editor.putString(accountId + "_accountHolderName", accountHolderName);
        editor.putBoolean(accountId + "_isDefault", false);
        editor.apply();
    }
}
