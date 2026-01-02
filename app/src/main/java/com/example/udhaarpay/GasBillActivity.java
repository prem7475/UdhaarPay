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

public class GasBillActivity extends AppCompatActivity {

    private EditText etConsumerNumber, etBillAmount;
    private Spinner spState, spProvider;
    private Button btnPayBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas_bill);

        etConsumerNumber = findViewById(R.id.etConsumerNumber);
        etBillAmount = findViewById(R.id.etBillAmount);
        spState = findViewById(R.id.spState);
        spProvider = findViewById(R.id.spProvider);
        btnPayBill = findViewById(R.id.btnPayBill);

        // Set up state spinner
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(this,
                R.array.states, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spState.setAdapter(stateAdapter);

        // Set up provider spinner
        ArrayAdapter<CharSequence> providerAdapter = ArrayAdapter.createFromResource(this,
                R.array.gas_providers, android.R.layout.simple_spinner_item);
        providerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProvider.setAdapter(providerAdapter);

        btnPayBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String consumerNumber = etConsumerNumber.getText().toString();
                String amount = etBillAmount.getText().toString();
                String state = spState.getSelectedItem().toString();
                String provider = spProvider.getSelectedItem().toString();

                if (!consumerNumber.isEmpty() && !amount.isEmpty()) {
                    // Save to SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("GasBillPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("lastConsumerNumber", consumerNumber);
                    editor.putString("lastBillAmount", amount);
                    editor.putString("lastState", state);
                    editor.putString("lastProvider", provider);
                    editor.apply();

                    Toast.makeText(GasBillActivity.this, "Gas bill payment successful!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(GasBillActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
