package com.example.udhaarpay.ui.auth

import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.udhaarpay.R
import com.example.udhaarpay.databinding.FragmentOtpBinding
import com.google.android.gms.auth.api.phone.SmsRetriever

class OtpFragment : Fragment(), OtpListener {

    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!

    private lateinit var otpReceiver: OtpReceiver

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startSmsListener()

        binding.buttonVerifyOtp.setOnClickListener {
            // Logic to verify OTP would go here
            findNavController().navigate(R.id.action_otp_fragment_to_profile_setup_fragment)
        }
    }

    private fun startSmsListener() {
        try {
            otpReceiver = OtpReceiver(this)
            val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
            ContextCompat.registerReceiver(requireActivity(), otpReceiver, intentFilter, ContextCompat.RECEIVER_NOT_EXPORTED)

            val client = SmsRetriever.getClient(requireActivity())
            val task = client.startSmsRetriever()
            task.addOnSuccessListener {
                // Successfully started retriever
            }
            task.addOnFailureListener {
                // Failed to start retriever
                Log.e("OtpFragment", "Failed to start SMS retriever", it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onOtpReceived(otp: String) {
        binding.editTextOtp.setText(otp)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::otpReceiver.isInitialized) {
            requireActivity().unregisterReceiver(otpReceiver)
        }
        _binding = null
    }
}