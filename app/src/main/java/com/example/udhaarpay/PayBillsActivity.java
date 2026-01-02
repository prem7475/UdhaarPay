package com.example.udhaarpay;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PayBillsActivity extends AppCompatActivity {

    private Spinner spBillType;
    private EditText etBillNumber, etAmount;
    private Button btnPayBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_bills);

        spBillType = findViewById(R.id.spBillType);
        etBillNumber = findViewById(R.id.etBillNumber);
        etAmount = findViewById(R.id.etAmount);
        btnPayBill = findViewById(R.id.btnPayBill);

        // Set up bill type spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.bill_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBillType.setAdapter(adapter);

        btnPayBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String billType = spBillType.getSelectedItem().toString();
                String billNumber = etBillNumber.getText().toString();
                String amount = etAmount.getText().toString();

                if (!billNumber.isEmpty() && !amount.isEmpty()) {
                    // Deduct from wallet balance
                    updateWalletBalance(amount);

                    // Record transaction in history
                    recordTransaction("Bill Payment", billType + " - " + billNumber, "-" + amount, "PayBills");

                    // Save to SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("PayBillsPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("lastBillType", billType);
                    editor.putString("lastBillNumber", billNumber);
                    editor.putString("lastBillAmount", amount);
                    editor.apply();

                    Toast.makeText(PayBillsActivity.this, "Bill payment successful!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(PayBillsActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateWalletBalance(String amount) {
        SharedPreferences prefs = getSharedPreferences("WalletPrefs", MODE_PRIVATE);
        float currentBalance = prefs.getFloat("walletBalance", 0.0f);
        float deductAmount = Float.parseFloat(amount);
        float newBalance = currentBalance - deductAmount;

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

    private void showUpiPinDialog(Runnable onSuccess) {
        UpiPinBottomSheetFragment pinFragment = new UpiPinBottomSheetFragment();
        pinFragment.setOnPinVerifiedListener(onSuccess);
        pinFragment.show(getSupportFragmentManager(), "UpiPinDialog");
    }
}
