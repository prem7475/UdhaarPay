package com.example.udhaarpay;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CreditCardApplicationActivity extends AppCompatActivity {

    private EditText etAnnualIncome, etEmploymentDetails, etAddress;
    private Spinner spCardType;
    private Button btnApplyCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card_application);

        etAnnualIncome = findViewById(R.id.etAnnualIncome);
        etEmploymentDetails = findViewById(R.id.etEmploymentDetails);
        etAddress = findViewById(R.id.etAddress);
        spCardType = findViewById(R.id.spCardType);
        btnApplyCard = findViewById(R.id.btnApplyCard);

        btnApplyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String income = etAnnualIncome.getText().toString();
                String employment = etEmploymentDetails.getText().toString();
                String address = etAddress.getText().toString();
                String cardType = spCardType.getSelectedItem().toString();

                if (!income.isEmpty() && !employment.isEmpty() && !address.isEmpty()) {
                    // Save to SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("CardPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("lastAnnualIncome", income);
                    editor.putString("lastEmploymentDetails", employment);
                    editor.putString("lastAddress", address);
                    editor.putString("lastCardType", cardType);
                    editor.apply();

                    Toast.makeText(CreditCardApplicationActivity.this, "Credit card application submitted successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CreditCardApplicationActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
