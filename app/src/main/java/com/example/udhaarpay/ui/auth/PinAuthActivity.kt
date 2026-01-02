package com.example.udhaarpay.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.udhaarpay.MainActivity

// Minimal stub for PIN auth to satisfy references during a minimal build
class PinAuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // For minimal build, immediately proceed to MainActivity
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
