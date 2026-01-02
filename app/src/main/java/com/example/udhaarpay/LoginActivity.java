package com.example.udhaarpay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etMobileNumber;
    private MaterialButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Find the views from the XML layout
        etMobileNumber = findViewById(R.id.etMobileNumber);
        btnLogin = findViewById(R.id.btnLogin);

        // Add text watcher to change button color when 10 digits are entered
        etMobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 10) {
                    // Change button to white background when 10 digits are entered
                    btnLogin.setBackgroundTintList(getResources().getColorStateList(R.color.white));
                    btnLogin.setTextColor(getResources().getColor(R.color.app_blue_dark));
                } else {
                    // Reset to original colors
                    btnLogin.setBackgroundTintList(getResources().getColorStateList(R.color.app_blue_light));
                    btnLogin.setTextColor(getResources().getColor(R.color.app_blue_dark));
                }
            }
        });

        // Set a click listener on the button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This is a DUMMY login. No real authentication happens.
                String mobileNumber = etMobileNumber.getText().toString();

                if (mobileNumber.length() == 10) {
                    // If number is 10 digits, pretend login is successful
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Close the login activity so user can't go back
                } else {
                    // Show an error message
                    Toast.makeText(LoginActivity.this, "Please enter a valid 10-digit number", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
