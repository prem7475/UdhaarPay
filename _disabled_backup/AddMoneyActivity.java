package com.example.udhaarpay;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddMoneyActivity extends AppCompatActivity {

    private RadioGroup rgPaymentMethod;
    private EditText etAmount;
    private Button btnAddMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);

        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        etAmount = findViewById(R.id.etAmount);
        btnAddMoney = findViewById(R.id.btnAddMoney);

        btnAddMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = rgPaymentMethod.getCheckedRadioButtonId();
                String amount = etAmount.getText().toString();

                if (selectedId == -1) {
                    Toast.makeText(AddMoneyActivity.this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (amount.isEmpty()) {
                    Toast.makeText(AddMoneyActivity.this, "Please enter an amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton selectedRadioButton = findViewById(selectedId);
                String paymentMethod = selectedRadioButton.getText().toString();

                // Update wallet balance
                updateWalletBalance(amount);

                // Record transaction in history
                recordTransaction("Add Money", paymentMethod, "+" + amount, "AddMoney");

                // Save to SharedPreferences
                SharedPreferences prefs = getSharedPreferences("AddMoneyPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("lastPaymentMethod", paymentMethod);
                editor.putString("lastAddAmount", amount);
                editor.apply();

                Toast.makeText(AddMoneyActivity.this, "Money added to wallet successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void updateWalletBalance(String amount) {
        SharedPreferences prefs = getSharedPreferences("WalletPrefs", MODE_PRIVATE);
        float currentBalance = prefs.getFloat("walletBalance", 0.0f);
        float addAmount = Float.parseFloat(amount);
        float newBalance = currentBalance + addAmount;

        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat("walletBalance", newBalance);
        editor.apply();
    }

    private void recordTransaction(String type, String description, String amount, String category) {
        SharedPreferences prefs = getSharedPreferences("TransactionHistory", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String timestamp = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(new Date());
        String transactionKey = "transaction_" + System.currentTimeMillis();

        editor.putString(transactionKey + "_type", type);
        editor.putString(transactionKey + "_description", description);
        editor.putString(transactionKey + "_amount", amount);
        editor.putString(transactionKey + "_timestamp", timestamp);
        editor.putString(transactionKey + "_category", category);
        editor.apply();
    }
}
