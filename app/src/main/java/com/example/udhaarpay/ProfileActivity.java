package com.example.udhaarpay;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.io.FileOutputStream;

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivUserQrCode;
    private Button btnShareQr, btnDownloadQr;
    private TextView tvUserName, tvUserPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsingToolbar);
        ivUserQrCode = findViewById(R.id.ivUserQrCode);
        btnShareQr = findViewById(R.id.btnShareQr);
        btnDownloadQr = findViewById(R.id.btnDownloadQr);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserPhone = findViewById(R.id.tvUserPhone);

        // Setup toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Set user data
        tvUserName.setText("Aarav Sharma");
        tvUserPhone.setText("+91 98765 43210");

        // Generate and display QR code (placeholder for now)
        // In a real app, this would generate a QR code for the user's UPI ID
        ivUserQrCode.setImageResource(R.drawable.ic_qr_code_scanner);

        // Setup QR code buttons
        btnShareQr.setOnClickListener(v -> shareQrCode());
        btnDownloadQr.setOnClickListener(v -> downloadQrCode());

        // Setup menu items
        findViewById(R.id.layoutPaymentSettings).setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentSettingsActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.layoutHelpSupport).setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatSupportActivity.class);
            startActivity(intent);
        });
    }

    private void shareQrCode() {
        // Create a share intent for the QR code
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "My UPI ID: aarav@paytm\nScan to pay me!");
        startActivity(Intent.createChooser(shareIntent, "Share UPI ID"));
    }

    private void downloadQrCode() {
        // In a real app, this would save the QR code bitmap to device storage
        Toast.makeText(this, "QR Code downloaded successfully!", Toast.LENGTH_SHORT).show();
    }
}
