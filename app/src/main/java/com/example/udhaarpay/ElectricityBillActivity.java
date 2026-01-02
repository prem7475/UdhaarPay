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

public class ElectricityBillActivity extends AppCompatActivity {

    private EditText etConsumerNumber, etBillAmount;
    private Spinner spState, spBoard;
    private Button btnPayBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electricity_bill);

        etConsumerNumber = findViewById(R.id.etConsumerNumber);
        etBillAmount = findViewById(R.id.etBillAmount);
        spState = findViewById(R.id.spState);
        spBoard = findViewById(R.id.spBoard);
        btnPayBill = findViewById(R.id.btnPayBill);

        // Set up state spinner
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(this,
                R.array.states, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spState.setAdapter(stateAdapter);

        // Set up board spinner
        ArrayAdapter<CharSequence> boardAdapter = ArrayAdapter.createFromResource(this,
                R.array.electricity_boards, android.R.layout.simple_spinner_item);
        boardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBoard.setAdapter(boardAdapter);

        btnPayBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String consumerNumber = etConsumerNumber.getText().toString();
                String amount = etBillAmount.getText().toString();
                String state = spState.getSelectedItem().toString();
                String board = spBoard.getSelectedItem().toString();

                if (!consumerNumber.isEmpty() && !amount.isEmpty()) {
                    // Save to SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("ElectricityBillPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("lastConsumerNumber", consumerNumber);
                    editor.putString("lastBillAmount", amount);
                    editor.putString("lastState", state);
                    editor.putString("lastBoard", board);
                    editor.apply();

                    Toast.makeText(ElectricityBillActivity.this, "Electricity bill payment successful!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ElectricityBillActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
