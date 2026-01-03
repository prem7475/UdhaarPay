package com.example.udhaarpay;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DematAccountActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPhone, etPanNumber, etAadhaarNumber, etAddress;
    private Spinner spinnerBank, spinnerAccountType;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demat_account);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Open Demat Account");

        // Initialize views
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPanNumber = findViewById(R.id.etPanNumber);
        etAadhaarNumber = findViewById(R.id.etAadhaarNumber);
        etAddress = findViewById(R.id.etAddress);
        spinnerBank = findViewById(R.id.spinnerBank);
        spinnerAccountType = findViewById(R.id.spinnerAccountType);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Setup spinners
        setupSpinners();

        // Submit button click listener
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    submitDematApplication();
                }
            }
        });
    }

    private void setupSpinners() {
        // Bank spinner
        String[] banks = {"Select Bank", "HDFC Bank", "ICICI Bank", "SBI", "Axis Bank", "Kotak Mahindra", "IDFC First", "Yes Bank"};
        ArrayAdapter<String> bankAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, banks);
        bankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBank.setAdapter(bankAdapter);

        // Account type spinner
        String[] accountTypes = {"Select Account Type", "Individual", "Joint", "NRI", "Corporate"};
        ArrayAdapter<String> accountTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, accountTypes);
        accountTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccountType.setAdapter(accountTypeAdapter);
    }

    private boolean validateForm() {
        if (etFullName.getText().toString().trim().isEmpty()) {
            etFullName.setError("Full name is required");
            return false;
        }

        if (etEmail.getText().toString().trim().isEmpty()) {
            etEmail.setError("Email is required");
            return false;
        }

        if (etPhone.getText().toString().trim().isEmpty()) {
            etPhone.setError("Phone number is required");
            return false;
        }

        if (etPanNumber.getText().toString().trim().isEmpty()) {
            etPanNumber.setError("PAN number is required");
            return false;
        }

        if (etAadhaarNumber.getText().toString().trim().isEmpty()) {
            etAadhaarNumber.setError("Aadhaar number is required");
            return false;
        }

        if (etAddress.getText().toString().trim().isEmpty()) {
            etAddress.setError("Address is required");
            return false;
        }

        if (spinnerBank.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a bank", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (spinnerAccountType.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select account type", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void submitDematApplication() {
        // Here you would typically send the data to your backend
        Toast.makeText(this, "Demat account application submitted successfully!", Toast.LENGTH_LONG).show();
        finish(); // Close activity
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
