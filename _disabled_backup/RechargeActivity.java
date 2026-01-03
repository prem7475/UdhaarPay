package com.example.udhaarpay;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RechargeActivity extends AppCompatActivity {

    private EditText etAmount, etPhoneNumber;
    private Spinner spRechargeType;
    private Button btnRecharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);

        etAmount = findViewById(R.id.etAmount);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        spRechargeType = findViewById(R.id.spRechargeType);
        btnRecharge = findViewById(R.id.btnRecharge);

        btnRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = etAmount.getText().toString();
                String phone = etPhoneNumber.getText().toString();
                String type = spRechargeType.getSelectedItem().toString();

                if (!amount.isEmpty() && !phone.isEmpty()) {
                    // Save to SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("RechargePrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("lastRechargeAmount", amount);
                    editor.putString("lastRechargePhone", phone);
                    editor.putString("lastRechargeType", type);
                    editor.apply();

                    Toast.makeText(RechargeActivity.this, "Recharge successful!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RechargeActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
