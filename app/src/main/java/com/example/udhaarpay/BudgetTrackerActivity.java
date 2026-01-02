package com.example.udhaarpay;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BudgetTrackerActivity extends AppCompatActivity {

    private EditText etMonthlyBudget, etSpentAmount;
    private TextView tvRemainingBudget, tvBudgetStatus;
    private Button btnSetBudget, btnAddExpense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_tracker);

        etMonthlyBudget = findViewById(R.id.etMonthlyBudget);
        etSpentAmount = findViewById(R.id.etSpentAmount);
        tvRemainingBudget = findViewById(R.id.tvRemainingBudget);
        tvBudgetStatus = findViewById(R.id.tvBudgetStatus);
        btnSetBudget = findViewById(R.id.btnSetBudget);
        btnAddExpense = findViewById(R.id.btnAddExpense);

        loadBudgetData();

        btnSetBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String budgetStr = etMonthlyBudget.getText().toString();
                if (!budgetStr.isEmpty()) {
                    double budget = Double.parseDouble(budgetStr);
                    SharedPreferences prefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putFloat("monthlyBudget", (float) budget);
                    editor.putFloat("spentAmount", 0);
                    editor.apply();
                    updateBudgetDisplay(budget, 0);
                    Toast.makeText(BudgetTrackerActivity.this, "Budget set successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BudgetTrackerActivity.this, "Please enter a budget amount", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String expenseStr = etSpentAmount.getText().toString();
                if (!expenseStr.isEmpty()) {
                    SharedPreferences prefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE);
                    float currentSpent = prefs.getFloat("spentAmount", 0);
                    float budget = prefs.getFloat("monthlyBudget", 0);
                    float newSpent = currentSpent + Float.parseFloat(expenseStr);

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putFloat("spentAmount", newSpent);
                    editor.apply();

                    updateBudgetDisplay(budget, newSpent);
                    etSpentAmount.setText("");
                    Toast.makeText(BudgetTrackerActivity.this, "Expense added!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BudgetTrackerActivity.this, "Please enter an expense amount", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadBudgetData() {
        SharedPreferences prefs = getSharedPreferences("BudgetPrefs", MODE_PRIVATE);
        float budget = prefs.getFloat("monthlyBudget", 0);
        float spent = prefs.getFloat("spentAmount", 0);
        updateBudgetDisplay(budget, spent);
    }

    private void updateBudgetDisplay(double budget, double spent) {
        double remaining = budget - spent;
        tvRemainingBudget.setText("Remaining Budget: ₹" + String.format("%.2f", remaining));

        if (remaining < 0) {
            tvBudgetStatus.setText("Status: Over Budget!");
            tvBudgetStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else if (remaining < budget * 0.1) {
            tvBudgetStatus.setText("Status: Low Budget Warning");
            tvBudgetStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
        } else {
            tvBudgetStatus.setText("Status: On Track");
            tvBudgetStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }
}
