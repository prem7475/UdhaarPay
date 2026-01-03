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

public class BroadbandRechargeActivity extends AppCompatActivity {

    private EditText etAccountNumber, etRechargeAmount;
    private Spinner spProvider, spPlan;
    private Button btnRecharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadband_recharge);

        etAccountNumber = findViewById(R.id.etAccountNumber);
        etRechargeAmount = findViewById(R.id.etRechargeAmount);
        spProvider = findViewById(R.id.spProvider);
        spPlan = findViewById(R.id.spPlan);
        btnRecharge = findViewById(R.id.btnRecharge);

        // Set up provider spinner
        ArrayAdapter<CharSequence> providerAdapter = ArrayAdapter.createFromResource(this,
                R.array.broadband_providers, android.R.layout.simple_spinner_item);
        providerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProvider.setAdapter(providerAdapter);

        // Set up plan spinner
        ArrayAdapter<CharSequence> planAdapter = ArrayAdapter.createFromResource(this,
                R.array.broadband_plans, android.R.layout.simple_spinner_item);
        planAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPlan.setAdapter(planAdapter);

        btnRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accountNumber = etAccountNumber.getText().toString();
                String amount = etRechargeAmount.getText().toString();
                String provider = spProvider.getSelectedItem().toString();
                String plan = spPlan.getSelectedItem().toString();

                if (!accountNumber.isEmpty() && !amount.isEmpty()) {
                    // Save to SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("BroadbandRechargePrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("lastAccountNumber", accountNumber);
                    editor.putString("lastRechargeAmount", amount);
                    editor.putString("lastProvider", provider);
                    editor.putString("lastPlan", plan);
                    editor.apply();

                    Toast.makeText(BroadbandRechargeActivity.this, "Broadband recharge successful!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(BroadbandRechargeActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
