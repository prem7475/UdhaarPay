package com.example.udhaarpay;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.udhaarpay.data.model.Transaction;
import com.example.udhaarpay.data.model.TransactionType;
import com.example.udhaarpay.data.model.TransactionStatus;

public class SendMoneyActivity extends AppCompatActivity {
    
    private EditText etRecipientName;
    private EditText etUpiId;
    private EditText etAmount;
    private Button btnSendMoney;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);
        
        initializeViews();
        loadLastSendMoneyData();
        setupClickListeners();
    }
    
    private void initializeViews() {
        etRecipientName = findViewById(R.id.etRecipientName);
        etUpiId = findViewById(R.id.etUpiId);
        etAmount = findViewById(R.id.etAmount);
        btnSendMoney = findViewById(R.id.btnSendMoney);
    }
    
    private void setupClickListeners() {
        btnSendMoney.setOnClickListener(v -> handleSendMoney());
    }
    
    private void handleSendMoney() {
        String recipient = etRecipientName.getText().toString().trim();
        String upiId = etUpiId.getText().toString().trim();
        String amount = etAmount.getText().toString().trim();
        
        if (!validateInputs(recipient, upiId, amount)) {
            return;
        }
        
        processPayment(recipient, upiId, amount);
    }
    
    private boolean validateInputs(String recipient, String upiId, String amount) {
        if (recipient.isEmpty()) {
            etRecipientName.setError("Recipient name is required");
            etRecipientName.requestFocus();
            return false;
        }
        
        if (upiId.isEmpty()) {
            etUpiId.setError("UPI ID is required");
            etUpiId.requestFocus();
            return false;
        }
        
        if (!upiId.contains("@")) {
            etUpiId.setError("Invalid UPI ID format");
            etUpiId.requestFocus();
            return false;
        }
        
        if (amount.isEmpty()) {
            etAmount.setError("Amount is required");
            etAmount.requestFocus();
            return false;
        }
        
        try {
            double sendAmount = Double.parseDouble(amount);
            if (sendAmount <= 0) {
                etAmount.setError("Enter valid amount");
                etAmount.requestFocus();
                return false;
            }
            if (sendAmount > 10000) {
                etAmount.setError("Maximum send limit is ₹10,000");
                etAmount.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etAmount.setError("Enter valid amount");
            etAmount.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void loadLastSendMoneyData() {
        SharedPreferences prefs = getSharedPreferences("SendMoneyPrefs", MODE_PRIVATE);
        String lastRecipient = prefs.getString("lastRecipient", "");
        String lastUpiId = prefs.getString("lastUpiId", "");
        String lastAmount = prefs.getString("lastAmount", "");
        
        if (!lastRecipient.isEmpty()) {
            etRecipientName.setText(lastRecipient);
        }
        if (!lastUpiId.isEmpty()) {
            etUpiId.setText(lastUpiId);
        }
        if (!lastAmount.isEmpty()) {
            etAmount.setText(lastAmount);
        }
    }
    
    private void processPayment(String recipient, String upiId, String amount) {
        try {
            double sendAmount = Double.parseDouble(amount);
            
            updateWalletBalance(amount);
            
            Transaction transaction = new Transaction(
                0,
                "",
                "",
                TransactionType.SEND,
                null,
                "Sent to " + recipient,
                sendAmount,
                0,
                sendAmount,
                new java.util.Date(),
                TransactionStatus.COMPLETED,
                null,
                null,
                null,
                null,
                recipient,
                upiId,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                null,
                true,
                null,
                null,
                new java.util.Date(),
                new java.util.Date()
            );
            
            new Thread(() -> {
                // saveToDatabse(transaction);
            }).start();
            
            SharedPreferences prefs = getSharedPreferences("SendMoneyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("lastRecipient", recipient);
            editor.putString("lastUpiId", upiId);
            editor.putString("lastAmount", amount);
            editor.apply();
            
            runOnUiThread(() -> {
                Toast.makeText(this, "Money sent successfully!", Toast.LENGTH_SHORT).show();
                finish();
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void updateWalletBalance(String amount) {
        // Placeholder for wallet update logic
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
