package com.example.udhaarpay;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class TicketBookingActivity extends AppCompatActivity {

    private EditText etFrom, etTo, etDate, etPassengers;
    private Spinner spTicketType;
    private Button btnBookTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_booking);

        // Set up toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        etFrom = findViewById(R.id.etFrom);
        etTo = findViewById(R.id.etTo);
        etDate = findViewById(R.id.etDate);
        etPassengers = findViewById(R.id.etPassengers);
        spTicketType = findViewById(R.id.spTicketType);
        btnBookTicket = findViewById(R.id.btnBookTicket);

        // Set up ticket type spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.ticket_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTicketType.setAdapter(adapter);

        // Load last booking data
        loadLastBookingData();

        // Set up date picker for date field
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        btnBookTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String from = etFrom.getText().toString().trim();
                String to = etTo.getText().toString().trim();
                String date = etDate.getText().toString().trim();
                String passengers = etPassengers.getText().toString().trim();
                String type = spTicketType.getSelectedItem().toString();

                if (validateInputs(from, to, date, passengers)) {
                    // Save to SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("TicketPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("lastTicketFrom", from);
                    editor.putString("lastTicketTo", to);
                    editor.putString("lastTicketDate", date);
                    editor.putString("lastTicketPassengers", passengers);
                    editor.putString("lastTicketType", type);
                    editor.apply();

                    Toast.makeText(TicketBookingActivity.this,
                        "Ticket booked successfully for " + passengers + " passenger(s)!",
                        Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, selectedYear, selectedMonth, selectedDay) -> {
                String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                etDate.setText(selectedDate);
            },
            year, month, day
        );

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void loadLastBookingData() {
        SharedPreferences prefs = getSharedPreferences("TicketPrefs", MODE_PRIVATE);
        String lastFrom = prefs.getString("lastTicketFrom", "");
        String lastTo = prefs.getString("lastTicketTo", "");
        String lastDate = prefs.getString("lastTicketDate", "");
        String lastPassengers = prefs.getString("lastTicketPassengers", "");
        String lastType = prefs.getString("lastTicketType", "");

        if (!lastFrom.isEmpty()) {
            etFrom.setText(lastFrom);
        }
        if (!lastTo.isEmpty()) {
            etTo.setText(lastTo);
        }
        if (!lastDate.isEmpty()) {
            etDate.setText(lastDate);
        }
        if (!lastPassengers.isEmpty()) {
            etPassengers.setText(lastPassengers);
        }

        // Set spinner selection if available
        if (!lastType.isEmpty()) {
            setSpinnerSelection(spTicketType, lastType);
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

    private boolean validateInputs(String from, String to, String date, String passengers) {
        if (from.isEmpty()) {
            etFrom.setError("Departure city is required");
            etFrom.requestFocus();
            return false;
        }

        if (to.isEmpty()) {
            etTo.setError("Destination city is required");
            etTo.requestFocus();
            return false;
        }

        if (from.equalsIgnoreCase(to)) {
            etTo.setError("Departure and destination cannot be the same");
            etTo.requestFocus();
            return false;
        }

        if (date.isEmpty()) {
            etDate.setError("Travel date is required");
            etDate.requestFocus();
            return false;
        }

        if (passengers.isEmpty()) {
            etPassengers.setError("Number of passengers is required");
            etPassengers.requestFocus();
            return false;
        }

        try {
            int numPassengers = Integer.parseInt(passengers);
            if (numPassengers <= 0) {
                etPassengers.setError("Enter valid number of passengers");
                etPassengers.requestFocus();
                return false;
            }
            if (numPassengers > 6) {
                etPassengers.setError("Maximum 6 passengers allowed");
                etPassengers.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            etPassengers.setError("Enter valid number");
            etPassengers.requestFocus();
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
