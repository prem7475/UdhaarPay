package com.example.udhaarpay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankSelectionActivity extends AppCompatActivity {

    private Spinner spinnerBanks;
    private EditText etAccountNumber, etIfscCode, etAccountHolderName;
    private Button btnSubmit;
    private String selectedBankName = "";
    private Map<String, String> bankIfscMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_selection);

        // Initialize views
        Toolbar toolbar = findViewById(R.id.toolbar);
        spinnerBanks = findViewById(R.id.spinnerBanks);
        etAccountNumber = findViewById(R.id.etAccountNumber);
        etIfscCode = findViewById(R.id.etIfscCode);
        etAccountHolderName = findViewById(R.id.etAccountHolderName);
        btnSubmit = findViewById(R.id.btnSubmit);

        // Setup toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Add Bank Account");
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize bank IFSC mapping
        initializeBankIfscMap();

        // Setup bank spinner
        List<String> banks = new ArrayList<>(bankIfscMap.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, banks);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBanks.setAdapter(adapter);

        // Handle bank selection
        spinnerBanks.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBankName = banks.get(position);
                String ifscCode = bankIfscMap.get(selectedBankName);
                etIfscCode.setText(ifscCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Setup submit button
        btnSubmit.setOnClickListener(v -> {
            if (validateInputs()) {
                // Navigate to UPI PIN setup activity
                Intent intent = new Intent(this, UpiPinSetupActivity.class);
                intent.putExtra("bank_name", selectedBankName);
                intent.putExtra("account_number", etAccountNumber.getText().toString());
                intent.putExtra("ifsc_code", etIfscCode.getText().toString());
                intent.putExtra("account_holder_name", etAccountHolderName.getText().toString());
                startActivity(intent);
                finish(); // Close this activity
            }
        });
    }

    private void initializeBankIfscMap() {
        bankIfscMap = new HashMap<>();
        bankIfscMap.put("State Bank of India", "SBIN0001234");
        bankIfscMap.put("HDFC Bank", "HDFC0001234");
        bankIfscMap.put("ICICI Bank", "ICIC0001234");
        bankIfscMap.put("Axis Bank", "UTIB0001234");
        bankIfscMap.put("Punjab National Bank", "PUNB0123456");
        bankIfscMap.put("Bank of Baroda", "BARB0ABCDEF");
        bankIfscMap.put("Canara Bank", "CNRB0001234");
        bankIfscMap.put("Union Bank of India", "UBIN0531234");
        bankIfscMap.put("IDBI Bank", "IBKL0001234");
        bankIfscMap.put("Kotak Mahindra Bank", "KKBK0001234");
        bankIfscMap.put("IndusInd Bank", "INDB0001234");
        bankIfscMap.put("Yes Bank", "YESB0001234");
        bankIfscMap.put("Federal Bank", "FDRL0001234");
        bankIfscMap.put("South Indian Bank", "SIBL0000123");
        bankIfscMap.put("RBL Bank", "RATN0000123");
    }

    private boolean validateInputs() {
        if (selectedBankName.isEmpty()) {
            Toast.makeText(this, "Please select a bank", Toast.LENGTH_SHORT).show();
            return false;
        }

        String accountNumber = etAccountNumber.getText().toString().trim();
        if (accountNumber.isEmpty()) {
            Toast.makeText(this, "Please enter account number", Toast.LENGTH_SHORT).show();
            return false;
        }

        String ifscCode = etIfscCode.getText().toString().trim();
        if (ifscCode.isEmpty()) {
            Toast.makeText(this, "Please enter IFSC code", Toast.LENGTH_SHORT).show();
            return false;
        }

        String accountHolderName = etAccountHolderName.getText().toString().trim();
        if (accountHolderName.isEmpty()) {
            Toast.makeText(this, "Please enter account holder name", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
