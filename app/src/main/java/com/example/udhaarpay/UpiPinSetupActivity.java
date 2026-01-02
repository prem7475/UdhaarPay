package com.example.udhaarpay;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

// Minimal stub for UPI PIN setup flow used in BankSelectionActivity
public class UpiPinSetupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // For minimal build, go straight to AccountDetailsActivity or MainActivity
        Intent intent = new Intent(this, AccountDetailsActivity.class);
        // pass along extras if any
        intent.putExtras(getIntent().getExtras() == null ? new Bundle() : getIntent().getExtras());
        startActivity(intent);
        finish();
    }
}
