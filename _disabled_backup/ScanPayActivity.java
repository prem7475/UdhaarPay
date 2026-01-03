package com.example.udhaarpay;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScanPayActivity extends AppCompatActivity {

    private EditText etPayeeName, etAmount;
    private Button btnScanPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_pay);

        etPayeeName = findViewById(R.id.etPayeeName);
        etAmount = findViewById(R.id.etAmount);
        btnScanPay = findViewById(R.id.btnScanPay);

        btnScanPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String payee = etPayeeName.getText().toString();
                String amount = etAmount.getText().toString();

                if (!payee.isEmpty() && !amount.isEmpty()) {
                    // Save to SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("ScanPayPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("lastPayeeName", payee);
                    editor.putString("lastScanPayAmount", amount);
                    editor.apply();

                    // Record transaction in history
                    recordTransaction("Scan & Pay", payee, "-" + amount, "ScanPay");

                    Toast.makeText(ScanPayActivity.this, "Payment successful!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ScanPayActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
