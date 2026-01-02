package com.example.udhaarpay;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ExpenseManagerActivity extends AppCompatActivity {

    private EditText etExpenseAmount, etExpenseDescription;
    private Spinner spExpenseCategory;
    private Button btnAddExpense, btnClearExpenses;
    private ListView lvExpenses;
    private ArrayList<String> expensesList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_manager);

        etExpenseAmount = findViewById(R.id.etExpenseAmount);
        etExpenseDescription = findViewById(R.id.etExpenseDescription);
        spExpenseCategory = findViewById(R.id.spExpenseCategory);
        btnAddExpense = findViewById(R.id.btnAddExpense);
        btnClearExpenses = findViewById(R.id.btnClearExpenses);
        lvExpenses = findViewById(R.id.lvExpenses);

        expensesList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, expensesList);
        lvExpenses.setAdapter(adapter);

        // Set up category spinner
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.expense_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spExpenseCategory.setAdapter(categoryAdapter);

        loadExpenses();

        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = etExpenseAmount.getText().toString();
                String description = etExpenseDescription.getText().toString();
                String category = spExpenseCategory.getSelectedItem().toString();

                if (!amount.isEmpty() && !description.isEmpty()) {
                    String expense = category + ": ₹" + amount + " - " + description;
                    expensesList.add(expense);
                    adapter.notifyDataSetChanged();
                    saveExpenses();

                    etExpenseAmount.setText("");
                    etExpenseDescription.setText("");
                    Toast.makeText(ExpenseManagerActivity.this, "Expense added!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ExpenseManagerActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnClearExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expensesList.clear();
                adapter.notifyDataSetChanged();
                clearExpenses();
                Toast.makeText(ExpenseManagerActivity.this, "All expenses cleared!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadExpenses() {
        SharedPreferences prefs = getSharedPreferences("ExpensePrefs", MODE_PRIVATE);
        Set<String> expensesSet = prefs.getStringSet("expenses", new HashSet<String>());
        expensesList.addAll(expensesSet);
        adapter.notifyDataSetChanged();
    }

    private void saveExpenses() {
        SharedPreferences prefs = getSharedPreferences("ExpensePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> expensesSet = new HashSet<>(expensesList);
        editor.putStringSet("expenses", expensesSet);
        editor.apply();
    }

    private void clearExpenses() {
        SharedPreferences prefs = getSharedPreferences("ExpensePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("expenses");
        editor.apply();
    }
}
