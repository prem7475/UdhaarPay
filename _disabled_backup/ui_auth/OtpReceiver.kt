package com.example.udhaarpay.ui.auth

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

interface OtpListener {
    fun onOtpReceived(otp: String)
}

class OtpReceiver(private val listener: OtpListener) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            @Suppress("UNCHECKED_CAST", "DEPRECATION")
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

            when (status.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    @Suppress("UNCHECKED_CAST", "DEPRECATION")
                    val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    val otp = message.filter { it.isDigit() }.take(6)
                    listener.onOtpReceived(otp)
                }
                CommonStatusCodes.TIMEOUT -> {
                    // Handle timeout
                }
            }
        }
    }
}