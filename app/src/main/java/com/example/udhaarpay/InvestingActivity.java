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

public class InvestingActivity extends AppCompatActivity {

    private EditText etInvestmentAmount, etDuration;
    private Spinner spInvestmentType;
    private Button btnInvest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investing);

        // Set up toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etInvestmentAmount = findViewById(R.id.etInvestmentAmount);
        etDuration = findViewById(R.id.etDuration);
        spInvestmentType = findViewById(R.id.spInvestmentType);
        btnInvest = findViewById(R.id.btnInvest);

        // Set up investment type spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.investment_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spInvestmentType.setAdapter(adapter);

        // Load last investment data
        loadLastInvestmentData();

        btnInvest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = etInvestmentAmount.getText().toString().trim();
                String duration = etDuration.getText().toString().trim();
                String type = spInvestmentType.getSelectedItem().toString();

                if (validateInputs(amount, duration)) {
                    // Save to SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("InvestPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("lastInvestmentAmount", amount);
                    editor.putString("lastInvestmentDuration", duration);
                    editor.putString("lastInvestmentType", type);
                    editor.apply();

                    Toast.makeText(InvestingActivity.this,
                        "Investment of ₹" + amount + " in " + type + " successful!",
                        Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    private void loadLastInvestmentData() {
        SharedPreferences prefs = getSharedPreferences("InvestPrefs", MODE_PRIVATE);
        String lastAmount = prefs.getString("lastInvestmentAmount", "");
        String lastDuration = prefs.getString("lastInvestmentDuration", "");
        String lastType = prefs.getString("lastInvestmentType", "");

        if (!lastAmount.isEmpty()) {
            etInvestmentAmount.setText(lastAmount);
        }
        if (!lastDuration.isEmpty()) {
            etDuration.setText(lastDuration);
        }

        // Set spinner selection if available
        if (!lastType.isEmpty()) {
            setSpinnerSelection(spInvestmentType, lastType);
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(value);
            if (position >= 0) {
                spinner.setSelection(position);
            }
        }
    }

    private boolean validateInputs(String amount, String duration) {
        if (amount.isEmpty()) {
            etInvestmentAmount.setError("Investment amount is required");
            etInvestmentAmount.requestFocus();
            return false;
        }

        try {
            double investmentAmount = Double.parseDouble(amount);
            if (investmentAmount <= 0) {
                etInvestmentAmount.setError("Enter valid amount");
                etInvestmentAmount.requestFocus();
                return false;
            }
            if (investmentAmount < 100) {
                etInvestmentAmount.setError("Minimum investment is ₹100");
                etInvestmentAmount.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etInvestmentAmount.setError("Enter valid amount");
            etInvestmentAmount.requestFocus();
            return false;
        }

        if (duration.isEmpty()) {
            etDuration.setError("Duration is required");
            etDuration.requestFocus();
            return false;
        }

        try {
            int investmentDuration = Integer.parseInt(duration);
            if (investmentDuration <= 0) {
                etDuration.setError("Enter valid duration");
                etDuration.requestFocus();
                return false;
            }
            if (investmentDuration > 30) {
                etDuration.setError("Maximum duration is 30 years");
                etDuration.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etDuration.setError("Enter valid duration in years");
            etDuration.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
