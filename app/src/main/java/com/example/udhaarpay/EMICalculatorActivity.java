package com.example.udhaarpay;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EMICalculatorActivity extends AppCompatActivity {

    private EditText etPrincipal, etRate, etTenure;
    private TextView tvEMIResult;
    private Button btnCalculateEMI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emi_calculator);

        etPrincipal = findViewById(R.id.etPrincipal);
        etRate = findViewById(R.id.etRate);
        etTenure = findViewById(R.id.etTenure);
        tvEMIResult = findViewById(R.id.tvEMIResult);
        btnCalculateEMI = findViewById(R.id.btnCalculateEMI);

        btnCalculateEMI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String principalStr = etPrincipal.getText().toString();
                String rateStr = etRate.getText().toString();
                String tenureStr = etTenure.getText().toString();

                if (!principalStr.isEmpty() && !rateStr.isEmpty() && !tenureStr.isEmpty()) {
                    double principal = Double.parseDouble(principalStr);
                    double rate = Double.parseDouble(rateStr) / 100 / 12; // Monthly rate
                    int tenure = Integer.parseInt(tenureStr) * 12; // In months

                    double emi = (principal * rate * Math.pow(1 + rate, tenure)) / (Math.pow(1 + rate, tenure) - 1);

                    tvEMIResult.setText("Monthly EMI: ₹" + String.format("%.2f", emi));

                    // Save to SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("EMIPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("lastPrincipal", principalStr);
                    editor.putString("lastRate", rateStr);
                    editor.putString("lastTenure", tenureStr);
                    editor.putString("lastEMI", String.format("%.2f", emi));
                    editor.apply();

                    Toast.makeText(EMICalculatorActivity.this, "EMI calculated successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EMICalculatorActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
