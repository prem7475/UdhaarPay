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

public class DTHRechargeActivity extends AppCompatActivity {

    private EditText etSubscriberId, etRechargeAmount;
    private Spinner spOperator;
    private Button btnRecharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dth_recharge);

        etSubscriberId = findViewById(R.id.etSubscriberId);
        etRechargeAmount = findViewById(R.id.etRechargeAmount);
        spOperator = findViewById(R.id.spOperator);
        btnRecharge = findViewById(R.id.btnRecharge);

        // Set up operator spinner
        ArrayAdapter<CharSequence> operatorAdapter = ArrayAdapter.createFromResource(this,
                R.array.dth_operators, android.R.layout.simple_spinner_item);
        operatorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOperator.setAdapter(operatorAdapter);

        btnRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subscriberId = etSubscriberId.getText().toString();
                String amount = etRechargeAmount.getText().toString();
                String operator = spOperator.getSelectedItem().toString();

                if (!subscriberId.isEmpty() && !amount.isEmpty()) {
                    // Save to SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("DTHRechargePrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("lastSubscriberId", subscriberId);
                    editor.putString("lastRechargeAmount", amount);
                    editor.putString("lastOperator", operator);
                    editor.apply();

                    Toast.makeText(DTHRechargeActivity.this, "DTH recharge successful!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(DTHRechargeActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
