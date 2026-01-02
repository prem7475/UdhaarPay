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

public class MobileRechargeActivity extends AppCompatActivity {

    private EditText etMobileNumber, etRechargeAmount;
    private Spinner spOperator, spCircle;
    private Button btnRecharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_recharge);

        // Set up toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etMobileNumber = findViewById(R.id.etMobileNumber);
        etRechargeAmount = findViewById(R.id.etRechargeAmount);
        spOperator = findViewById(R.id.spOperator);
        spCircle = findViewById(R.id.spCircle);
        btnRecharge = findViewById(R.id.btnRecharge);

        // Load last used data
        loadLastRechargeData();

        // Set up operator spinner
        ArrayAdapter<CharSequence> operatorAdapter = ArrayAdapter.createFromResource(this,
                R.array.mobile_operators, android.R.layout.simple_spinner_item);
        operatorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOperator.setAdapter(operatorAdapter);

        // Set up circle spinner
        ArrayAdapter<CharSequence> circleAdapter = ArrayAdapter.createFromResource(this,
                R.array.circles, android.R.layout.simple_spinner_item);
        circleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCircle.setAdapter(circleAdapter);

        btnRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = etMobileNumber.getText().toString().trim();
                String amount = etRechargeAmount.getText().toString().trim();
                String operator = spOperator.getSelectedItem().toString();
                String circle = spCircle.getSelectedItem().toString();

                if (validateInputs(mobile, amount)) {
                    // Save to SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("MobileRechargePrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("lastMobileNumber", mobile);
                    editor.putString("lastRechargeAmount", amount);
                    editor.putString("lastOperator", operator);
                    editor.putString("lastCircle", circle);
                    editor.apply();

                    // Show success message and close activity
                    Toast.makeText(MobileRechargeActivity.this,
                        "Mobile recharge of ₹" + amount + " successful for " + mobile,
                        Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    private void loadLastRechargeData() {
        SharedPreferences prefs = getSharedPreferences("MobileRechargePrefs", MODE_PRIVATE);
        String lastMobile = prefs.getString("lastMobileNumber", "");
        String lastAmount = prefs.getString("lastRechargeAmount", "");
        String lastOperator = prefs.getString("lastOperator", "");
        String lastCircle = prefs.getString("lastCircle", "");

        if (!lastMobile.isEmpty()) {
            etMobileNumber.setText(lastMobile);
        }
        if (!lastAmount.isEmpty()) {
            etRechargeAmount.setText(lastAmount);
        }

        // Set spinner selections if available
        if (!lastOperator.isEmpty()) {
            setSpinnerSelection(spOperator, lastOperator);
        }
        if (!lastCircle.isEmpty()) {
            setSpinnerSelection(spCircle, lastCircle);
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

    private boolean validateInputs(String mobile, String amount) {
        if (mobile.isEmpty()) {
            etMobileNumber.setError("Mobile number is required");
            etMobileNumber.requestFocus();
            return false;
        }

        if (mobile.length() != 10) {
            etMobileNumber.setError("Enter valid 10-digit mobile number");
            etMobileNumber.requestFocus();
            return false;
        }

        if (amount.isEmpty()) {
            etRechargeAmount.setError("Recharge amount is required");
            etRechargeAmount.requestFocus();
            return false;
        }

        try {
            double rechargeAmount = Double.parseDouble(amount);
            if (rechargeAmount <= 0) {
                etRechargeAmount.setError("Enter valid amount");
                etRechargeAmount.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etRechargeAmount.setError("Enter valid amount");
            etRechargeAmount.requestFocus();
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
