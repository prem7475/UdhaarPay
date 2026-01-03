package com.example.udhaarpay.ui.cards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.udhaarpay.databinding.FragmentApplyForCardBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class ApplyForCardFragment : Fragment() {

    private var _binding: FragmentApplyForCardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApplyForCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dobEditText.setOnClickListener {
            showDatePicker()
        }

        binding.checkEligibilityButton.setOnClickListener {
            checkEligibility()
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date of Birth")
            .build()
        datePicker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = it
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.dobEditText.setText(sdf.format(calendar.time))
        }
        datePicker.show(childFragmentManager, "DATE_PICKER")
    }

    private fun checkEligibility() {
        val pan = binding.panCardEditText.text.toString()
        val dob = binding.dobEditText.text.toString()
        val cibil = binding.cibilScoreEditText.text.toString().toIntOrNull() ?: 750 // Default CIBIL

        if (!isValidPan(pan)) {
            binding.panCardInputLayout.error = "Invalid PAN Card Number"
            return
        } else {
            binding.panCardInputLayout.error = null
        }

        if (!isOfAge(dob)) {
            binding.dobInputLayout.error = "You must be at least 21 years old"
            return
        } else {
            binding.dobInputLayout.error = null
        }

        val result = when {
            cibil > 800 -> "Eligible for Premium Platinum Card"
            cibil > 750 -> "Eligible for Platinum Card"
            cibil > 700 -> "Eligible for Gold Card"
            cibil > 650 -> "Eligible for Silver Card"
            else -> "Not Eligible for any card at the moment"
        }
        binding.eligibilityResultText.text = result
    }

    private fun isValidPan(pan: String): Boolean {
        val pattern = "[A-Z]{5}[0-9]{4}[A-Z]{1}".toRegex()
        return pan.matches(pattern)
    }

    private fun isOfAge(dobString: String): Boolean {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dob = sdf.parse(dobString) ?: return false
        val dobCalendar = Calendar.getInstance()
        dobCalendar.time = dob
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age >= 21
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}