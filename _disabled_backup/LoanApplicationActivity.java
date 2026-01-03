package com.example.udhaarpay;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoanApplicationActivity extends AppCompatActivity {

    private EditText etLoanAmount, etLoanTenure, etMonthlyIncome, etEmploymentType;
    private Spinner spLoanType;
    private Button btnApplyLoan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_application);

        etLoanAmount = findViewById(R.id.etLoanAmount);
        etLoanTenure = findViewById(R.id.etLoanTenure);
        etMonthlyIncome = findViewById(R.id.etMonthlyIncome);
        etEmploymentType = findViewById(R.id.etEmploymentType);
        spLoanType = findViewById(R.id.spLoanType);
        btnApplyLoan = findViewById(R.id.btnApplyLoan);

        btnApplyLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loanAmount = etLoanAmount.getText().toString();
                String tenure = etLoanTenure.getText().toString();
                String income = etMonthlyIncome.getText().toString();
                String employment = etEmploymentType.getText().toString();
                String loanType = spLoanType.getSelectedItem().toString();

                if (!loanAmount.isEmpty() && !tenure.isEmpty() && !income.isEmpty() && !employment.isEmpty()) {
                    // Save to SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("LoanPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("lastLoanAmount", loanAmount);
                    editor.putString("lastLoanTenure", tenure);
                    editor.putString("lastMonthlyIncome", income);
                    editor.putString("lastEmploymentType", employment);
                    editor.putString("lastLoanType", loanType);
                    editor.apply();

                    Toast.makeText(LoanApplicationActivity.this, "Loan application submitted successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(LoanApplicationActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
