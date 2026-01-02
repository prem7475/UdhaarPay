package com.example.udhaarpay;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class CreditScoreActivity extends AppCompatActivity {

    private EditText etPAN, etAadhaar;
    private TextView tvCreditScore;
    private Button btnCheckScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_score);

        etPAN = findViewById(R.id.etPAN);
        etAadhaar = findViewById(R.id.etAadhaar);
        tvCreditScore = findViewById(R.id.tvCreditScore);
        btnCheckScore = findViewById(R.id.btnCheckScore);

        btnCheckScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pan = etPAN.getText().toString();
                String aadhaar = etAadhaar.getText().toString();

                if (!pan.isEmpty() && !aadhaar.isEmpty()) {
                    // Generate random credit score (300-900)
                    Random random = new Random();
                    int score = 300 + random.nextInt(601);

                    tvCreditScore.setText("Your Credit Score: " + score);

                    // Save to SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("CreditScorePrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("lastPAN", pan);
                    editor.putString("lastAadhaar", aadhaar);
                    editor.putInt("lastCreditScore", score);
                    editor.apply();

                    Toast.makeText(CreditScoreActivity.this, "Credit score checked successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CreditScoreActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
