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

public class BusTicketBookingActivity extends AppCompatActivity {

    private EditText etFrom, etTo, etDate, etPassengers;
    private Spinner spBusOperator;
    private Button btnBookBusTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_ticket_booking);

        etFrom = findViewById(R.id.etFrom);
        etTo = findViewById(R.id.etTo);
        etDate = findViewById(R.id.etDate);
        etPassengers = findViewById(R.id.etPassengers);
        spBusOperator = findViewById(R.id.spBusOperator);
        btnBookBusTicket = findViewById(R.id.btnBookBusTicket);

        // Set up bus operator spinner
        ArrayAdapter<CharSequence> operatorAdapter = ArrayAdapter.createFromResource(this,
                R.array.bus_operators, android.R.layout.simple_spinner_item);
        operatorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBusOperator.setAdapter(operatorAdapter);

        btnBookBusTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String from = etFrom.getText().toString();
                String to = etTo.getText().toString();
                String date = etDate.getText().toString();
                String passengers = etPassengers.getText().toString();
                String operator = spBusOperator.getSelectedItem().toString();

                if (!from.isEmpty() && !to.isEmpty() && !date.isEmpty() && !passengers.isEmpty()) {
                    // Save to SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("BusTicketPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("lastBusFrom", from);
                    editor.putString("lastBusTo", to);
                    editor.putString("lastBusDate", date);
                    editor.putString("lastBusPassengers", passengers);
                    editor.putString("lastBusOperator", operator);
                    editor.apply();

                    Toast.makeText(BusTicketBookingActivity.this, "Bus ticket booked successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(BusTicketBookingActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
