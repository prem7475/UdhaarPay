package com.example.udhaarpay.ui.card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.udhaarpay.databinding.FragmentApplyCardBinding
import java.util.Calendar

class ApplyCardFragment : Fragment() {

    private var _binding: FragmentApplyCardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApplyCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSubmit.setOnClickListener {
            val pan = binding.editTextPan.text.toString()
            // Mock data based on PAN
            val name = "Prem Narayani"
            val dob = "01/01/1990"
            val cibil = 780

            binding.textViewName.text = "Name: $name"
            binding.textViewDob.text = "DOB: $dob"

            val dobParts = dob.split("/")
            val year = dobParts[2].toInt()
            val age = Calendar.getInstance().get(Calendar.YEAR) - year

            if (age < 21) {
                binding.textViewResult.text = "Ineligible: Age must be 21 or older"
                return@setOnClickListener
            }

            if (cibil > 750) {
                binding.textViewResult.text = "Congratulations! You are eligible for the UdhaarPay Platinum Card."
            } else if (cibil > 650) {
                binding.textViewResult.text = "Congratulations! You are eligible for the UdhaarPay Gold Card."
            } else {
                binding.textViewResult.text = "Sorry, you are not eligible for any card at this time."
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}